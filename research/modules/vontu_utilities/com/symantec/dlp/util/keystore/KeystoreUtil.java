// 
// Decompiled by Procyon v0.5.29
// 

package com.symantec.dlp.util.keystore;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Collection;
import java.util.Arrays;
import java.security.KeyStoreException;
import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedList;
import java.security.cert.Certificate;
import java.util.List;
import java.security.KeyStore;

public class KeystoreUtil
{
    public static List<Certificate> getTrustedCertificates(final KeyStore keystore) throws KeyStoreException {
        final List<Certificate> certificates = new LinkedList<Certificate>();
        for (final String alias : Collections.list(keystore.aliases())) {
            if (keystore.entryInstanceOf(alias, KeyStore.TrustedCertificateEntry.class)) {
                certificates.add(keystore.getCertificate(alias));
            }
        }
        return certificates;
    }
    
    public static List<Certificate> getChainedCertificates(final KeyStore keystore) throws KeyStoreException {
        final List<Certificate> certificates = new LinkedList<Certificate>();
        for (final String alias : Collections.list(keystore.aliases())) {
            if (keystore.entryInstanceOf(alias, KeyStore.PrivateKeyEntry.class)) {
                certificates.addAll(Arrays.asList(keystore.getCertificateChain(alias)));
            }
        }
        return certificates;
    }
    
    public static boolean isValid(final KeyStore keystore) throws KeyStoreException {
        final Date currentDate = new Date();
        for (final Certificate certificate : getChainedCertificates(keystore)) {
            final X509Certificate x509Certificate = (X509Certificate)certificate;
            if (currentDate.after(x509Certificate.getNotAfter())) {
                return false;
            }
        }
        for (final Certificate certificate : getTrustedCertificates(keystore)) {
            final X509Certificate x509Certificate = (X509Certificate)certificate;
            if (currentDate.after(x509Certificate.getNotAfter())) {
                return false;
            }
        }
        return true;
    }
    
    public static String parseSubjectAltNameType(final int typeCode) {
        switch (typeCode) {
            case 49: {
                return "EMAIL";
            }
            case 50: {
                return "DNS";
            }
            case 54: {
                return "URI";
            }
            case 55: {
                return "IP";
            }
            case 56: {
                return "OID";
            }
            default: {
                return String.valueOf(typeCode);
            }
        }
    }
}
