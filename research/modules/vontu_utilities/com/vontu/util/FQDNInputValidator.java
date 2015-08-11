// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public class FQDNInputValidator
{
    private String _fqdnValue;
    boolean _validated;
    private String _host;
    private String _port;
    
    public FQDNInputValidator(final String fqdnValue) {
        this._fqdnValue = fqdnValue;
        this._validated = false;
    }
    
    public void validate() throws FQDNInputValidationException {
        if (this._fqdnValue == null) {
            throw new FQDNInputValidationException(FQDNInputValidationErrorEnum.EMPTY_FIELD, this._fqdnValue);
        }
        final String[] serverAndPort = this._fqdnValue.split(":");
        if (serverAndPort.length > 2 || serverAndPort.length == 0) {
            throw new FQDNInputValidationException(FQDNInputValidationErrorEnum.INVALID_HOST_OR_IP_AND_PORT, this._fqdnValue);
        }
        this._host = serverAndPort[0].trim();
        if (this._host.length() == 0) {
            throw new FQDNInputValidationException(FQDNInputValidationErrorEnum.INVALID_HOST, this._host);
        }
        if (serverAndPort.length == 2) {
            try {
                this._port = serverAndPort[1].trim();
                if (this._port != null && this._port.length() > 0) {
                    final int portNum = Integer.parseInt(this._port);
                    if (portNum < 1 || portNum > 65535) {
                        throw new FQDNInputValidationException(FQDNInputValidationErrorEnum.INVALID_PORT, this._port);
                    }
                }
            }
            catch (NumberFormatException e) {
                throw new FQDNInputValidationException(FQDNInputValidationErrorEnum.INVALID_PORT, this._port);
            }
        }
        this._validated = true;
    }
    
    public String getHost() {
        if (!this._validated) {
            throw new IllegalStateException("FQDN must be validated before calling getHost ()");
        }
        return this._host;
    }
    
    public String getPort() {
        if (!this._validated) {
            throw new IllegalStateException("FQDN must be validated before calling getPort ()");
        }
        return this._port;
    }
}
