// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.mortbay.util.URI;

import com.hunnysoft.jmime.Mailbox;
import com.hunnysoft.jmime.ByteString;

public class NetworkAddressUtil
{
    private static final String NNTP_POSTING = "NNTP Posting";
    private static final String AT_SIGN = "@";
    private static final String DOT = ".";
    private static final String REGEX_DOT = "\\.";
    
    public static String getEmailDomain(final String email) {
        final Mailbox mailbox = new Mailbox(new ByteString(email));
        mailbox.parse();
        return mailbox.getDomain().toString().toLowerCase();
    }
    
    public static String getURLDomain(final String URL) {
        final URI parser = new URI(URL);
        String result = parser.getHost();
        if (result == null) {
            result = URL;
        }
        result = getDomain(result);
        return result.toLowerCase();
    }
    
    public static String getNewsGroupDomain(final String newsGroup) {
        return "NNTP Posting";
    }
    
    public static boolean isValidIPv4Address(final String address) {
        final String[] strTok = address.split("\\.");
        if (strTok.length != 4) {
            return false;
        }
        for (final String token : strTok) {
            try {
                final int number = Integer.parseInt(token);
                if (number < 0 || number > 255) {
                    return false;
                }
            }
            catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
    
    private static String getDomain(final String host) {
        if (host == null) {
            return "Unknown";
        }
        if (isValidIPv4Address(host)) {
            return host;
        }
        final StringTokenizer st = new StringTokenizer(host, ".");
        final LinkedList elements = new LinkedList();
        final int numTokens = st.countTokens();
        while (st.hasMoreTokens()) {
            elements.addFirst(st.nextToken());
        }
        final StringBuffer result = new StringBuffer();
        final String[] segs = { null, null, null };
        for (int i = 0; i < numTokens && i < 3; ++i) {
            segs[i] = (String) elements.get(i);
        }
        if (segs[2] != null && segs[0].length() == 2) {
            result.append(segs[2]);
            result.append(".");
        }
        if (segs[1] != null) {
            result.append(segs[1]);
            result.append(".");
        }
        if (segs[0] != null) {
            result.append(segs[0]);
        }
        return result.toString();
    }
    
    public static String getShortEmail(final String emailAddress) {
        if (emailAddress == null) {
            return emailAddress;
        }
        final int start = emailAddress.lastIndexOf(60);
        final int end = emailAddress.lastIndexOf(62);
        final boolean needShorten = start >= 0 && end >= 0 && start < end;
        if (needShorten) {
            return emailAddress.substring(start + 1, end).replaceAll("\"", "");
        }
        return emailAddress.replaceAll("\"", "");
    }
    
    public static boolean isEmailMatch(final String pattern, final String email) {
        if (pattern == null || email == null) {
            return false;
        }
        final String domain = getEmailDomain(email);
        if (domain == null) {
            return false;
        }
        if (pattern.indexOf("@") == -1) {
            final String[] patternParts = pattern.split("\\.");
            final String[] emailParts = domain.split("\\.");
            if (patternParts.length == emailParts.length) {
                if (domain.compareToIgnoreCase(pattern) == 0) {
                    return true;
                }
            }
            else if (patternParts.length < emailParts.length && domain.toLowerCase().endsWith("." + pattern)) {
                return true;
            }
        }
        else if (email.toLowerCase().indexOf(pattern) != -1) {
            return true;
        }
        return false;
    }
    
    public static boolean isNewsgroupMatch(final String pattern, final String newsgroup) {
        if (pattern == null || newsgroup == null) {
            return false;
        }
        final String[] patternParts = pattern.split("\\.");
        final String[] newsgroupParts = newsgroup.split("\\.");
        if (patternParts.length == newsgroupParts.length) {
            if (newsgroup.compareToIgnoreCase(pattern) == 0) {
                return true;
            }
        }
        else if (patternParts.length < newsgroupParts.length && newsgroup.toLowerCase().startsWith(pattern + ".")) {
            return true;
        }
        return false;
    }
    
    public static boolean isUsernameMatch(final String pattern, final String username) {
        return pattern != null && username != null && username.toLowerCase().startsWith(pattern);
    }
    
    public static boolean isURLMatch(final String pattern, final String url) {
        if (pattern == null || url == null) {
            return false;
        }
        final URI uri = new URI(url);
        if (uri == null) {
            return false;
        }
        final String host = uri.getHost().toLowerCase();
        if (pattern.charAt(0) != '*') {
            return host.equals(pattern);
        }
        return host.endsWith(pattern.substring(1));
    }
    
    public static boolean isIPAddressMatch(final String pattern, final String ipAddress) {
        if (pattern == null || ipAddress == null) {
            return false;
        }
        final String patternRegex = convertWildCardToRegex(pattern);
        final Pattern patternRegexPattern = Pattern.compile(patternRegex);
        final Matcher matcher = patternRegexPattern.matcher(ipAddress);
        return matcher.find();
    }
    
    public static String convertWildCardToRegex(final String patternStr) {
        final StringBuilder convertedPattern = new StringBuilder();
        final String replacedStr = patternStr.replace(',', '|');
        for (int i = 0; i < replacedStr.length(); ++i) {
            if (Character.isDigit(replacedStr.charAt(i))) {
                convertedPattern.append(replacedStr.charAt(i));
            }
            else if (replacedStr.charAt(i) == '.') {
                convertedPattern.append("\\.");
            }
            else if (replacedStr.charAt(i) == '*') {
                convertedPattern.append("\\d{1,3}");
            }
            else if (replacedStr.charAt(i) == '|') {
                convertedPattern.append("|");
            }
        }
        return convertedPattern.toString();
    }
}
