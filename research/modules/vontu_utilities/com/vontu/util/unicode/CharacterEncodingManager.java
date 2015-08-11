// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import com.vontu.util.unicode.icu.NotGuessingGuesser;
import com.vontu.util.unicode.icu.IcuGuesser;
import com.vontu.util.unicode.icu.BestGuessResult;
import java.util.Collection;
import java.io.IOException;
import com.vontu.util.URILoader;
import java.net.URI;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Properties;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.vontu.util.unicode.icu.Guesser;

public class CharacterEncodingManager
{
    public static final String DEFAULT = "UTF-8";
    private static final String GUESSING_ENABLED_DEFAULT = "off";
    private static final String CHECK_ICU_CLASS = "com.ibm.icu.text.CharsetDetector";
    public static final String SYSTEM_PROPERTY_FILE = "com.vontu.unicode.encodings";
    private Guesser _encodingGuesser;
    private static final String PROPERTY_SETTING_LOADOTHER = "settings.loadAdditional";
    public static final String PROPERTY_NAME_SUFFIX = "_name";
    public static final String PROPERTY_ALIASES_SUFFIX = "_aliases";
    public static final String PROPERTY_LANGUAGE_SUFFIX = "_language";
    public static final String PROPERTY_BYTECONVERTER_SUFFIX = "_byteconverter";
    public static final String PROPERTY_JAVANAMES_SUFFIX = "_javanames";
    public static final String PROPERTY_UNITTESTSTRING_SUFFIX = "_unitteststring";
    public static final String PROPERTY_CANNOT_ENCODE_SUFFIX = "_cannotencode";
    public static final String PROPERTY_CANNOT_DECODE_SUFFIX = "_cannotdecode";
    public static final String VALUE_GENERIC_CONVERTER = "com.vontu.util.unicode.converter.GenericConverter";
    public static final String PROPERTY_JAVA_ENCODER = "com.vontu.util.unicode.converter.GenericConverter";
    public static final String PROPERTIES_FILE = "/com/vontu/util/unicode/characterEncoding.properties";
    private static CharacterEncodingManager _instance;
    private static final Logger _logger;
    private Map<String, CharacterEncoding> _fastAliasLookupMap;
    private List<CharacterEncoding> _allEncodings;
    private Map<CharacterEncoding, Integer> _countUsage;
    
    private CharacterEncodingManager(final boolean generateUnknownEncodings) {
        this._encodingGuesser = null;
        this._countUsage = null;
        this._fastAliasLookupMap = new HashMap<String, CharacterEncoding>();
        (this._allEncodings = new ArrayList<CharacterEncoding>()).add(CharacterEncoding.UNKNOWN);
        this.assignGuesser();
        this.loadAllEncodings(generateUnknownEncodings);
    }
    
    public CharacterEncoding getEncoding(final String name) {
        return this._fastAliasLookupMap.get(this.normalize(name));
    }
    
    private void loadAllEncodings(boolean generateUnknownEncodings) {
        final Properties properties = this.loadEncodingProperties(getPropertiesFile());
        final Enumeration<?> e = properties.propertyNames();
        while (e.hasMoreElements()) {
            final String key = (String)e.nextElement();
            if (key.endsWith("_name")) {
                final String prefix = key.substring(0, key.length() - "_name".length());
                this.loadEncoding(properties, prefix);
            }
        }
        if (properties.getProperty("settings.loadAdditional") != null) {
            generateUnknownEncodings = properties.getProperty("settings.loadAdditional").trim().equalsIgnoreCase("true");
        }
        if (!generateUnknownEncodings) {
            return;
        }
        final Map<String, Charset> availableCharsets = Charset.availableCharsets();
        for (final String charsetName : availableCharsets.keySet()) {
            final CharacterEncoding encoding = this.getEncoding(charsetName);
            if (encoding == null) {
                this.generateEncoding(charsetName, availableCharsets.get(charsetName));
            }
            else {
                if (encoding.getConverter() != null) {
                    continue;
                }
                final ByteConverter converter = this.lookupConverter("com.vontu.util.unicode.converter.GenericConverter");
                final boolean canDecode = true;
                final boolean canEncode = availableCharsets.get(charsetName).canEncode();
                converter.initialize(charsetName, canEncode, canDecode);
                encoding.setConverter(converter);
                this.generateEncoding(charsetName, availableCharsets.get(charsetName));
            }
        }
    }
    
    private void generateEncoding(final String name, final Charset charset) {
        CharacterEncoding encoding = null;
        final Set<String> aliasSet = (charset.aliases() == null) ? new HashSet<String>() : charset.aliases();
        final String[] aliases = new String[aliasSet.size()];
        int i = 0;
        for (final String alias : aliasSet) {
            aliases[i] = this.normalize(alias);
            if (encoding == null) {
                encoding = this.getEncoding(aliases[i]);
            }
            ++i;
        }
        if (encoding != null) {
            this._fastAliasLookupMap.put(this.normalize(name), encoding);
            for (final String alias2 : aliases) {
                if (this.getEncoding(alias2) == null) {
                    this._fastAliasLookupMap.put(alias2, encoding);
                }
            }
            return;
        }
        final ByteConverter converter = this.lookupConverter("com.vontu.util.unicode.converter.GenericConverter");
        final Language lang = Language.UNKNOWN;
        final boolean canDecode = true;
        final boolean canEncode = charset.canEncode();
        final String prefix = "GEN";
        this.addEncodingToGlobalList(name, aliases, lang, converter, prefix, canEncode, canDecode);
    }
    
    private void loadEncoding(final Properties properties, final String prefix) {
        final String name = properties.getProperty(prefix + "_name");
        final String language = properties.getProperty(prefix + "_language");
        final String byteconverter = properties.getProperty(prefix + "_byteconverter");
        if (name == null || name.trim().equals("") || language == null || language.trim().equals("") || Language.lookupLanguage(language) == null || byteconverter == null || byteconverter.trim().equals("")) {
            CharacterEncodingManager._logger.warning("Insufficient information for encoding: " + name + " (prefix:" + prefix + ")");
            return;
        }
        final String aliases = properties.getProperty(prefix + "_aliases");
        final String[] aliasList = (String[])((aliases == null) ? null : this.parseAliasList(aliases));
        final ByteConverter converter = byteconverter.equals("none") ? null : this.lookupConverter(byteconverter);
        final Language lang = Language.lookupLanguage(language);
        final String cannotEncode = properties.getProperty(prefix + "_cannotencode");
        final boolean canEncode = cannotEncode == null || !cannotEncode.trim().toLowerCase().equals("true");
        final String cannotDecode = properties.getProperty(prefix + "_cannotdecode");
        final boolean canDecode = cannotDecode == null || !cannotDecode.trim().toLowerCase().equals("true");
        this.addEncodingToGlobalList(name, aliasList, lang, converter, prefix, canEncode, canDecode);
    }
    
    private void addEncodingToGlobalList(String name, final String[] aliasList, final Language lang, final ByteConverter converter, final String prefix, final boolean canEncode, final boolean canDecode) {
        name = name.trim();
        final CharacterEncoding encoding = new CharacterEncoding(name, aliasList, lang, converter, prefix, canEncode, canDecode);
        this._allEncodings.add(encoding);
        final String characterSet = this.normalize(name);
        if (this._fastAliasLookupMap.containsKey(characterSet)) {
            CharacterEncodingManager._logger.warning("Duplicate encoding name: <" + name + "> mapped to " + this._fastAliasLookupMap.get(characterSet) + " and " + encoding);
        }
        else {
            this._fastAliasLookupMap.put(characterSet, encoding);
        }
        if (aliasList != null) {
            for (final String alias : aliasList) {
                if (this._fastAliasLookupMap.containsKey(alias)) {
                    if (this._fastAliasLookupMap.get(alias) != encoding) {
                        final CharacterEncoding alternate = this._fastAliasLookupMap.get(alias);
                        CharacterEncodingManager._logger.warning("Duplicate encoding name for alias: <" + alias + "> mapped to " + alternate + " and " + encoding);
                    }
                }
                else {
                    this._fastAliasLookupMap.put(alias, encoding);
                }
            }
        }
    }
    
    private String normalize(String charSetName) {
        final StringBuilder output = new StringBuilder();
        charSetName = charSetName.trim();
        for (int i = 0; i < charSetName.length(); ++i) {
            final char c = charSetName.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                output.append(Character.toUpperCase(c));
            }
        }
        return output.toString();
    }
    
    private String[] parseAliasList(final String list) {
        final String[] aliases = list.split(",");
        for (int i = 0; i < aliases.length; ++i) {
            aliases[i] = this.normalize(aliases[i]);
        }
        return aliases;
    }
    
    private ByteConverter lookupConverter(final String className) {
        try {
            return (ByteConverter)Class.forName(className).newInstance();
        }
        catch (ClassNotFoundException e) {
            CharacterEncodingManager._logger.warning("Cound not find byte converter class: " + className);
            return null;
        }
        catch (InstantiationException e2) {
            CharacterEncodingManager._logger.warning("Cound not instanciate byte converter class: " + className);
            return null;
        }
        catch (IllegalAccessException e3) {
            CharacterEncodingManager._logger.warning("Cound not instanciate byte converter class: " + className);
            return null;
        }
    }
    
    public Properties loadEncodingProperties(final String uriName) {
        final URI uri = URI.create("resource:" + uriName);
        final Properties currentFileProperties = new Properties();
        try {
            currentFileProperties.load(URILoader.load(uri));
        }
        catch (IOException e) {
            throw new RuntimeException("Error loading character encoding file: " + uriName + " - " + e.getMessage());
        }
        return currentFileProperties;
    }
    
    public int getNumEncodings() {
        return this._allEncodings.size();
    }
    
    public int getNumAliases() {
        return this._fastAliasLookupMap.keySet().size();
    }
    
    List<CharacterEncoding> getAllEncodings() {
        return new ArrayList<CharacterEncoding>(this._allEncodings);
    }
    
    public void recordUsage(final CharacterEncoding encoding) {
        if (this._countUsage == null) {
            this._countUsage = new HashMap<CharacterEncoding, Integer>();
        }
        Integer c = 1;
        if (this._countUsage.containsKey(encoding)) {
            c = this._countUsage.get(encoding) + 1;
            this._countUsage.remove(encoding);
        }
        this._countUsage.put(encoding, c);
    }
    
    public void resetUsageCount() {
        this._countUsage = null;
    }
    
    public Map<CharacterEncoding, Integer> getUsage() {
        return (this._countUsage == null) ? new HashMap<CharacterEncoding, Integer>() : new HashMap<CharacterEncoding, Integer>(this._countUsage);
    }
    
    public CharacterEncoding getDefault() {
        return this.getEncoding("UTF-8");
    }
    
    public BestGuessResult guessEncodingAndDecode(final byte[] original) {
        return this._encodingGuesser.guessEncoding(original);
    }
    
    public static synchronized CharacterEncodingManager getInstance() {
        if (CharacterEncodingManager._instance == null) {
            CharacterEncodingManager._instance = new CharacterEncodingManager(true);
        }
        return CharacterEncodingManager._instance;
    }
    
    public static synchronized void setInstance(final CharacterEncodingManager instance) {
        CharacterEncodingManager._instance = instance;
    }
    
    public static CharacterEncodingManager getNewIndependentInstance(final boolean generateUnknownEncodings) {
        return new CharacterEncodingManager(generateUnknownEncodings);
    }
    
    public synchronized void updateStettings(final Properties properties) {
        if (properties == null) {
            return;
        }
        final String enabled = properties.getProperty("EncodingGuessingEnabled", "off").trim().toLowerCase();
        if ((enabled.equals("on") || enabled.equals("true")) && this.isIcuLibraryLoaded()) {
            this.assignGuesser(new IcuGuesser(properties));
        }
        else {
            this.assignGuesser(new NotGuessingGuesser(properties));
        }
    }
    
    private void assignGuesser() {
        this.assignGuesser(this.isIcuLibraryLoaded() ? new IcuGuesser() : new NotGuessingGuesser());
    }
    
    private void assignGuesser(final Guesser guesser) {
        this._encodingGuesser = guesser;
        CharacterEncodingManager._logger.info("Using new Guesser: " + guesser.getName());
    }
    
    public boolean isIcuLibraryLoaded() {
        try {
            Class.forName("com.ibm.icu.text.CharsetDetector");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    private static String getPropertiesFile() {
        final String settingsProperty = System.getProperty("com.vontu.unicode.encodings");
        if (settingsProperty == null || settingsProperty.trim().equals("")) {
            return "/com/vontu/util/unicode/characterEncoding.properties";
        }
        return settingsProperty;
    }
    
    Guesser getEncodingGuesser() {
        return this._encodingGuesser;
    }
    
    @Override
    public String toString() {
        return "CharSets:" + this._allEncodings.size() + "," + "Encodings:" + this._fastAliasLookupMap.keySet().size() + "," + "Guesser: " + this._encodingGuesser.getName();
    }
    
    static {
        CharacterEncodingManager._instance = null;
        _logger = Logger.getLogger(CharacterEncodingManager.class.getName());
    }
}
