// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.passwordstore;

import java.io.IOException;
import java.io.File;
import com.vontu.util.properties.EncryptedProperties;

public class LegacyProtectPasswordProperties extends EncryptedProperties
{
    public static final String ADMINISTRATOR_PASSWORD_PROPERTY = "administrator.password";
    public static final String ORACLE_PASSWORD_PROPERTY = "jdbc.password.oracle-thin";
    
    public LegacyProtectPasswordProperties(final File propertiesFile) throws IOException {
        super(propertiesFile);
    }
    
    public String getAdministratorPassword() {
        return this.getProperty("administrator.password");
    }
    
    public String getDatabasePassword() {
        return this.getProperty("jdbc.password.oracle-thin");
    }
}
