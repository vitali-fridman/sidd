// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import java.io.EOFException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.logging.Logger;

public class ChunkDataSource
{
    public static void main(final String[] args) {
        final Logger _logger = Logger.getLogger(ChunkDataSource.class.getName());
        final File infile = new File(args[0] + ".dat");
        final int numberRecords = Integer.parseInt(args[1]);
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(infile);
            bufferedReader = new BufferedReader(fileReader);
        }
        catch (IOException exception) {
            _logger.log(Level.SEVERE, "Could not open input file " + args[0], exception);
            System.exit(-1);
        }
        boolean notDone = true;
        int outCount = 1;
        while (notDone) {
            FileWriter fileWriter = null;
            final File outFile = new File(args[0] + "." + Integer.toString(outCount) + ".dat");
            try {
                fileWriter = new FileWriter(outFile);
                for (int i = 0; i < numberRecords; ++i) {
                    fileWriter.write(bufferedReader.readLine());
                    fileWriter.write("\r\n");
                }
                fileWriter.close();
            }
            catch (EOFException exception2) {
                notDone = false;
            }
            catch (IOException exception3) {
                notDone = false;
            }
            finally {
                try {
                    fileWriter.close();
                }
                catch (IOException ex) {}
            }
            ++outCount;
        }
    }
}
