// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.software;

public class VersionRange implements Comparable<VersionRange>
{
    private final Version greaterThanOrEqualToVersion;
    private final Version lessThanVersion;
    
    public VersionRange(final Version greaterThanOrEqualToVersion, final Version lessThanVersion) {
        this.greaterThanOrEqualToVersion = greaterThanOrEqualToVersion;
        this.lessThanVersion = lessThanVersion;
        if (greaterThanOrEqualToVersion == null || lessThanVersion == null) {
            throw new NullPointerException("Programming error. Version may not be null.");
        }
        if (greaterThanOrEqualToVersion.compareTo(lessThanVersion) >= 0) {
            throw new IllegalArgumentException("Programming error. Lower agent software version limit (" + greaterThanOrEqualToVersion + ") must be less than upper agent software version limit (" + lessThanVersion + ").");
        }
    }
    
    public boolean isVersionInRange(final Version version) {
        if (version == null) {
            throw new NullPointerException("Programming error. Version may not be null.");
        }
        final boolean isInRange = version.compareTo(this.greaterThanOrEqualToVersion) >= 0 && version.compareTo(this.lessThanVersion) < 0;
        return isInRange;
    }
    
    public boolean isBelowAndAdjacent(final VersionRange other) {
        final boolean isBelowAndAdjacent = this.lessThanVersion.equals(other.greaterThanOrEqualToVersion);
        return isBelowAndAdjacent;
    }
    
    public boolean isBelow(final VersionRange other) {
        final boolean isBelow = this.lessThanVersion.compareTo(other.greaterThanOrEqualToVersion) <= 0;
        return isBelow;
    }
    
    public Version getGreaterThanOrEqualToVersion() {
        return this.greaterThanOrEqualToVersion;
    }
    
    public Version getLessThanVersion() {
        return this.lessThanVersion;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.greaterThanOrEqualToVersion == null) ? 0 : this.greaterThanOrEqualToVersion.hashCode());
        result = 31 * result + ((this.lessThanVersion == null) ? 0 : this.lessThanVersion.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final VersionRange other = (VersionRange)obj;
        if (this.greaterThanOrEqualToVersion == null) {
            if (other.greaterThanOrEqualToVersion != null) {
                return false;
            }
        }
        else if (!this.greaterThanOrEqualToVersion.equals(other.greaterThanOrEqualToVersion)) {
            return false;
        }
        if (this.lessThanVersion == null) {
            if (other.lessThanVersion != null) {
                return false;
            }
        }
        else if (!this.lessThanVersion.equals(other.lessThanVersion)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int compareTo(final VersionRange other) {
        int comparison = this.greaterThanOrEqualToVersion.compareTo(other.greaterThanOrEqualToVersion);
        if (comparison == 0) {
            comparison = this.lessThanVersion.compareTo(other.lessThanVersion);
        }
        return comparison;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("VersionRange [greaterThanOrEqualToVersion=");
        builder.append(this.greaterThanOrEqualToVersion);
        builder.append(", lessThanVersion=");
        builder.append(this.lessThanVersion);
        builder.append("]");
        return builder.toString();
    }
    
    public static void validateVersionRange(final VersionRange expectedVersionRange, final VersionRange actualVersionRange) {
        if (!expectedVersionRange.equals(actualVersionRange)) {
            throw new IllegalArgumentException("Programming error. Actual version range (" + actualVersionRange + ") is not equal to expected version range (" + expectedVersionRange + ").");
        }
    }
}
