// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.software;

import java.util.logging.Level;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.logging.Logger;

public class Software
{
    private static final Logger _logger;
    private final String PRODUCT_FAMILY_NAME = "Symantec Data Loss Prevention";
    private final String PRODUCT_FAMILY_IDENTIFIER = "241ffa3c-822a-47e9-aecc-e3898b49c1b0";
    private final String PRODUCT_DESCRIPTION = "Symantec Data Loss Prevention delivers a proven, content-aware solution to discover, monitor, protect and manage confidential data wherever it is stored or used.  The solution allows you to measurably reduce your risk of a data breach, demonstrate regulatory compliance and safeguard customer privacy, brand equity and intellectual property. Unlike other solutions, Symantec\u2122 Data Loss Prevention delivers comprehensive coverage across all data types and exit points combined with a proven track record of successful deployments. With Symantec Data Loss Prevention, customers can proactively protect their information, rapidly respond to the changing threat landscape and leverage content-awareness to more effectively and efficiently deploy enterprise security.";
    private final ProtectComponent _component;
    private final File _versionFile;
    
    public Software() throws NoVersionException {
        File managerVer = null;
        File monitorVer = null;
        try {
            managerVer = new File(getVersionFilePath(ProtectComponent.MANAGER));
            monitorVer = new File(getVersionFilePath(ProtectComponent.MONITOR));
        }
        catch (Exception ex) {}
        if (monitorVer != null && monitorVer.exists() && managerVer != null && managerVer.exists()) {
            this._component = ProtectComponent.SINGLETIER;
            this._versionFile = managerVer;
        }
        else if (monitorVer != null && monitorVer.exists()) {
            this._component = ProtectComponent.MONITOR;
            this._versionFile = monitorVer;
        }
        else {
            if (managerVer == null || !managerVer.exists()) {
                throw new NoVersionException("Version file path not specified");
            }
            this._component = ProtectComponent.MANAGER;
            this._versionFile = managerVer;
        }
    }
    
    private static String getVersionFilePath(final ProtectComponent protectComponent) {
        String versionFilePath = ".";
        if (protectComponent == ProtectComponent.MANAGER || protectComponent == ProtectComponent.SINGLETIER) {
            versionFilePath = System.getProperty("com.vontu.manager.version.file");
        }
        else if (protectComponent == ProtectComponent.MONITOR) {
            versionFilePath = System.getProperty("com.vontu.monitor.version.file");
        }
        return versionFilePath;
    }
    
    public ProtectComponent component() {
        return this._component;
    }
    
    public Version version() throws NoVersionException {
        BufferedReader versionReader;
        try {
            versionReader = new BufferedReader(new FileReader(this._versionFile));
        }
        catch (FileNotFoundException e) {
            throw new NoVersionException("Version file missing: " + this._versionFile.getAbsolutePath(), e);
        }
        try {
            final String version = versionReader.readLine();
            return new Version(version);
        }
        catch (IOException e2) {
            throw new NoVersionException("Could not read version information.", e2);
        }
        finally {
            try {
                versionReader.close();
            }
            catch (IOException e3) {
                Software._logger.log(Level.SEVERE, "Failed to close version file.", e3);
            }
        }
    }
    
    public String getProductName() {
        return "Symantec Data Loss Prevention";
    }
    
    public String getProductIdentifier() {
        return "241ffa3c-822a-47e9-aecc-e3898b49c1b0";
    }
    
    public String getProductDescription() {
        return "Symantec Data Loss Prevention delivers a proven, content-aware solution to discover, monitor, protect and manage confidential data wherever it is stored or used.  The solution allows you to measurably reduce your risk of a data breach, demonstrate regulatory compliance and safeguard customer privacy, brand equity and intellectual property. Unlike other solutions, Symantec\u2122 Data Loss Prevention delivers comprehensive coverage across all data types and exit points combined with a proven track record of successful deployments. With Symantec Data Loss Prevention, customers can proactively protect their information, rapidly respond to the changing threat landscape and leverage content-awareness to more effectively and efficiently deploy enterprise security.";
    }
    
    static {
        _logger = Logger.getLogger(Software.class.getName());
    }
}
