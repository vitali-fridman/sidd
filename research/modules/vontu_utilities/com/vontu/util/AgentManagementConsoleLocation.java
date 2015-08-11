// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.net.MalformedURLException;
import java.net.URL;

public class AgentManagementConsoleLocation
{
    private static final String AGENT_MGMT_SOLUTION_VIRTUAL_DIRECTORY = "/Altiris/DLPAgentmgmt";
    private URL _url;
    
    public AgentManagementConsoleLocation(final String serverAndPort) throws FQDNInputValidationException {
        this._url = null;
        final FQDNInputValidator validator = new FQDNInputValidator(serverAndPort);
        validator.validate();
        try {
            this._url = this.constructURLandValidate(validator.getHost(), validator.getPort());
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid fully qualified notification server host name or address and port.");
        }
    }
    
    public AgentManagementConsoleLocation(final String host, final String port) throws IllegalArgumentException {
        this._url = null;
        try {
            this._url = this.constructURLandValidate(host, port);
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid fully qualified notification server host name or address and port.");
        }
    }
    
    protected URL constructURLandValidate(final String host, final String port) throws MalformedURLException {
        URL toRet = null;
        if (port != null) {
            toRet = new URL("http", host, Integer.parseInt(port), "/Altiris/DLPAgentmgmt");
        }
        else {
            toRet = new URL("http", host, "/Altiris/DLPAgentmgmt");
        }
        return toRet;
    }
    
    public String getHost() {
        return this._url.getHost();
    }
    
    public String getPort() {
        if (this._url.getPort() == -1) {
            return null;
        }
        return String.valueOf(this._url.getPort());
    }
    
    public String getConsoleURLString() {
        return this._url.toString();
    }
    
    public String getDefaultPort() {
        return String.valueOf(this._url.getDefaultPort());
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof AgentManagementConsoleLocation && this._url.toString().equals(((AgentManagementConsoleLocation)obj)._url.toString());
    }
}
