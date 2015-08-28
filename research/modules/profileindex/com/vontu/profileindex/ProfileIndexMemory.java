// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex;

import java.util.logging.Level;
import com.vontu.util.system.MemoryInfoWithGc;
import com.vontu.util.config.SettingProvider;
import com.vontu.util.system.MemoryInfo;
import com.vontu.util.config.SettingReader;
import java.util.logging.Logger;

public class ProfileIndexMemory
{
    private static final Logger _logger;
    private static final int KB = 1024;
    private static final int MB = 1048576;
    public static final String SYSTEM_INFO_IMPL = "system_info_impl";
    public static final String CHECK_AVAILABLE_RAM = "check_available_ram";
    public static final boolean CHECK_AVAILABLE_RAM_DEFAULT = true;
    public static final String MAXIMUM_IN_PROCESS_INDEX_SIZE = "maximum_in_process_index_size";
    public static final int MAXIMUM_IN_PROCESS_INDEX_SIZE_DEFAULT = 104857600;
    public static final String MINIMUM_MEMORY_RESERVE = "minimum_memory_reserve";
    public static final long MINIMUM_MEMORY_RESERVE_DEFAULT = 209715200L;
    private final ProfileIndexDescriptor _indexDescriptor;
    private final long _memoryRequirement;
    private final SettingReader _settingReader;
    private final SystemInfo _systemInfo;
    private final MemoryInfo _memoryInfo;
    
    public ProfileIndexMemory(final SettingProvider settingProvider, final ProfileIndexDescriptor indexDescriptor) {
        this._settingReader = new SettingReader(settingProvider, ProfileIndexMemory._logger);
        this._memoryRequirement = getMemoryRequirement(indexDescriptor, this._settingReader);
        this._systemInfo = getSystemInfo(this._settingReader.getSetting("system_info_impl", ""));
        this._indexDescriptor = indexDescriptor;
        this._memoryInfo = (MemoryInfo)new MemoryInfoWithGc();
    }
    
    protected static SystemInfo getSystemInfo(final String className) {
        if (className.trim().length() == 0) {
            ProfileIndexMemory._logger.warning("SystemInfo implementation isn't specified. RAM check will be disabled.");
            return null;
        }
        try {
            return (SystemInfo)Class.forName(className).newInstance();
        }
        catch (InstantiationException e) {
            ProfileIndexMemory._logger.log(Level.WARNING, "Failed to create instance of class " + className + ". The RAM check will be disabled.", e);
        }
        catch (IllegalAccessException e2) {
            ProfileIndexMemory._logger.log(Level.WARNING, "Failed to create instance of class " + className + ". The RAM check will be disabled.", e2);
        }
        catch (ClassNotFoundException e3) {
            ProfileIndexMemory._logger.warning("Class " + className + " can't be found. The RAM check will be disabled.");
        }
        catch (ClassCastException e4) {
            ProfileIndexMemory._logger.warning("Class " + className + " doesn't implement SystemInfo interface. " + "The RAM check will be disabled.");
        }
        return null;
    }
    
    protected void checkFitsInRam() throws IndexException {
        if (this._settingReader.getBooleanSetting("check_available_ram", true) && this._systemInfo != null) {
            this.checkFitsInRam(this._systemInfo.getAvailableRam());
        }
    }
    
    protected void checkFitsInRam(final long availableRam) throws IndexException {
        final String profileName = this._indexDescriptor.profile().name();
        final String indexVerion = String.valueOf(this._indexDescriptor.version());
        if (ProfileIndexMemory._logger.isLoggable(Level.FINE)) {
            ProfileIndexMemory._logger.fine("Memory required to load " + profileName + " version " + indexVerion + ' ' + this._memoryRequirement + ", available RAM " + availableRam + '.');
        }
        if (this._memoryRequirement > availableRam) {
            throw new IndexException(IndexError.INDEX_NOT_ENOUGH_RAM, new Object[] { profileName, indexVerion, String.valueOf((this._memoryRequirement - availableRam) / 1048576L) });
        }
    }
    
    protected InputStreamFactory getSingleStreamFactory() {
        return this._indexDescriptor.streams()[0];
    }
    
    private long getMaxInProcessIndexSize() {
        return this._settingReader.getMemorySetting("maximum_in_process_index_size", 104857600L);
    }
    
    protected boolean doesFitInProcessMemory() {
        return this._indexDescriptor.size() < this.getMaxInProcessIndexSize() && this._memoryInfo.jvmHasMemory(this._indexDescriptor.size());
    }
    
    protected static long getMemoryRequirement(final ProfileIndexDescriptor indexDescriptor, final SettingReader settingReader) {
        return indexDescriptor.size() + settingReader.getMemorySetting("minimum_memory_reserve", 209715200L);
    }
    
    static {
        _logger = Logger.getLogger(ProfileIndexMemory.class.getName());
    }
}
