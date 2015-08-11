// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.parse;

public class PortNumberParser
{
    private final int MAXIMUM_PORT_NUMBER = 65535;
    
    public int getPortNumber(final String portValue) throws IllegalArgumentException {
        int portNumber;
        try {
            portNumber = Integer.parseInt(portValue);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Malformed Port Number");
        }
        if (!this.isWithinValidRange(portNumber)) {
            throw new IllegalArgumentException("The Port Number is not within the Valid Range");
        }
        return portNumber;
    }
    
    public boolean isValidPort(final String portValue) {
        int portNumber;
        try {
            portNumber = Integer.parseInt(portValue);
        }
        catch (Exception e) {
            return false;
        }
        return this.isWithinValidRange(portNumber);
    }
    
    private boolean isWithinValidRange(final int port) {
        return port >= 0 && port <= 65535;
    }
}
