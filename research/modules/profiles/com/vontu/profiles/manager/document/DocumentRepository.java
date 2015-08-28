// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import com.vontu.model.data.NullValue;
import com.vontu.model.data.Course;
import com.vontu.directorycrawler.VontuFile;
import java.util.List;
import com.vontu.model.DataAccessException;
import com.vontu.profiles.common.ProfilesError;
import com.vontu.directorycrawler.VontuFileException;
import java.util.logging.Level;
import com.vontu.model.data.Data;
import com.vontu.model.data.DocSourceDoc;
import java.util.Date;
import com.vontu.model.data.DocSource;
import java.util.LinkedList;
import java.util.Collection;
import com.vontu.profiles.common.ProfilesException;
import com.vontu.model.data.InfoSource;
import com.vontu.directorycrawler.ProfilerFileSystemCrawler;
import com.vontu.model.Model;
import java.util.logging.Logger;

public class DocumentRepository
{
    private static final Logger _logger;
    private final Model _model;
    private IDocSourceDocStore _documentStore;
    private final ProfilerFileSystemCrawler _crawler;
    private int _numFiles;
    private static final String INIT_FILE_TYPE = "indexInitType";
    
    public DocumentRepository(final Model model, final InfoSource infoSource, final ProfilerFileSystemCrawler crawler) throws ProfilesException, InterruptedException {
        this._model = model;
        this._crawler = crawler;
        this._numFiles = 0;
        this.createRepository(infoSource);
    }
    
    public int getNumFiles() {
        return this._numFiles;
    }
    
    public Collection<DocSourceDocWrapper> getDocuments() {
        return this._documentStore.getSortedDocuments();
    }
    
    private void createRepository(final InfoSource infoSource) throws ProfilesException, InterruptedException {
        try {
            this._model.beginTransaction();
            this._documentStore = new DocSourceDocStoreTreeMapImpl(this.getDocSourceDocs(infoSource));
            boolean done = false;
            final List<DocSourceDocWrapper> updatedDocs = new LinkedList<DocSourceDocWrapper>();
            while (!done) {
                try {
                    final VontuFile file = this._crawler.getNextFile();
                    if (file == null) {
                        done = true;
                        continue;
                    }
                    if (!fileSizeOk(file.length(), ((DocSource)infoSource).getCourse())) {
                        continue;
                    }
                    final String fileName = file.getCanonicalPath();
                    final Date lastModified = new Date(file.lastModified());
                    final int fileSize = (int)file.length();
                    if (this._documentStore.contains(file)) {
                        final DocSourceDocWrapper docWrapper = this._documentStore.update(file);
                        updatedDocs.add(docWrapper);
                    }
                    else {
                        final DocSourceDoc document = (DocSourceDoc)this._model.newDataObject((Class)DocSourceDoc.class, false);
                        document.setInfoSource(infoSource);
                        document.setName(fileName);
                        document.setLastModified(lastModified);
                        document.setFileSize(fileSize);
                        document.setActiveInDetection(0);
                        document.setIsCrackable(0);
                        document.setFileType("indexInitType");
                        this._model.persistDataObject((Data)document);
                        this._documentStore.add(document, file);
                    }
                    ++this._numFiles;
                }
                catch (VontuFileException e) {
                    if (DocumentRepository._logger.isLoggable(Level.FINE)) {
                        DocumentRepository._logger.log(Level.FINE, "Cannot access a file", (Throwable)e);
                    }
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
            }
            for (int i = 0; i < updatedDocs.size(); ++i) {
                final DocSourceDocWrapper docWrapper2 = updatedDocs.get(i);
                this._model.currentTransaction().waitLock((Data)docWrapper2.getDocSourceDoc(), 1, false);
                docWrapper2.synchDocumentMetaInfo();
            }
            this._model.commitTransaction();
        }
        catch (InterruptedException e2) {
            throw e2;
        }
        catch (DataAccessException e3) {
            if (DocumentRepository._logger.isLoggable(Level.FINE)) {
                DocumentRepository._logger.log(Level.FINE, "Cannot create document source document during document indexing", (Throwable)e3);
            }
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, e3.getMessage());
        }
        catch (Throwable t) {
            if (DocumentRepository._logger.isLoggable(Level.FINE)) {
                DocumentRepository._logger.log(Level.FINE, "Unknown error during document indexing", t);
            }
            throw new ProfilesException(ProfilesError.PROFILES_DATABASE, t.getMessage());
        }
        finally {
            Model.cleanupTransaction();
        }
    }
    
    private static boolean fileSizeOk(final long fileSize, final Course course) {
        final int maxSize = course.getMaxFileSizeFilter();
        final int minSize = course.getMinFileSizeFilter();
        return (NullValue.isNull(maxSize) || fileSize <= maxSize) && (NullValue.isNull(minSize) || fileSize >= minSize);
    }
    
    private Collection<DocSourceDoc> getDocSourceDocs(final InfoSource infoSource) throws DataAccessException {
        final DocSourceDoc qeDocSourceDoc = (DocSourceDoc)this._model.newDataObject((Class)DocSourceDoc.class, false);
        qeDocSourceDoc.setInfoSource(infoSource);
        return (Collection<DocSourceDoc>)this._model.getDataCollectionByExample((Data)qeDocSourceDoc, Model.ComparisonType.WILDCARD);
    }
    
    static {
        _logger = Logger.getLogger(DocumentRepository.class.getName());
    }
}
