// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.vontu.util.collection.Converter;
import java.util.LinkedHashSet;
import java.util.Set;
import com.vontu.util.CSVString;
import java.util.Arrays;
import com.vontu.util.StringUtil;
import java.util.Collection;
import com.vontu.util.RegExUtils;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.OverlappingFileLockException;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import com.vontu.util.unicode.CharacterEncoding;
import com.vontu.util.unicode.UnsupportedEncodingException;
import com.vontu.util.unicode.CharacterEncodingManager;
import com.vontu.util.unicode.CharacterConversionException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public class FileUtil
{
    private static final Logger logger;
    public static final String VALID_FILE_NAME_DELIMITERS = "[\r\n,]";
    
    public static void writeFileFromInputStream(final String fileName, final InputStream inputStream) throws IOException {
        final File file = new File(fileName);
        final FileOutputStream os = new FileOutputStream(file);
        final byte[] buffer = new byte[4096];
        int bytes_read;
        while ((bytes_read = inputStream.read(buffer)) != -1) {
            os.write(buffer, 0, bytes_read);
        }
        os.close();
    }
    
    public static boolean deleteDirectoryRecursively(final File root) {
        if (root.exists()) {
            final File[] files = root.listFiles();
            if (files != null) {
                deleteChildren(root);
            }
            return root.delete();
        }
        return true;
    }
    
    public static void deleteChildren(final File root) {
        for (final File file : root.listFiles()) {
            if (file.isDirectory()) {
                deleteDirectoryRecursively(file);
            }
            else {
                file.delete();
            }
        }
    }
    
    public static CharSequence getContentFromFile(final String fileName, final String encoding) throws IOException, CharacterConversionException {
        final File file = new File(fileName);
        return getContentFromFile(file, encoding);
    }
    
    public static CharSequence getContentFromFile(final File file, final String encoding) throws IOException, CharacterConversionException {
        final byte[] bytes = getByteArrayFromFile(file);
        if (encoding != null) {
            try {
                final CharacterEncoding enc = CharacterEncodingManager.getInstance().getEncoding(encoding);
                return enc.convert(bytes);
            }
            catch (UnsupportedEncodingException ex) {}
        }
        return CharacterEncodingManager.getInstance().guessEncodingAndDecode(bytes).getDecodedContent();
    }
    
    public static byte[] getByteArrayFromFile(final File file) throws IOException {
        final InputStream is = new FileInputStream(file);
        final long length = file.length();
        if (length > 2147483647L) {
            throw new IOException("File " + file.toString() + " is too large");
        }
        final byte[] bytes = new byte[(int)length];
        int offset = 0;
        for (int numRead = 0; offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead) {}
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.toString());
        }
        is.close();
        return bytes;
    }
    
    public static CharSequence getContentFromStream(final InputStream file, final long length, final String encoding) throws IOException, CharacterConversionException {
        final byte[] bytes = getByteArrayFromStream(file, length);
        if (encoding != null) {
            try {
                final CharacterEncoding enc = CharacterEncodingManager.getInstance().getEncoding(encoding);
                return enc.convert(bytes);
            }
            catch (UnsupportedEncodingException ex) {}
        }
        return CharacterEncodingManager.getInstance().guessEncodingAndDecode(bytes).getDecodedContent();
    }
    
    public static byte[] getByteArrayFromStream(final InputStream is, final long length) throws IOException {
        if (length > 2147483647L) {
            throw new IOException("File is too large");
        }
        final byte[] bytes = new byte[(int)length];
        int offset = 0;
        for (int numRead = 0; offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead) {}
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file ");
        }
        is.close();
        return bytes;
    }
    
    public static void copyFile(final File src, final File dest) throws IOException {
        BufferedInputStream srcStream = null;
        BufferedOutputStream destStream = null;
        try {
            srcStream = new BufferedInputStream(new FileInputStream(src));
            destStream = new BufferedOutputStream(new FileOutputStream(dest));
            int b;
            while ((b = srcStream.read()) != -1) {
                destStream.write(b);
            }
        }
        finally {
            if (srcStream != null) {
                srcStream.close();
            }
            if (destStream != null) {
                destStream.close();
            }
        }
    }
    
    public static String createUrl(final String path) {
        if (path == null) {
            return null;
        }
        final String url = path.replace('/', '\\');
        if (url.startsWith("\\\\")) {
            return "file://" + url;
        }
        return null;
    }
    
    public static String createFirefoxFriendlyUrl(final String path) {
        if (path == null) {
            return null;
        }
        final String url = path.replace('\\', '/');
        if (url.startsWith("//")) {
            return "file:///" + url;
        }
        return null;
    }
    
    public static String getFileName(final String path) {
        if (path == null) {
            return null;
        }
        return path.substring(path.replace('\\', '/').lastIndexOf(47) + 1);
    }
    
    public static String getParentDirectory(final String path) {
        if (path == null) {
            return null;
        }
        final int lastDirectorySeparator = path.replace('\\', '/').lastIndexOf(47);
        if (lastDirectorySeparator == -1) {
            return null;
        }
        return path.substring(0, lastDirectorySeparator);
    }
    
    public static String getByteString(final long totalBytes) {
        final DecimalFormat format = new DecimalFormat("#,###.00");
        String byteString;
        if (totalBytes >= 1073741824L) {
            final double gb = totalBytes / 1.073741824E9;
            byteString = format.format(gb) + " GB";
        }
        else if (totalBytes >= 1048576L) {
            final double mb = totalBytes / 1048576.0;
            byteString = format.format(mb) + " MB";
        }
        else if (totalBytes >= 1024L) {
            final double kb = totalBytes / 1024.0;
            byteString = format.format(kb) + " KB";
        }
        else {
            byteString = String.valueOf(totalBytes) + " Bytes";
        }
        return byteString;
    }
    
    public static RandomAccessFile openAndLockFile(final File file) throws VontuFileException {
        RandomAccessFile raFile = null;
        try {
            raFile = new RandomAccessFile(file, "rw");
        }
        catch (FileNotFoundException e) {
            throw new VontuFileException("Unable to open file \"" + file.getAbsolutePath() + "\" because it's possibly locked. Skipping.", e);
        }
        try {
            if (raFile.getChannel().tryLock() == null) {
                try {
                    raFile.close();
                }
                catch (IOException ex) {}
                throw new VontuFileException("Unable to lock file \"" + file.getAbsolutePath() + "\". Skipping.");
            }
            if (raFile.length() == 0L) {
                raFile.close();
                if (!file.delete()) {
                    throw new VontuFileException("Unable to delete zero length file: " + file.getName());
                }
                return null;
            }
        }
        catch (OverlappingFileLockException e2) {
            try {
                raFile.close();
            }
            catch (IOException ex2) {}
            throw new VontuFileException("Unable to lock file \"" + file.getAbsolutePath() + "\". Skipping.");
        }
        catch (IOException e3) {
            try {
                raFile.close();
            }
            catch (IOException ex3) {}
            if (!file.delete()) {
                throw new VontuFileException("Unable to delete file: " + file.getName());
            }
            return null;
        }
        return raFile;
    }
    
    public static void close(final Writer device) {
        if (device != null) {
            try {
                device.close();
            }
            catch (IOException ex) {}
        }
    }
    
    public static void close(final Reader device) {
        if (device != null) {
            try {
                device.close();
            }
            catch (IOException ex) {}
        }
    }
    
    public static void close(final OutputStream device) {
        if (device != null) {
            try {
                device.close();
            }
            catch (IOException ex) {}
        }
    }
    
    public static void close(final InputStream device) {
        if (device != null) {
            try {
                device.close();
            }
            catch (IOException ex) {}
        }
    }
    
    public static String[] getParsedFileNames(final String fileExtensions) {
        return RegExUtils.parseOnRegex(fileExtensions, "[\r\n,]");
    }
    
    public static Collection<String> getFileNamePatterns(final String fileNames) {
        if (StringUtil.isEmptyTrimmed(fileNames)) {
            throw new IllegalArgumentException("Pattern must be set and not empty.");
        }
        return Arrays.asList(getParsedFileNames(fileNames));
    }
    
    public static String setFileNamePatterns(final Collection<String> patterns, final String seperatorPattern) {
        final CSVString<String> out = new CSVString<String>();
        out.addAll(patterns);
        out.setSeparator(seperatorPattern);
        return out.toString();
    }
    
    public static Set<String> getDocumentTypes(final String typesCsv) {
        final Set<String> documentTypes = new LinkedHashSet<String>();
        final CSVString<String> in = new CSVString<String>(typesCsv);
        if (!in.isEmpty()) {
            documentTypes.addAll(in.getValues(new StringToLowerConverter()));
        }
        return documentTypes;
    }
    
    public static String createTimeStampFileNameWithFullPath(final String parentFolder, final String filename) {
        return parentFolder + File.separator + createTimeStampFileName(filename);
    }
    
    public static String createTimeStampFileName(final String filename) {
        final SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        final Date d = new Date();
        return format.format(d) + "_" + filename;
    }
    
    public static boolean validateAndCreateFolder(final File folder) {
        return (!folder.exists() || folder.isDirectory()) && (folder.exists() || folder.mkdirs());
    }
    
    public static void writeDataToFile(final File f, final List<String> data) throws Exception {
        final OutputStream bo = new BufferedOutputStream(new FileOutputStream(f));
        for (final String d : data) {
            bo.write(d.getBytes());
            bo.write("\n".getBytes());
        }
        bo.close();
    }
    
    public static boolean writeDataToZipEntry(final String zipEntryName, final ZipOutputStream zipStream, final List<String> data) throws IOException {
        try {
            zipStream.putNextEntry(new ZipEntry(zipEntryName));
            for (final String d : data) {
                zipStream.write(d.getBytes());
                zipStream.write("\n\r".getBytes());
            }
        }
        finally {
            zipStream.closeEntry();
        }
        return data.size() > 0;
    }
    
    public static void zipFiles(final String outputFileName, final List<File> filesToZip) throws Exception {
        final OutputStream out = new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(outputFileName)));
        final ZipOutputStream zipFile = new ZipOutputStream(out);
        zipFile.setLevel(-1);
        final byte[] buffer = new byte[1024];
        int entries = 0;
        for (final File f : filesToZip) {
            if (FileUtil.logger.isLoggable(Level.FINE)) {
                FileUtil.logger.log(Level.FINE, "zipFiles trying to add " + f.getName() + " entry to the zip file " + outputFileName);
            }
            if (f.exists()) {
                final InputStream is = new BufferedInputStream(new FileInputStream(f));
                zipFile.putNextEntry(new ZipEntry(f.getName()));
                int len;
                while ((len = is.read(buffer)) > 0) {
                    zipFile.write(buffer, 0, len);
                }
                is.close();
                zipFile.closeEntry();
                ++entries;
            }
        }
        if (entries > 0) {
            if (FileUtil.logger.isLoggable(Level.FINE)) {
                FileUtil.logger.log(Level.FINE, "No Entries were added to the zip file - " + outputFileName);
            }
            zipFile.close();
        }
        else {
            if (FileUtil.logger.isLoggable(Level.FINE)) {
                FileUtil.logger.log(Level.FINE, entries + " entries were added to zip file - " + outputFileName);
            }
            out.close();
            final File f2 = new File(outputFileName);
            f2.delete();
        }
    }
    
    static {
        logger = Logger.getLogger(FileUtil.class.getName());
    }
    
    public static class StringToLowerConverter implements Converter<String, String>
    {
        @Override
        public String convert(final String source) {
            return source.trim().toLowerCase();
        }
    }
}
