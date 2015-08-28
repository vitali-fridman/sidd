// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public abstract class Token
{
    public abstract int getType();
    
    public abstract TokenPosition getPosition();
    
    public String getBestValue() {
        if (!this.hasNormalizedValues()) {
            return this.getValue();
        }
        if (this.getNormalizedValues().length > 0) {
            return this.getLongestNormalizedValue();
        }
        return null;
    }
    
    public boolean hasNormalizedValues() {
        return TokenType.hasNormalizer(this.getType());
    }
    
    public abstract String[] getNormalizedValues();
    
    public abstract String getValue();
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final int type = this.getType();
        final String value = this.getValue();
        final TokenPosition position = this.getPosition();
        if (position != null) {
            sb.append(position.start).append(':').append(position.length);
            sb.append(':').append(position.line);
        }
        if (type == 12 && value.charAt(0) == '\t') {
            sb.append('-').append(TokenType.getName(type)).append(":TAB:").append(this.getValue());
        }
        else {
            sb.append('-').append(TokenType.getName(type)).append(':').append(this.getValue());
        }
        if (this.hasNormalizedValues()) {
            sb.append("  --> Normalized");
            final String[] normalized = this.getNormalizedValues();
            for (int i = 0; i < normalized.length; ++i) {
                sb.append(" :").append(normalized[i]);
            }
        }
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        if (this.hasNormalizedValues()) {
            final String[] values = this.getNormalizedValues();
            int hash = 17;
            for (int i = 0; i < values.length; ++i) {
                hash += values[i].hashCode();
            }
            return hash;
        }
        return this.getValue().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        final Token otherToken = (Token)obj;
        if (this.getType() != otherToken.getType()) {
            return false;
        }
        if (!this.hasNormalizedValues()) {
            return !otherToken.hasNormalizedValues() && this.getValue().equals(otherToken.getValue());
        }
        if (!otherToken.hasNormalizedValues()) {
            return false;
        }
        final String[] thisValues = this.getNormalizedValues();
        final String[] otherValues = otherToken.getNormalizedValues();
        if (thisValues.length != otherValues.length) {
            return false;
        }
        for (int i = 0; i < thisValues.length; ++i) {
            if (!thisValues[i].equals(otherValues[i])) {
                return false;
            }
        }
        return true;
    }
    
    private String getLongestNormalizedValue() {
        int longestIndex = 0;
        int longestLength = 0;
        final String[] values = this.getNormalizedValues();
        if (values.length == 0) {
            return this.getValue();
        }
        for (int i = 0; i < values.length; ++i) {
            if (values[i].length() > longestLength) {
                longestLength = values[i].length();
                longestIndex = i;
            }
        }
        return values[longestIndex];
    }
    
    String getTrueLongestNormalizedValue() {
        int longestIndex = 0;
        int longestLength = 0;
        final String[] values = this.getNormalizedValues();
        if (values.length == 0) {
            return null;
        }
        for (int i = 0; i < values.length; ++i) {
            if (values[i].length() > longestLength) {
                longestLength = values[i].length();
                longestIndex = i;
            }
        }
        return values[longestIndex];
    }
}
