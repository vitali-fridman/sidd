// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class DBDataToken extends Token
{
    private final int _type;
    private final TokenPosition _position;
    private final Normalizer _normalizer;
    private String[] _normalizedValues;
    private String _value;
    
    public DBDataToken(final String value, final int type, final TokenPosition position, final boolean validate) throws TokenValidationException {
        this(value, type, position);
        if (validate && TokenType.hasValidator(type) && !TokenType.getValidator(type).assertTokenValidity(value)) {
            throw new TokenValidationException();
        }
    }
    
    public DBDataToken(final String value, final int type, final TokenPosition position) {
        this._value = value;
        this._type = type;
        this._position = position;
        this._normalizer = TokenType.getNormalizer(this._type);
    }
    
    @Override
    public String[] getNormalizedValues() {
        if (this._normalizer != null) {
            if (this._normalizedValues == null) {
                this._normalizedValues = this._normalizer.normalize(this._value);
            }
        }
        else {
            this._normalizedValues = new String[0];
        }
        return this._normalizedValues;
    }
    
    @Override
    public boolean hasNormalizedValues() {
        return this._normalizer != null;
    }
    
    @Override
    public String getValue() {
        return this._value;
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
