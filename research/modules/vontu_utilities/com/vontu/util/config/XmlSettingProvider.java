// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

import org.xml.sax.Attributes;
import javax.xml.parsers.SAXParser;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class XmlSettingProvider implements SettingProvider
{
    private final Logger _logger;
    private final Map<String, String> _settings;
    
    public XmlSettingProvider(final String pathSystemProperty) {
        this(pathSystemProperty, null, null);
    }
    
    public XmlSettingProvider(final String pathSystemProperty, final String sectionName) {
        this(pathSystemProperty, sectionName, null);
    }
    
    public XmlSettingProvider(final String pathSystemProperty, final String sectionName, final Logger logger) {
        this._settings = new HashMap<String, String>();
        this._logger = logger;
        final String propertyFilePath = System.getProperty(pathSystemProperty);
        if (propertyFilePath == null) {
            this.logWarining("Configuration property " + pathSystemProperty + " isn't set." + " Using default configuration values.");
        }
        else {
            try {
                final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                parser.parse(propertyFilePath, new XmlHandler(sectionName, this._settings));
            }
            catch (Exception e) {
                this.logWarining("Configuration file \"" + propertyFilePath + "\" can't be read." + " Using default configuration values.");
            }
        }
    }
    
    private void logWarining(final String message) {
        if (this._logger != null) {
            this._logger.warning(message);
        }
    }
    
    @Override
    public String getSetting(final String name) {
        return this._settings.get(name);
    }
    
    public Map<String, String> getSettingMap() {
        return new HashMap<String, String>(this._settings);
    }
    
    private static final class XmlHandler extends DefaultHandler
    {
        private static final String _NAME_TAG = "name";
        private static final String _VALUE_TAG = "value";
        private static final int _NOT_READING = 0;
        private static final int _READING_NAME = 1;
        private static final int _EXPECTING_VALUE = 2;
        private static final int _READING_VALUE = 3;
        private String _name;
        private final String _namePrefix;
        private final Map<String, String> _settings;
        private int _state;
        private StringBuffer _stringBuilder;
        
        XmlHandler(final String sectionName, final Map<String, String> settings) {
            this._namePrefix = ((sectionName != null) ? (sectionName + '.') : null);
            this._settings = settings;
            this._state = 0;
        }
        
        @Override
        public void characters(final char[] ch, final int start, final int length) {
            if (this._state == 3 || this._state == 1) {
                this._stringBuilder.append(ch, start, length);
            }
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {
            if (this._state == 2 && "value".equals(qName)) {
                this._state = 3;
                this._stringBuilder = new StringBuffer();
            }
            else if ("name".equals(qName)) {
                this._state = 1;
                this._stringBuilder = new StringBuffer();
            }
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String qName) {
            if (this._state == 3 && "value".equals(qName)) {
                this._settings.put(this._name, this._stringBuilder.toString());
                this._state = 0;
            }
            else if ("name".equals(qName)) {
                this._name = this._stringBuilder.toString();
                if (this._namePrefix == null || this._name.startsWith(this._namePrefix)) {
                    this._state = 2;
                }
                else {
                    this._state = 0;
                }
            }
        }
    }
}
