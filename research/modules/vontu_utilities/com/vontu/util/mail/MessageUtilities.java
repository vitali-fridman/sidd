// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.mail;

import com.hunnysoft.jmime.Message;
import java.util.Iterator;
import com.vontu.util.bytestring.ByteStringUtilities;
import java.util.ArrayList;
import com.hunnysoft.jmime.FieldBody;
import com.hunnysoft.jmime.Field;
import com.hunnysoft.jmime.AddressList;
import com.vontu.util.NetworkAddressUtil;
import com.hunnysoft.jmime.Address;
import java.util.HashSet;
import com.hunnysoft.jmime.Headers;
import com.hunnysoft.jmime.ByteStringBuffer;
import com.hunnysoft.jmime.ByteString;

public class MessageUtilities
{
    private static final ByteString START_MESSAGE_MARKER;
    private static final ByteString END_LINE_MARKER;
    private static final ByteString RCPT_TO;
    private static final ByteString RCPT_UTF7_AT;
    public static final ByteString APPARENTLY_TO;
    public static final ByteString X_APPARENTLY_TO;
    
    public static int byteStringIndexOf(final ByteString data, final int start, final ByteString token, final boolean ignoreCase) {
        for (int i = start; i + token.length() <= data.length(); ++i) {
            if (data.regionMatches(ignoreCase, i, token, 0, token.length())) {
                return i;
            }
        }
        return -1;
    }
    
    public static ByteString fixRcptUtf7(final ByteString rcpt) {
        if (rcpt.indexOf(64) != -1) {
            return rcpt;
        }
        final int utf7replaceIndex = rcpt.indexOf(MessageUtilities.RCPT_UTF7_AT);
        if (utf7replaceIndex == -1) {
            return rcpt;
        }
        final ByteStringBuffer out = new ByteStringBuffer();
        out.append(rcpt.substring(0, utf7replaceIndex));
        out.append("@");
        out.append(rcpt.substring(utf7replaceIndex + 5, rcpt.length()));
        return out.toByteString();
    }
    
    public static ByteString recipientByteStringTrim(final ByteString recipientString) {
        int start = recipientString.indexOf(60);
        int end = recipientString.indexOf(62);
        start -= recipientString.offset();
        end -= recipientString.offset();
        if (start < 0 || end < 0) {
            return recipientString.trim();
        }
        if (start > end) {
            return recipientString.trim();
        }
        return new ByteString(recipientString.substring(start + 1, end).trim());
    }
    
    private static HashSet<String> getEmailEnvelopeRecipients(final Headers headers) {
        final HashSet<String> emailEnvelopeRecipients = new HashSet<String>();
        for (int numFields = headers.numFields(), i = 0; i < numFields; ++i) {
            final Field field = headers.fieldAt(i);
            if (isRecipientField(field)) {
                final FieldBody fieldBody = field.fieldBody();
                if (fieldBody instanceof Address) {
                    final ByteString rcpt = (fieldBody.getText().length() > 0) ? fieldBody.getText() : fieldBody.getString();
                    emailEnvelopeRecipients.add(NetworkAddressUtil.getShortEmail(fixRcptUtf7(rcpt).toString()));
                }
                else if (fieldBody instanceof AddressList) {
                    final AddressList list = (AddressList)fieldBody;
                    for (int j = 0; j < list.numAddresses(); ++j) {
                        final Address address = list.addressAt(j);
                        final ByteString rcpt2 = (address.getText().length() > 0) ? address.getText() : address.getString();
                        emailEnvelopeRecipients.add(NetworkAddressUtil.getShortEmail(fixRcptUtf7(rcpt2).toString()));
                    }
                }
                else {
                    final ByteString rcpt = field.fieldBody().getString();
                    emailEnvelopeRecipients.add(NetworkAddressUtil.getShortEmail(fixRcptUtf7(rcpt).toString()));
                }
            }
        }
        return emailEnvelopeRecipients;
    }
    
    private static boolean isRecipientField(final Field field) {
        final ByteString name = field.getFieldName();
        return name.equalsIgnoreCase(Headers.TO) || name.equalsIgnoreCase(Headers.CC) || name.equalsIgnoreCase(Headers.BCC) || name.equalsIgnoreCase(Headers.RESENT_TO) || name.equalsIgnoreCase(MessageUtilities.APPARENTLY_TO) || name.equalsIgnoreCase(MessageUtilities.X_APPARENTLY_TO);
    }
    
    private static ArrayList<String> getSmtpEnvelopeRecipients(final ByteString messageString) {
        final ArrayList<String> smtpEnvelopeRecipients = new ArrayList<String>();
        final int indexOfData = byteStringIndexOf(messageString, 0, MessageUtilities.START_MESSAGE_MARKER, true);
        if (indexOfData > -1) {
            final ByteString smtpEnvelopeByteString = messageString.substring(0, indexOfData);
            int index = 0;
            for (int start = 0, endOfLineIndex = 0; (index = byteStringIndexOf(smtpEnvelopeByteString, start, MessageUtilities.RCPT_TO, true)) >= 0; start = endOfLineIndex) {
                index += MessageUtilities.RCPT_TO.length();
                endOfLineIndex = byteStringIndexOf(smtpEnvelopeByteString, index, MessageUtilities.END_LINE_MARKER, false);
                if (endOfLineIndex == -1) {
                    endOfLineIndex = smtpEnvelopeByteString.length();
                }
                final ByteString recipient = ByteStringUtilities.duplicate(recipientByteStringTrim(smtpEnvelopeByteString.substring(index, endOfLineIndex)));
                smtpEnvelopeRecipients.add(NetworkAddressUtil.getShortEmail(fixRcptUtf7(recipient).toString()));
            }
        }
        return smtpEnvelopeRecipients;
    }
    
    private static boolean checkForBcc(final HashSet<String> emailEnvelopeRecipients, final ArrayList<String> smtpEnvelopeRecipients) {
        for (final String smtpEnvelopeRecipient : smtpEnvelopeRecipients) {
            if (!emailEnvelopeRecipients.contains(smtpEnvelopeRecipient)) {
                return true;
            }
        }
        return false;
    }
    
    public static ArrayList<String> getEmailBCCRecipients(final ByteString messageString) {
        final Message mimeMessage = new Message(messageString);
        mimeMessage.parse();
        final Headers messageHeaders = mimeMessage.headers();
        final HashSet<String> emailEnvelopeRecipients = getEmailEnvelopeRecipients(messageHeaders);
        final ArrayList<String> smtpEnvelopeRecipients = getSmtpEnvelopeRecipients(messageString);
        ArrayList<String> emailBCCRecipients = new ArrayList<String>();
        if (checkForBcc(emailEnvelopeRecipients, smtpEnvelopeRecipients)) {
            emailBCCRecipients = smtpEnvelopeRecipients;
        }
        return emailBCCRecipients;
    }
    
    static {
        START_MESSAGE_MARKER = new ByteString("DATA\r\n");
        END_LINE_MARKER = new ByteString("\r\n");
        RCPT_TO = new ByteString("RCPT TO:");
        RCPT_UTF7_AT = new ByteString("+AEA-");
        APPARENTLY_TO = new ByteString("Apparently-To");
        X_APPARENTLY_TO = new ByteString("X-Apparently-To");
    }
}
