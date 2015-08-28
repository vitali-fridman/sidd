// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import com.vontu.profileindex.IndexException;
import com.vontu.profiles.monitor.notification.LoadFailure;
import com.vontu.profiles.monitor.notification.LoadEvent;
import com.vontu.profiles.monitor.notification.LoadOperation;
import java.util.logging.Level;
import java.util.concurrent.CopyOnWriteArraySet;
import com.vontu.communication.data.IndexDescriptorMarshallable;
import com.vontu.communication.data.ProfileDescriptorMarshallable;
import java.util.Set;
import com.vontu.profiles.monitor.notification.LoadObserver;
import java.util.logging.Logger;
import com.vontu.util.DisposableImpl;

class IndexLoaderHelper extends DisposableImpl
{
    private static final Logger _logger;
    private final LoadedIndexMap _loadedIndices;
    private final IndexSet _indexMap;
    private final LoadObserver _observer;
    private final Set<ProfileDescriptorMarshallable> _loadedProfiles;
    
    IndexLoaderHelper(final Iterable<IndexDescriptorMarshallable> availableIndices, final LoadedIndexMap loadedIndices, final LoadObserver observer) {
        this(new IndexSet(availableIndices), loadedIndices, observer);
    }
    
    IndexLoaderHelper(final IndexSet availableIndices, final LoadedIndexMap loadedIndices, final LoadObserver observer) {
        this._loadedProfiles = new CopyOnWriteArraySet<ProfileDescriptorMarshallable>();
        this._indexMap = availableIndices;
        this._loadedIndices = loadedIndices;
        this._observer = observer;
    }
    
    public void dispose() throws Throwable {
        super.dispose();
        this._loadedIndices.unloadAll();
    }
    
    public void loadIndex(final IndexDescriptorMarshallable descriptor) {
        if (this.isDisposed()) {
            return;
        }
        final ProfileDescriptorMarshallable profile = descriptor.getProfile();
        final String profileId = profile.getProfileID();
        if (this._loadedIndices.isLoaded(profileId)) {
            return;
        }
        if (!this._indexMap.contains(descriptor)) {
            IndexLoaderHelper._logger.log(Level.FINE, "Profile index [{0}:{1} version {2}] won't be loaded because it was removed while load request was in the queue", IndexLogMessage.prependIndexDescriptorArgs(descriptor, new Object[0]));
            return;
        }
        if (!this._loadedProfiles.contains(profile)) {
            IndexLoaderHelper._logger.log(Level.FINE, "Profile index [{0}:{1} version {2}] won't be loaded because it isn't used in any condition of any active policy", IndexLogMessage.prependIndexDescriptorArgs(descriptor, new Object[0]));
            return;
        }
        try {
            this._loadedIndices.load(profileId, descriptor);
            this._observer.loadComplete(new LoadEvent(descriptor, LoadOperation.LOAD_INDEX));
        }
        catch (IndexException e) {
            this._observer.loadFailed(new LoadFailure(descriptor, LoadOperation.LOAD_INDEX, e));
        }
    }
    
    private void logActiveInfoSources(final int addedCount, final int removedCount) {
        if (IndexLoaderHelper._logger.isLoggable(Level.FINE)) {
            final StringBuffer infoSourceCsv = new StringBuffer();
            for (final ProfileDescriptorMarshallable profile : this._loadedProfiles) {
                if (infoSourceCsv.length() > 0) {
                    infoSourceCsv.append(", ");
                }
                infoSourceCsv.append('\"').append(profile.getProfileName()).append('\"');
            }
            final int size = this._loadedProfiles.size();
            IndexLoaderHelper._logger.fine("Added " + addedCount + ", removed " + removedCount + " profiles. Current count is " + size + ((size > 0) ? (": " + (Object)infoSourceCsv + '.') : "."));
        }
    }
    
    public void reconcileIndices(final Collection<ProfileDescriptorMarshallable> profileDescriptors) {
        if (this.isDisposed()) {
            return;
        }
        final Collection<ProfileDescriptorMarshallable> profilesToUnload = new HashSet<ProfileDescriptorMarshallable>(this._loadedProfiles);
        final int loadedProfilesSize = profilesToUnload.size();
        profilesToUnload.removeAll(profileDescriptors);
        final Collection<ProfileDescriptorMarshallable> profilesToLoad = new HashSet<ProfileDescriptorMarshallable>(profileDescriptors);
        profilesToLoad.removeAll(this._loadedProfiles);
        if (IndexLoaderHelper._logger.isLoggable(Level.FINE)) {
            IndexLoaderHelper._logger.fine("Unloading " + profilesToUnload.size() + " profiles, maintaining " + (loadedProfilesSize - profilesToUnload.size()) + " profiles, loading " + profilesToLoad.size() + " profiles.");
        }
        this._loadedProfiles.removeAll(profilesToUnload);
        this._loadedProfiles.addAll(profilesToLoad);
        if (IndexLoaderHelper._logger.isLoggable(Level.FINE)) {
            this.logActiveInfoSources(profilesToLoad.size(), profilesToUnload.size());
        }
        for (final IndexDescriptorMarshallable indexDescriptor : this._indexMap) {
            final ProfileDescriptorMarshallable profile = indexDescriptor.getProfile();
            if (profilesToUnload.contains(profile)) {
                this.unloadIndex(profile.getProfileID());
            }
            else {
                if (!profilesToLoad.contains(profile)) {
                    continue;
                }
                this.loadIndex(indexDescriptor);
            }
        }
    }
    
    public void unloadIndex(final String profileId) {
        if (this.isDisposed()) {
            return;
        }
        this.unloadIndex(this._loadedIndices.getIndexDescriptor(profileId));
    }
    
    private void unloadIndex(final IndexDescriptorMarshallable descriptor) {
        if (descriptor == null) {
            return;
        }
        try {
            this._loadedIndices.unload(descriptor.getProfile().getProfileID());
            this._observer.loadComplete(new LoadEvent(descriptor, LoadOperation.UNLOAD_INDEX));
        }
        catch (IndexException e) {
            this._observer.loadFailed(new LoadFailure(descriptor, LoadOperation.UNLOAD_INDEX, e));
        }
    }
    
    static {
        _logger = Logger.getLogger(IndexLoaderHelper.class.getName());
    }
}
