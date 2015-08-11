// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.software;

public class Version implements Comparable<Object>
{
    public static final Version MINIMUM_VERSION;
    public static final Version MAXIMUM_VERSION;
    private String _version;
    private int _majorVersion;
    private int _minorVersion;
    private int _revision;
    private int _componentVersion;
    
    public Version(final String version) {
        this._version = version;
        this.parseVersion();
    }
    
    public void setVersion(final String version) {
        this._version = version;
        this.parseVersion();
    }
    
    private void parseVersion() {
        final String[] versions = this._version.split("\\.");
        if (versions.length == 4) {
            this._majorVersion = Integer.parseInt(versions[0]);
            this._minorVersion = Integer.parseInt(versions[1]);
            this._revision = Integer.parseInt(versions[2]);
            this._componentVersion = Integer.parseInt(versions[3]);
        }
        else if (versions.length == 3) {
            this._majorVersion = Integer.parseInt(versions[0]);
            this._minorVersion = Integer.parseInt(versions[1]);
            this._revision = Integer.parseInt(versions[2]);
            this._componentVersion = 0;
        }
        else if (versions.length == 2) {
            this._majorVersion = Integer.parseInt(versions[0]);
            this._minorVersion = Integer.parseInt(versions[1]);
            this._revision = 0;
            this._componentVersion = 0;
        }
        else {
            if (versions.length != 1) {
                throw new IllegalArgumentException("Invalid version: " + this._version);
            }
            this._majorVersion = Integer.parseInt(versions[0]);
            this._minorVersion = 0;
            this._revision = 0;
            this._componentVersion = 0;
        }
    }
    
    public String getVersionString() {
        return this._version;
    }
    
    public int getMajorVersion() {
        return this._majorVersion;
    }
    
    public int getMinorVersion() {
        return this._minorVersion;
    }
    
    public int getRevision() {
        return this._revision;
    }
    
    public int getComponentVersion() {
        return this._componentVersion;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Version) {
            final Version compareVersion = (Version)obj;
            return this._version.equals(compareVersion._version);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this._version.hashCode();
    }
    
    @Override
    public String toString() {
        return this._version;
    }
    
    @Override
    public int compareTo(final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (this.equals(o)) {
            return 0;
        }
        if (o instanceof Version) {
            final Version compareVersion = (Version)o;
            int thisVersion = this._majorVersion;
            int otherVersion = compareVersion._majorVersion;
            if (thisVersion == otherVersion) {
                thisVersion = this._minorVersion;
                otherVersion = compareVersion._minorVersion;
                if (thisVersion == otherVersion) {
                    thisVersion = this._revision;
                    otherVersion = compareVersion._revision;
                    if (thisVersion == otherVersion) {
                        thisVersion = this._componentVersion;
                        otherVersion = compareVersion._componentVersion;
                    }
                }
            }
            return this.compareVersion(thisVersion, otherVersion);
        }
        throw new ClassCastException();
    }
    
    public int compareMajorAndMinor(final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (this.equals(o)) {
            return 0;
        }
        if (o instanceof Version) {
            final Version compareVersion = (Version)o;
            int thisVersion = this._majorVersion;
            int otherVersion = compareVersion._majorVersion;
            if (thisVersion == otherVersion) {
                thisVersion = this._minorVersion;
                otherVersion = compareVersion._minorVersion;
            }
            return this.compareVersion(thisVersion, otherVersion);
        }
        throw new ClassCastException();
    }
    
    private int compareVersion(final int version1, final int version2) {
        if (version1 == version2) {
            return 0;
        }
        if (version1 < version2) {
            return -1;
        }
        return 1;
    }
    
    static {
        MINIMUM_VERSION = new Version("0.0.0.0");
        MAXIMUM_VERSION = new Version("2147483647.2147483647.2147483647.2147483647");
    }
}
