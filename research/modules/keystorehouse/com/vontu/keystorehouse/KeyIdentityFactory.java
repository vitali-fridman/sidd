// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.keystorehouse;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import com.vontu.util.config.ConfigurationException;
import com.vontu.util.config.SettingProvider;
import com.vontu.util.config.SettingReader;
import java.util.logging.Logger;

public class KeyIdentityFactory
{
    private static final Logger _logger;
    private final SettingReader _settingReader;
    private final SettingProvider _settingProvider;
    public static final String KEY_IDENTITY_CLASS_PROPERTY = "key_identity_class";
    
    public KeyIdentityFactory(final SettingProvider settingProvider) {
        this._settingProvider = settingProvider;
        this._settingReader = new SettingReader(settingProvider, KeyIdentityFactory._logger);
    }
    
    public KeyIdentity createKeyIdentity() throws ConfigurationException {
        return this.createKeyIdentity(this._settingReader.getSetting("key_identity_class"));
    }
    
    private KeyIdentity createKeyIdentity(final String className) throws ConfigurationException {
        try {
            final Class keyIdentityClass = Class.forName(className);
            final Constructor[] constructors = keyIdentityClass.getConstructors();
            Constructor defaultConstructor = null;
            Constructor preferredConstructor = null;
            for (int i = 0; i < constructors.length; ++i) {
                final Class[] parameters = constructors[i].getParameterTypes();
                if (parameters.length == 1 && SettingProvider.class.isAssignableFrom(parameters[0])) {
                    preferredConstructor = constructors[i];
                }
                else if (parameters.length == 0) {
                    defaultConstructor = constructors[i];
                }
            }
            if (preferredConstructor != null) {
                return (KeyIdentity) preferredConstructor.newInstance(this._settingProvider);
            }
            if (defaultConstructor != null) {
                return (KeyIdentity) keyIdentityClass.newInstance();
            }
            throw new ConfigurationException("No suitable constructor can be found in KeyIdentity class " + className + '.');
        }
        catch (ClassNotFoundException e) {
            throw new ConfigurationException("KeyIdentity implementation class " + className + " cannot be found.", (Throwable)e);
        }
        catch (ClassCastException e2) {
            throw new ConfigurationException("Class " + className + " doesn't implement KeyIdentity interface.", (Throwable)e2);
        }
        catch (InvocationTargetException e3) {
            if (e3.getCause() instanceof ConfigurationException) {
                throw (ConfigurationException)e3.getCause();
            }
            throw new ConfigurationException("Can't create instance of KeyIdentity class " + className + '.', e3.getCause());
        }
        catch (Exception e4) {
            throw new ConfigurationException("Can't create instance of KeyIdentity class " + className + '.', (Throwable)e4);
        }
    }
    
    static {
        _logger = Logger.getLogger(KeyIdentityFactory.class.getName());
    }
}
