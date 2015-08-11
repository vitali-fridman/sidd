// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

public enum Language
{
    UNKNOWN("unknown"), 
    WESTERN("Western"), 
    CELTIC("Celtic"), 
    GREEK("Greek"), 
    ICELANDIC("Icelandic"), 
    NORDIC("Nordic"), 
    SOUTH_EUROPEAN("South European"), 
    BALTIC("Baltic"), 
    CENTRAL_EUROPEAN("Central European"), 
    CROATIAN("Croatian"), 
    CYRILLIC("Cyrillic"), 
    ROMANIAN("Romanian"), 
    CHINESE_SIMPLIFIED("Chinese Simplified"), 
    CHINESE_TRADITIONAL("Chinese Traditional"), 
    JAPANESE("Japanese"), 
    KOREAN("Korean"), 
    ARMENIAN("Armenian"), 
    GEORGIAN("Georgian"), 
    THAI("Thai"), 
    TURKISH("Turkish"), 
    VIETNAMESE("Vietnamese"), 
    HINDI("Hindi"), 
    GUJARATI("Gujarati"), 
    GUMUKHI("Gumukhi"), 
    ARABIC("Arabic"), 
    FARSI("Farsi"), 
    HEBREW("Hebrew"), 
    UNICODE("Unicode");
    
    private String _name;
    
    private Language(final String name) {
        this._name = name;
    }
    
    public String getName() {
        return this._name;
    }
    
    public static Language lookupLanguage(String language) {
        language = language.toLowerCase();
        for (final Language lang : values()) {
            if (lang.getName().toLowerCase().equals(language)) {
                return lang;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this._name;
    }
}
