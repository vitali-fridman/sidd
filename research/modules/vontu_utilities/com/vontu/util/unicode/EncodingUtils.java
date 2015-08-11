// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import com.vontu.util.StringUtil;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.util.logging.Logger;

public class EncodingUtils
{
    private static final Logger logger;
    
    public static boolean skipUtf8Bom(final BufferedInputStream input) throws IOException {
        input.mark(4);
        final byte[] bomBytes = new byte[3];
        if (input.read(bomBytes) != 3 || bomBytes[0] != -17 || bomBytes[1] != -69 || bomBytes[2] != -65) {
            input.reset();
            return false;
        }
        return true;
    }
    
    public static void skipUtf8Bom(final Charset charset, final BufferedInputStream inputStream) throws IOException {
        if (!StandardCharsets.UTF_8.equals(charset)) {
            EncodingUtils.logger.finer("Encoding [" + charset + "] is not " + StandardCharsets.UTF_8 + ".");
            return;
        }
        skipUtf8Bom(inputStream);
    }
    
    public static String toAsciiHexString(final String originalFileName, final String encoding) {
        final CharacterEncoding managerEncoding = CharacterEncodingManager.getInstance().getEncoding(encoding);
        byte[] encodedBytes;
        try {
            if (managerEncoding != null) {
                encodedBytes = managerEncoding.convert(originalFileName);
            }
            else {
                encodedBytes = originalFileName.getBytes();
            }
        }
        catch (CharacterConversionException e) {
            encodedBytes = originalFileName.getBytes();
        }
        catch (UnsupportedEncodingException e2) {
            encodedBytes = originalFileName.getBytes();
        }
        final StringBuilder builder = new StringBuilder();
        for (final byte current : encodedBytes) {
            if (current == 46 || (current >= 48 && current <= 57) || (current >= 97 && current <= 122) || (current >= 65 && current <= 90)) {
                builder.append((char)current);
            }
            else {
                final String hex = Integer.toHexString(current & 0xFF);
                builder.append("%");
                builder.append(StringUtil.addZeroPadding(hex, 2));
            }
        }
        return builder.toString();
    }
    
    static {
        logger = Logger.getLogger(EncodingUtils.class.getName());
    }
}
