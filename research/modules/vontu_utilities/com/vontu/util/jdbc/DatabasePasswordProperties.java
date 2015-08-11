// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.net.URISyntaxException;
import java.net.URI;
import java.util.Properties;
import java.io.IOException;
import java.io.File;
import com.vontu.util.properties.EncryptedProperties;

public class DatabasePasswordProperties extends EncryptedProperties
{
    public static final String FILE_LOCATION_PROPERTY = "com.vontu.jdbc.auth.properties.uri";
    public static final String BASE_PASSWORD_PROPERTY = "jdbc.password.";
    public static final String ORACLE_PASSWORD_PROPERTY = "jdbc.password.oracle-thin";
    
    public DatabasePasswordProperties() throws IOException {
        super(new File(getFileURI()));
    }
    
    public DatabasePasswordProperties(final File propertiesFile) throws IOException {
        super(propertiesFile);
    }
    
    public DatabasePasswordProperties(final File propertiesFile, final String databasePassword) throws IOException {
        super(propertiesFile, new Properties());
        this.setDatabasePassword(databasePassword);
    }
    
    public String getDatabasePassword() {
        return this.getProperty("jdbc.password.oracle-thin");
    }
    
    public String getDatabasePassword(final String connectionAlias) {
        return this.getProperty("jdbc.password." + connectionAlias);
    }
    
    public void setDatabasePassword(final String password) throws IOException {
        this.setProperty("jdbc.password.oracle-thin", password);
    }
    
    public void setDatabasePassword(final String connectionAlias, final String password) throws IOException {
        this.setProperty("jdbc.password." + connectionAlias, password);
    }
    
    private static URI getFileURI() {
        try {
            return new URI(System.getProperty("com.vontu.jdbc.auth.properties.uri"));
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
