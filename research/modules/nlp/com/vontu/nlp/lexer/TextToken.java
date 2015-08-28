// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class TextToken extends Token
{
    private final int _type;
    private final TokenPosition _position;
    private int _index;
    private CharSequence _charContent;
    private String[] _normalizedValues;
    private String _value;
    
    public TextToken(final CharSequence charContent, final int type, final TokenPosition position, final boolean validate) throws TokenValidationException {
        this(charContent, type, position);
        if (validate && TokenType.hasValidator(type) && !TokenType.getValidator(type).assertTokenValidity(charContent, position.start, position.length)) {
            throw new TokenValidationException();
        }
    }
    
    public TextToken(final CharSequence charContent, final int type, final TokenPosition position) {
        this._charContent = charContent;
        this._type = type;
        this._position = position;
        this._index = -1;
    }
    
    public void releaseReferences() {
        if (this.hasNormalizedValues()) {
            if (this._normalizedValues == null) {
                this.getNormalizedValues();
            }
        }
        else if (this._value == null) {
            this.getValue();
        }
        this._charContent = null;
    }
    
    public int getIndex() {
        return this._index;
    }
    
    public void setIndex(final int index) {
        this._index = index;
    }
    
    @Override
    public String[] getNormalizedValues() {
        if (TokenType.hasNormalizer(this._type)) {
            if (this._normalizedValues == null) {
                this._normalizedValues = TokenType.getNormalizer(this._type).normalize(this.getCharContent(), this._position.start, this._position.length);
            }
        }
        else {
            this._normalizedValues = new String[0];
        }
        return this._normalizedValues;
    }
    
    public void setNormalizedValues(final String normalizedValue) {
        (this._normalizedValues = new String[1])[0] = normalizedValue;
    }
    
    @Override
    public String getValue() {
        if (this._value == null) {
            this._value = this.getCharContent().subSequence(this._position.start, this._position.start + this._position.length).toString();
        }
        return this._value;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(2048);
        sb.append(this._index).append(":").append(super.toString());
        return sb.toString();
    }
    
    public CharSequence getCharContent() {
        return this._charContent;
    }
    
    @Override
    public int getType() {
        return this._type;
    }
    
    @Override
    public TokenPosition getPosition() {
        return this._position;
    }
}
