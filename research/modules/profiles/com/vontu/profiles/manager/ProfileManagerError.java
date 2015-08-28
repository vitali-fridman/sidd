// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager;

import com.vontu.profiles.common.ProfilesError;

public class ProfileManagerError extends ProfilesError
{
    public static final ProfilesError PROFILE_MANAGER_UNEXPECTED_ERROR;
    public static final ProfilesError PROFILE_MANAGER_CORRUPTED_DATA_FILE;
    public static final ProfilesError PROFILE_MANAGER_DATA_FILE_NOT_FOUND;
    public static final ProfilesError PROFILE_MANAGER_CRYPTOGRAPHIC_ERROR;
    public static final ProfilesError PROFILE_MANAGER_PROFILE_TOO_LARGE;
    public static final ProfilesError PROFILE_MANAGER_FILE_ACCESS_ERROR;
    public static final ProfilesError PROFILE_MANAGER_INVALID_LOGIN;
    public static final ProfilesError PROFILE_MANAGER_NETWORK_DISCONNECT;
    public static final ProfilesError PROFILE_MANAGER_KEYS_NOT_IGNITED;
    public static final ProfilesError PROFILE_MANAGER_KEY_RETRIEVAL_ERROR;
    public static final ProfilesError PROFILE_MANAGER_RAM_INDEX_CREATE_ERROR;
    public static final ProfilesError PROFILES_MANAGER_PROCESS_SHUTDOWN_ERROR;
    public static final ProfilesError PROFILE_MANAGER_CRYPTO_FILE_WRITE_ERROR;
    public static final ProfilesError PROFILE_MANAGER_INDEXER_PROCESS_IO_ERROR;
    public static final ProfilesError PROFILE_MANAGER_COULD_NOT_CREATE_JVM;
    public static final ProfilesError PROFILE_MANAGER_NOT_ENOUGH_SPACE_FOR_OBJECT_HEAP;
    public static final ProfilesError PROFILE_MANAGER_CHARACTER_ENCODING_ERROR;
    public static final ProfilesError PROFILE_MANAGER_NOT_ENOUGH_DISK_SPACE;
    
    protected ProfileManagerError(final int value, final String description) throws IllegalArgumentException {
        super(value, description);
    }
    
    static {
        PROFILE_MANAGER_UNEXPECTED_ERROR = new ProfileManagerError(1001, "Unexpected indexing error occurred");
        PROFILE_MANAGER_CORRUPTED_DATA_FILE = new ProfileManagerError(1111, "Data file has too many errors");
        PROFILE_MANAGER_DATA_FILE_NOT_FOUND = new ProfileManagerError(1112, "Data file not found");
        PROFILE_MANAGER_CRYPTOGRAPHIC_ERROR = new ProfileManagerError(1114, "Cryptographic key error");
        PROFILE_MANAGER_PROFILE_TOO_LARGE = new ProfileManagerError(1116, "Index is too large");
        PROFILE_MANAGER_FILE_ACCESS_ERROR = new ProfileManagerError(1118, "Cannot read the file");
        PROFILE_MANAGER_INVALID_LOGIN = new ProfileManagerError(1120, "Invalid file location or SMB login");
        PROFILE_MANAGER_NETWORK_DISCONNECT = new ProfileManagerError(1121, "Destination host is not reachable");
        PROFILE_MANAGER_KEYS_NOT_IGNITED = new ProfileManagerError(1122, "Cryptographic keys are not ignited");
        PROFILE_MANAGER_KEY_RETRIEVAL_ERROR = new ProfileManagerError(1123, "Cryptographic keys reprieval error");
        PROFILE_MANAGER_RAM_INDEX_CREATE_ERROR = new ProfileManagerError(1124, "Error creating final index file");
        PROFILES_MANAGER_PROCESS_SHUTDOWN_ERROR = new ProfileManagerError(1125, "Indexer process did not shutdown in time");
        PROFILE_MANAGER_CRYPTO_FILE_WRITE_ERROR = new ProfileManagerError(1126, "Error writing crypto file");
        PROFILE_MANAGER_INDEXER_PROCESS_IO_ERROR = new ProfileManagerError(1127, "Error communicating with Indexer process");
        PROFILE_MANAGER_COULD_NOT_CREATE_JVM = new ProfileManagerError(1128, "Error creating Indexer process");
        PROFILE_MANAGER_NOT_ENOUGH_SPACE_FOR_OBJECT_HEAP = new ProfileManagerError(1129, "Error allocating memory for Indexer process");
        PROFILE_MANAGER_CHARACTER_ENCODING_ERROR = new ProfileManagerError(1130, "Error in character encoding of document text");
        PROFILE_MANAGER_NOT_ENOUGH_DISK_SPACE = new ProfileManagerError(1131, "Not enough space on the disk to create index files");
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_CORRUPTED_DATA_FILE, 11);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_DATA_FILE_NOT_FOUND, 9);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_CRYPTOGRAPHIC_ERROR, 27);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_PROFILE_TOO_LARGE, 22);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_INVALID_LOGIN, 24);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_NETWORK_DISCONNECT, 25);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_KEYS_NOT_IGNITED, 15);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_KEY_RETRIEVAL_ERROR, 10);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_RAM_INDEX_CREATE_ERROR, 28);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILES_MANAGER_PROCESS_SHUTDOWN_ERROR, 29);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_CRYPTO_FILE_WRITE_ERROR, 30);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_UNEXPECTED_ERROR, 16);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_INDEXER_PROCESS_IO_ERROR, 31);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_COULD_NOT_CREATE_JVM, 32);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_NOT_ENOUGH_SPACE_FOR_OBJECT_HEAP, 33);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_CHARACTER_ENCODING_ERROR, 34);
        ProfilesError.addStatusMapping(ProfileManagerError.PROFILE_MANAGER_NOT_ENOUGH_DISK_SPACE, 35);
    }
}
