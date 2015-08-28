// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import java.io.IOException;
import com.vontu.profileindexer.document.MessageDigestCreator;
import com.vontu.profileindexer.document.EndpointIndexProvider;
import com.vontu.profileindexer.document.EndpointCurrentIndexProvider;
import com.vontu.profileindexer.document.EndpointIndexWriter;
import com.vontu.profileindexer.document.MessageDigestCreatorFactory;
import com.vontu.util.config.SettingProvider;
import com.vontu.util.config.SettingReader;
import com.vontu.profileindexer.document.EndpointDocumentIndexCreator;

public class EndpointIndexCreatorFactory
{
    public static final String USE_JAVA_MD5 = "use_java_md5";
    public static final boolean USE_JAVA_MD5_DEFAULT = false;
    
    public static EndpointDocumentIndexCreator create(final IndexerSettingProvider settings, final boolean incremental, final DocumentIndexFileGeneratorImpl docIndexFileGen) throws IOException {
        final SettingReader settingReader = new SettingReader((SettingProvider)settings);
        final boolean useJavaMD5 = settingReader.getBooleanSetting("use_java_md5", false);
        final MessageDigestCreator messageDigestCreator = MessageDigestCreatorFactory.createMessageDigestCreator(useJavaMD5);
        final EndpointIndexWriter endpointIndexWriter = new EndpointIndexWriter(docIndexFileGen.getEndpointIndexFile());
        if (incremental) {
            return new EndpointDocumentIndexCreator(messageDigestCreator, endpointIndexWriter, (EndpointIndexProvider)new EndpointCurrentIndexProvider(docIndexFileGen.getPrevEndpointIndexFile()));
        }
        return new EndpointDocumentIndexCreator(messageDigestCreator, endpointIndexWriter);
    }
}
