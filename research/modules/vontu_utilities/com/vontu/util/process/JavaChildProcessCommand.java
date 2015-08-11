// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import java.util.Arrays;
import java.io.File;

public final class JavaChildProcessCommand
{
    private static final String[] NO_ARGS;
    
    public static String[] create(final String className, final String[] args) {
        return create(className, args, null, JavaChildProcessCommand.NO_ARGS, System.getProperty("java.class.path"));
    }
    
    public static String[] create(final String className, final String[] args, final String logConfigFile, final String[] vmParameters) {
        return create(className, args, logConfigFile, vmParameters, System.getProperty("java.class.path"));
    }
    
    public static String[] create(final String className, final String[] args, final String logConfigFile, final String[] vmParameters, final String classPath) {
        final CommandBuilder commandBuilder = new CommandBuilder();
        commandBuilder.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        if (new SixtyFourOrThirtyTwoBitDeterminer().isSixtyFourBitJVM()) {
            commandBuilder.append("-XX:+UseCompressedOops");
        }
        commandBuilder.append("-server");
        commandBuilder.append("-classpath");
        commandBuilder.append(classPath);
        commandBuilder.appendSystemProperty("com.vontu.properties");
        commandBuilder.append("-Djava.util.logging.config.file", logConfigFile);
        commandBuilder.append("-Djava.library.path", getJavaLibraryPath());
        commandBuilder.append("-Djava.protocol.handler.pkgs", "com.vontu.util.urlconnection");
        commandBuilder.appendSystemProperty("java.endorsed.dirs");
        if (!Arrays.asList(vmParameters).contains("-Xrs")) {
            commandBuilder.append("-Xrs");
        }
        commandBuilder.append(vmParameters);
        commandBuilder.append(CommandBuilder.fromProperty(className + ".vmParameters"));
        commandBuilder.append(className);
        commandBuilder.append(args);
        return commandBuilder.getCommand();
    }
    
    public static String getJavaLibraryPath() {
        String javaLibraryPath = System.getProperty("java.library.path");
        if (System.getProperty("os.name").startsWith("Windows")) {
            while (javaLibraryPath.charAt(javaLibraryPath.length() - 1) == '\\') {
                javaLibraryPath = javaLibraryPath.substring(0, javaLibraryPath.length() - 1);
            }
        }
        return javaLibraryPath;
    }
    
    static {
        NO_ARGS = new String[0];
    }
}
