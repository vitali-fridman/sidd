// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.passwordstore;

import java.io.IOException;
import com.vontu.util.jdbc.DatabasePasswordProperties;
import java.io.File;

public class DBPasswordChanger
{
    public static void main(final String[] args) {
        if (args.length != 2) {
            System.out.println("This utility changes the Oracle passwords used by Vontu Enforce.  Before using\nit, ensure that enforce has been shut down and that the password has been\nchanged within Oracle.\n\nUsage: DBPasswordChanger [Password File] [New Oracle Password]\n");
            System.exit(0);
        }
        final File passwordFile = new File(args[0]);
        final String newPassword = args[1];
        try {
            final DatabasePasswordProperties passwords = new DatabasePasswordProperties(passwordFile);
            final String oldDatabasePassword = passwords.getDatabasePassword();
            if (oldDatabasePassword == null) {
                System.err.println("Password properties file \"" + passwordFile + "\" did not previously contain a password.\n" + "Verify that you have updated the correct file.");
            }
            passwords.setDatabasePassword(newPassword);
            System.out.println("Password changed.");
        }
        catch (IOException e) {
            System.err.println("Password properties file could not be read: " + e.getMessage());
            System.exit(1);
        }
    }
}
