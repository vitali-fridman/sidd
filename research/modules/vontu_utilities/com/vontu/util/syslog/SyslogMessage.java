// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.syslog;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Calendar;

public class SyslogMessage
{
    private String _pri;
    private String _header;
    private String _body;
    private byte[] _messageBytes;
    
    public SyslogMessage(final int facility, final int severity, final String hostName, final String message) throws SyslogException {
        this.makePri(severity, facility);
        this.makeHeader(null);
        this.makeBody(hostName, message);
        this._messageBytes = this.makeBytes();
    }
    
    public SyslogMessage(final int facility, final int severity, final String hostName, final String message, final Calendar calendar) throws SyslogException {
        this.makePri(severity, facility);
        this.makeHeader(calendar);
        this.makeBody(hostName, message);
        this._messageBytes = this.makeBytes();
    }
    
    void makePri(final int severity, final int facility) {
        this._pri = "<" + Integer.toString(severity + facility * 8) + ">";
    }
    
    String getPriString() {
        return this._pri;
    }
    
    void makeHeader(final Calendar currentTime) {
        Calendar myTime;
        if (currentTime == null) {
            myTime = Calendar.getInstance();
        }
        else {
            myTime = currentTime;
        }
        String dateString;
        if (myTime.get(5) < 10) {
            dateString = new SimpleDateFormat("MMM  d HH:mm:ss ", Locale.US).format(myTime.getTime());
        }
        else {
            dateString = new SimpleDateFormat("MMM dd HH:mm:ss ", Locale.US).format(myTime.getTime());
        }
        this._header = dateString + " ";
    }
    
    public String getHeader() {
        return this._header;
    }
    
    private void makeBody(final String logName, final String message) {
        this._body = logName + " " + message;
    }
    
    public String getBody() {
        return this._body;
    }
    
    private byte[] makeBytes() throws SyslogException {
        final byte[] _priBytes = this._pri.getBytes();
        final byte[] _headerBytes = this._header.getBytes();
        byte[] _bodyBytes = null;
        try {
            _bodyBytes = this._body.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new SyslogException("Unsupported encoding UTF-8");
        }
        final int _priLength = _priBytes.length;
        final int _headerLength = _headerBytes.length;
        final int _bodyLength = _bodyBytes.length;
        final int totalLength = this._pri.length() + this._header.length() + _bodyBytes.length + 1;
        if (totalLength > 1460) {
            throw new SyslogException("Syslog message to large: size: " + Integer.toString(totalLength) + " MAX_MESSAGE_SIZE: " + 1460);
        }
        final byte[] returnValue = new byte[this._pri.length() + this._header.length() + _bodyBytes.length + 1];
        System.arraycopy(_priBytes, 0, returnValue, 0, _priLength);
        System.arraycopy(_headerBytes, 0, returnValue, _priLength, _headerLength);
        System.arraycopy(_bodyBytes, 0, returnValue, _priLength + _headerLength, _bodyLength);
        returnValue[totalLength - 1] = 0;
        return returnValue;
    }
    
    public byte[] getBytes() {
        return this._messageBytes;
    }
}
