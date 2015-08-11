// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.mail;

import com.hunnysoft.jmime.DispositionType;
import java.util.Vector;
import com.hunnysoft.jmime.MediaType;
import com.hunnysoft.jmime.Parameter;
import com.hunnysoft.jmime.Base64Decoder;
import com.hunnysoft.jmime.QuotedPrintableDecoder;
import com.hunnysoft.jmime.Entity;
import com.hunnysoft.jmime.EncodedWord;
import com.hunnysoft.jmime.Field;
import com.hunnysoft.jmime.Group;
import com.hunnysoft.jmime.AddressList;
import com.hunnysoft.jmime.ByteStringBuffer;
import com.hunnysoft.jmime.Body;
import com.hunnysoft.jmime.BodyPart;
import com.hunnysoft.jmime.QuotedPrintableEncoder;
import com.hunnysoft.jmime.Base64Encoder;
import java.io.UnsupportedEncodingException;
import com.hunnysoft.jmime.Mailbox;
import com.hunnysoft.jmime.DecodeException;
import com.hunnysoft.jmime.DateTime;
import com.hunnysoft.jmime.Headers;
import com.hunnysoft.jmime.ByteString;

public class Message
{
    private Date mDate;
    private Address mOriginator;
    private Address[] mToRecipients;
    private Address[] mCcRecipients;
    private Address[] mBccRecipients;
    private Text mSubject;
    private Text mMemoText;
    private Attachment[] mAttachments;
    private static final ByteString TEXT;
    private static final ByteString MESSAGE;
    private static final ByteString MULTIPART;
    private static final ByteString PLAIN;
    private static final ByteString PARTIAL;
    private static final ByteString EXTERNAL_BODY;
    private static final ByteString FILENAME;
    private static final ByteString NAME;
    private static final ByteString BASE64;
    private static final ByteString QUOTED_PRINTABLE;
    private static final ByteString NUMBER;
    private static final ByteString TOTAL;
    
    public Message() {
        this.mDate = new Date();
        this.mOriginator = new Address();
        this.mToRecipients = new Address[0];
        this.mCcRecipients = new Address[0];
        this.mBccRecipients = new Address[0];
        this.mSubject = new Text(ByteString.EMPTY_BYTE_STRING, "US-ASCII");
        this.mMemoText = new Text(ByteString.EMPTY_BYTE_STRING, "US-ASCII");
        this.mAttachments = new Attachment[0];
    }
    
    public Address getOriginator() {
        return this.mOriginator;
    }
    
    public void setOriginator(final Address addr) {
        this.mOriginator = addr;
    }
    
    public Address[] getToRecipients() {
        return this.mToRecipients;
    }
    
    public void addToRecipient(final Address addr) {
        final int n = this.mToRecipients.length;
        final Address[] addresses = new Address[n + 1];
        System.arraycopy(this.mToRecipients, 0, addresses, 0, n);
        addresses[n] = addr;
        this.mToRecipients = addresses;
    }
    
    public Address[] getCcRecipients() {
        return this.mCcRecipients;
    }
    
    public void addCcRecipient(final Address addr) {
        final int n = this.mCcRecipients.length;
        final Address[] addresses = new Address[n + 1];
        System.arraycopy(this.mCcRecipients, 0, addresses, 0, n);
        addresses[n] = addr;
        this.mCcRecipients = addresses;
    }
    
    public Address[] getBccRecipients() {
        return this.mBccRecipients;
    }
    
    public void addBccRecipient(final Address addr) {
        final int n = this.mBccRecipients.length;
        final Address[] addresses = new Address[n + 1];
        System.arraycopy(this.mBccRecipients, 0, addresses, 0, n);
        addresses[n] = addr;
        this.mBccRecipients = addresses;
    }
    
    public Text getSubject() {
        return this.mSubject;
    }
    
    public void setSubject(final Text subj) {
        this.mSubject = subj;
    }
    
    public Date getDate() {
        return this.mDate;
    }
    
    public void setDate(final Date date) {
        this.mDate = date;
    }
    
    public Text getMemoText() {
        return this.mMemoText;
    }
    
    public void setMemoText(final Text text) {
        this.mMemoText = text;
    }
    
    public Attachment[] getAttachments() {
        return this.mAttachments;
    }
    
    public void addAttachment(final Attachment attach) {
        final int n = this.mAttachments.length;
        final Attachment[] attachments = new Attachment[n + 1];
        System.arraycopy(this.mAttachments, 0, attachments, 0, n);
        attachments[n] = attach;
        this.mAttachments = attachments;
    }
    
    public void parse(final ByteString messageString) throws DecodeException {
        final com.hunnysoft.jmime.Message message = new com.hunnysoft.jmime.Message(messageString);
        message.parse();
        final Headers headers = message.headers();
        this.mOriginator = this.parseOriginator(message);
        this.mToRecipients = this.parseRecipients(message, "to");
        this.mCcRecipients = this.parseRecipients(message, "cc");
        if (headers.hasField(Headers.DATE)) {
            final DateTime date = headers.date();
            final int year = date.getYear();
            final int month = date.getMonth();
            final int day = date.getDay();
            final int hour = date.getHour();
            final int minute = date.getMinute();
            final int second = date.getSecond();
            final int zone = date.getZone();
            this.mDate = new Date(year, month, day, hour, minute, second, zone);
        }
        this.mSubject = this.parseSubject(message);
        this.mMemoText = this.parseMemoText(message);
        this.mAttachments = this.parseAttachments(message);
    }
    
    public ByteString serialize() {
        final com.hunnysoft.jmime.Message message = new com.hunnysoft.jmime.Message();
        final Headers headers = message.headers();
        final Body body = message.body();
        headers.fieldBody(Headers.MIME_VERSION).setText(new ByteString("1.0"));
        Mailbox mailbox = new Mailbox();
        mailbox.setString(new ByteString(this.mOriginator.getInetName()));
        mailbox.parse();
        String displayName = this.mOriginator.getDisplayName();
        String charset = this.mOriginator.getCharset();
        if (displayName.length() > 0) {
            if (charset.length() > 0 && !charset.equalsIgnoreCase("us-ascii")) {
                try {
                    final ByteString cs = new ByteString(charset);
                    mailbox.setDisplayName(displayName, cs);
                }
                catch (UnsupportedEncodingException e) {
                    mailbox.setDisplayName(displayName);
                }
            }
            else {
                mailbox.setDisplayName(displayName);
            }
        }
        headers.from().addAddress((com.hunnysoft.jmime.Address)mailbox);
        for (int i = 0; i < this.mToRecipients.length; ++i) {
            final Address addr = this.mToRecipients[i];
            mailbox = new Mailbox();
            mailbox.setString(new ByteString(addr.getInetName()));
            mailbox.parse();
            displayName = addr.getDisplayName();
            charset = addr.getCharset();
            if (displayName.length() > 0) {
                if (charset.length() > 0 && !charset.equalsIgnoreCase("us-ascii")) {
                    try {
                        final ByteString cs2 = new ByteString(charset);
                        mailbox.setDisplayName(displayName, cs2);
                    }
                    catch (UnsupportedEncodingException e2) {
                        mailbox.setDisplayName(displayName);
                    }
                }
                else {
                    mailbox.setDisplayName(displayName);
                }
            }
            headers.to().addAddress((com.hunnysoft.jmime.Address)mailbox);
        }
        for (int i = 0; i < this.mCcRecipients.length; ++i) {
            final Address addr = this.mCcRecipients[i];
            mailbox = new Mailbox();
            mailbox.setString(new ByteString(addr.getInetName()));
            mailbox.parse();
            displayName = addr.getDisplayName();
            charset = addr.getCharset();
            if (displayName.length() > 0) {
                if (charset.length() > 0 && !charset.equalsIgnoreCase("us-ascii")) {
                    try {
                        final ByteString cs2 = new ByteString(charset);
                        mailbox.setDisplayName(displayName, cs2);
                    }
                    catch (UnsupportedEncodingException e2) {
                        mailbox.setDisplayName(displayName);
                    }
                }
                else {
                    mailbox.setDisplayName(displayName);
                }
            }
            headers.cc().addAddress((com.hunnysoft.jmime.Address)mailbox);
        }
        for (int i = 0; i < this.mBccRecipients.length; ++i) {
            final Address addr = this.mBccRecipients[i];
            mailbox = new Mailbox();
            mailbox.setString(new ByteString(addr.getInetName()));
            mailbox.parse();
            displayName = addr.getDisplayName();
            charset = addr.getCharset();
            if (displayName.length() > 0) {
                if (charset.length() > 0 && !charset.equalsIgnoreCase("us-ascii")) {
                    try {
                        final ByteString cs2 = new ByteString(charset);
                        mailbox.setDisplayName(displayName, cs2);
                    }
                    catch (UnsupportedEncodingException e2) {
                        mailbox.setDisplayName(displayName);
                    }
                }
                else {
                    mailbox.setDisplayName(displayName);
                }
            }
            headers.bcc().addAddress((com.hunnysoft.jmime.Address)mailbox);
        }
        this.setSubject(message, this.mSubject.getText(), this.mSubject.getCharset(), '\0');
        headers.date().setValuesLiteral(this.mDate.getYear(), this.mDate.getMonth(), this.mDate.getDay(), this.mDate.getHour(), this.mDate.getMinute(), this.mDate.getSecond(), this.mDate.getZone());
        headers.messageId().createDefault();
        if (this.mAttachments.length == 0) {
            ByteString text = this.mMemoText.getText();
            final String encoding = this.mMemoText.getEncoding();
            final String language = this.mMemoText.getLanguage();
            headers.contentType().setType(new ByteString("text"));
            headers.contentType().setSubtype(new ByteString("plain"));
            headers.contentType().setCharset(new ByteString(encoding));
            if (language.length() > 0) {
                headers.fieldBody(Headers.CONTENT_LANGUAGE).setText(new ByteString(language));
            }
            final char xfer_encoding = this.recommendEncoding(this.mMemoText);
            if (xfer_encoding == 'B') {
                headers.contentTransferEncoding().fromEnum(5);
                text = new Base64Encoder().encode(text);
            }
            else if (xfer_encoding == 'Q') {
                headers.contentTransferEncoding().fromEnum(4);
                text = new QuotedPrintableEncoder().encode(text);
            }
            body.setString(text);
        }
        else {
            headers.contentType().setType(new ByteString("multipart"));
            headers.contentType().setSubtype(new ByteString("mixed"));
            headers.contentType().createBoundary(0);
            ByteString text = this.mMemoText.getText();
            final String encoding = this.mMemoText.getEncoding();
            final String language = this.mMemoText.getLanguage();
            BodyPart part = new BodyPart();
            Headers partHeaders = part.headers();
            partHeaders.contentType().setType(new ByteString("text"));
            partHeaders.contentType().setSubtype(new ByteString("plain"));
            partHeaders.contentType().setCharset(new ByteString(encoding));
            if (language.length() > 0) {
                partHeaders.fieldBody(Headers.CONTENT_LANGUAGE).setText(new ByteString(language));
            }
            final char xfer_encoding2 = this.recommendEncoding(this.mMemoText);
            if (xfer_encoding2 == 'B') {
                partHeaders.contentTransferEncoding().fromEnum(5);
                text = new Base64Encoder().encode(text);
            }
            else if (xfer_encoding2 == 'Q') {
                partHeaders.contentTransferEncoding().fromEnum(4);
                text = new QuotedPrintableEncoder().encode(text);
            }
            part.body().setString(text);
            body.addBodyPart(part);
            part = null;
            for (int i = 0; i < this.mAttachments.length; ++i) {
                final Attachment attach = this.mAttachments[i];
                part = new BodyPart();
                partHeaders = part.headers();
                partHeaders.contentTransferEncoding().fromEnum(5);
                partHeaders.contentType().setType(new ByteString(attach.getType()));
                partHeaders.contentType().setSubtype(new ByteString(attach.getSubtype()));
                partHeaders.contentType().setName(new ByteString(attach.getFileName()));
                partHeaders.contentDisposition().fromEnum(3);
                partHeaders.contentDisposition().setFilename(new ByteString(attach.getFileName()));
                final ByteString content = new Base64Encoder().encode(attach.getContent());
                part.body().setString(content);
                body.addBodyPart(part);
            }
        }
        message.assemble();
        final ByteString messageString = message.getString();
        return messageString;
    }
    
    private Address parseOriginator(final com.hunnysoft.jmime.Message aMessage) {
        String inetName = "";
        String displayName = "";
        final String charset = "";
        final Headers headers = aMessage.headers();
        if (headers.hasField(Headers.FROM)) {
            final AddressList fromList = headers.from();
            if (fromList.numAddresses() > 0) {
                final com.hunnysoft.jmime.Address from = fromList.addressAt(0);
                if (from instanceof Mailbox) {
                    final Mailbox mbox = (Mailbox)from;
                    ByteStringBuffer buf = new ByteStringBuffer(20);
                    buf.append(mbox.getLocalPart());
                    buf.append("@");
                    buf.append(mbox.getDomain());
                    inetName = buf.toByteString().toString();
                    buf = null;
                    displayName = mbox.getDisplayName();
                }
            }
        }
        final Address addr = new Address(inetName, displayName, charset);
        return addr;
    }
    
    private Address[] parseRecipients(final com.hunnysoft.jmime.Message aMessage, final String aWhich) {
        final ByteString which = new ByteString(aWhich);
        final Headers headers = aMessage.headers();
        Address[] addresses = new Address[0];
        for (int numFields = headers.numFields(), i = 0; i < numFields; ++i) {
            final Field field = headers.fieldAt(i);
            if (field.getFieldName().equalsIgnoreCase(which)) {
                final AddressList rcptList = (AddressList)field.fieldBody();
                for (int numAddresses = rcptList.numAddresses(), j = 0; j < numAddresses; ++j) {
                    final com.hunnysoft.jmime.Address addr = rcptList.addressAt(j);
                    if (addr instanceof Mailbox) {
                        final Mailbox rcpt = (Mailbox)addr;
                        ByteStringBuffer buf = new ByteStringBuffer(20);
                        buf.append(rcpt.getLocalPart());
                        buf.append("@");
                        buf.append(rcpt.getDomain());
                        final String inetName = buf.toByteString().toString();
                        buf = null;
                        final String displayName = rcpt.getDisplayName();
                        final Address eaddr = new Address(inetName, displayName, "");
                        final Address[] ar = new Address[addresses.length + 1];
                        System.arraycopy(addresses, 0, ar, 0, addresses.length);
                        ar[addresses.length] = eaddr;
                        addresses = ar;
                    }
                    else if (addr instanceof Group) {
                        final Group rcpt2 = (Group)addr;
                        final Address eaddr2 = new Address("", rcpt2.getGroupName().toString(), "");
                        final Address[] ar2 = new Address[addresses.length + 1];
                        System.arraycopy(addresses, 0, ar2, 0, addresses.length);
                        ar2[addresses.length] = eaddr2;
                        addresses = ar2;
                    }
                }
            }
        }
        return addresses;
    }
    
    private Text parseSubject(final com.hunnysoft.jmime.Message aMessage) {
        final ByteString US_ASCII = new ByteString("US-ASCII");
        Text subject = null;
        final Headers headers = aMessage.headers();
        if (headers.hasField(Headers.SUBJECT)) {
            final ByteStringBuffer buf = new ByteStringBuffer(80);
            String charset = "";
            final com.hunnysoft.jmime.Text subj = headers.subject();
            for (int numEncodedWords = subj.numEncodedWords(), i = 0; i < numEncodedWords; ++i) {
                final EncodedWord word = subj.encodedWordAt(i);
                if (buf.length() > 0) {
                    buf.append((byte)32);
                }
                buf.append(word.getDecodedText());
                if (charset.length() == 0 && !word.getCharset().equalsIgnoreCase(US_ASCII)) {
                    charset = word.getCharset().toString();
                }
            }
            subject = new Text(buf.toByteString(), charset);
        }
        if (subject == null) {
            subject = new Text(ByteString.EMPTY_BYTE_STRING, "");
        }
        return subject;
    }
    
    private Text parseMemoText(final com.hunnysoft.jmime.Message aMessage) throws DecodeException {
        Text text = null;
        ByteString type = Message.TEXT;
        ByteString subtype = Message.PLAIN;
        final Headers headers = aMessage.headers();
        if (headers.hasField(Headers.CONTENT_TYPE)) {
            type = headers.contentType().getType();
            subtype = headers.contentType().getSubtype();
        }
        if (type.equalsIgnoreCase(Message.TEXT)) {
            text = this.parseMemoText_Text((Entity)aMessage);
        }
        else if (type.equalsIgnoreCase(Message.MESSAGE)) {
            text = this.parseMemoText_Message(aMessage);
        }
        else if (type.equalsIgnoreCase(Message.MULTIPART)) {
            text = this.parseMemoText_Multipart(aMessage);
        }
        else {
            text = new Text(ByteString.EMPTY_BYTE_STRING, "");
        }
        return text;
    }
    
    private Text parseMemoText_Text(final Entity aEntity) throws DecodeException {
        final Headers headers = aEntity.headers();
        final Body body = aEntity.body();
        ByteString text = body.getString();
        ByteString cte = ByteString.EMPTY_BYTE_STRING;
        if (headers.hasField(Headers.CONTENT_TRANSFER_ENCODING)) {
            cte = headers.contentTransferEncoding().getString();
        }
        if (cte.equalsIgnoreCase(Message.QUOTED_PRINTABLE)) {
            text = new QuotedPrintableDecoder().decode(text);
        }
        else if (cte.equalsIgnoreCase(Message.BASE64)) {
            text = new Base64Decoder().decode(text);
        }
        String charset = "US-ASCII";
        if (headers.hasField(Headers.CONTENT_TYPE)) {
            for (int numParams = headers.contentType().numParameters(), i = 0; i < numParams; ++i) {
                final Parameter param = headers.contentType().parameterAt(i);
                final ByteString attrName = param.getName();
                if (attrName.equalsIgnoreCase(new ByteString("charset"))) {
                    charset = param.getValue().toString();
                    break;
                }
            }
        }
        String language = "";
        if (headers.hasField(Headers.CONTENT_LANGUAGE)) {
            String s = headers.fieldBody(Headers.CONTENT_LANGUAGE).getText().toString();
            final int n = s.indexOf(44);
            if (n >= 0) {
                s = s.substring(0, n);
            }
            language = s.toString();
        }
        if (language.length() > 0) {
            return new Text(text, charset, language);
        }
        return new Text(text, charset);
    }
    
    private Text parseMemoText_Message(final com.hunnysoft.jmime.Message aMessage) {
        final Headers headers = aMessage.headers();
        final Body body = aMessage.body();
        final ByteString subtype = headers.contentType().getSubtype();
        Text text;
        if (subtype.equalsIgnoreCase(Message.PARTIAL)) {
            text = this.parseMemoText_MessagePartial((Entity)aMessage);
        }
        else if (subtype.equalsIgnoreCase(Message.EXTERNAL_BODY)) {
            text = this.parseMemoText_MessageExternalBody((Entity)aMessage);
        }
        else {
            text = new Text(body.getString(), "US-ASCII");
        }
        return text;
    }
    
    private Text parseMemoText_MessagePartial(final Entity aEntity) {
        ByteString number = ByteString.EMPTY_BYTE_STRING;
        ByteString total = ByteString.EMPTY_BYTE_STRING;
        final MediaType contentType = aEntity.headers().contentType();
        for (int numParams = contentType.numParameters(), i = 0; i < numParams; ++i) {
            final Parameter param = contentType.parameterAt(i);
            final ByteString name = param.getName();
            final ByteString val = param.getValue();
            if (name.equalsIgnoreCase(Message.NUMBER)) {
                number = val;
            }
            if (name.equalsIgnoreCase(Message.TOTAL)) {
                total = val;
            }
        }
        if (total.length() == 0) {
            total = new ByteString("?");
        }
        final ByteStringBuffer buf = new ByteStringBuffer(30);
        buf.append("[This is part ");
        buf.append(number);
        buf.append(" of a total of ");
        buf.append(total);
        buf.append(" parts]");
        return new Text(buf.toByteString(), "US-ASCII");
    }
    
    private Text parseMemoText_MessageExternalBody(final Entity aEntity) {
        final String text = "[The body of this message is stored elsewhere]\r\n\r\n";
        final MediaType contentType = aEntity.headers().contentType();
        final ByteStringBuffer buf = new ByteStringBuffer(30);
        for (int numParams = contentType.numParameters(), i = 0; i < numParams; ++i) {
            final Parameter param = contentType.parameterAt(i);
            buf.append(param.getName());
            buf.append(": ");
            buf.append(param.getValue());
            buf.append("\r\n");
        }
        buf.append("\r\n");
        final com.hunnysoft.jmime.Message containedMessage = aEntity.body().message();
        if (containedMessage != null) {
            buf.append(containedMessage.getString());
        }
        return new Text(buf.toByteString(), "US-ASCII");
    }
    
    private Text parseMemoText_Multipart(final com.hunnysoft.jmime.Message aMessage) throws DecodeException {
        return this.parseMemoText_Multipart_Recursive((Entity)aMessage);
    }
    
    private Text parseMemoText_Multipart_Recursive(final Entity aEntity) throws DecodeException {
        Text text = null;
        final Headers headers = aEntity.headers();
        final Body body = aEntity.body();
        ByteString type = Message.TEXT;
        ByteString subtype = Message.PLAIN;
        if (headers.hasField(Headers.CONTENT_TYPE)) {
            type = headers.contentType().getType();
            subtype = headers.contentType().getSubtype();
        }
        if (type.equalsIgnoreCase(Message.MULTIPART)) {
            if (body.numBodyParts() > 0) {
                final BodyPart part = body.bodyPartAt(0);
                text = this.parseMemoText_Multipart_Recursive((Entity)part);
            }
        }
        else if (type.equalsIgnoreCase(Message.TEXT)) {
            text = this.parseMemoText_Text(aEntity);
        }
        if (text == null) {
            text = new Text(ByteString.EMPTY_BYTE_STRING, "");
        }
        return text;
    }
    
    private Attachment[] parseAttachments(final com.hunnysoft.jmime.Message aMessage) throws DecodeException {
        ByteString type = Message.TEXT;
        ByteString subtype = Message.PLAIN;
        final Headers headers = aMessage.headers();
        final Body body = aMessage.body();
        if (headers.hasField(Headers.CONTENT_TYPE)) {
            type = headers.contentType().getType();
            subtype = headers.contentType().getSubtype();
        }
        Attachment[] attachments;
        if (type.equalsIgnoreCase(Message.TEXT)) {
            attachments = new Attachment[0];
        }
        else if (type.equalsIgnoreCase(Message.MESSAGE)) {
            attachments = this.parseAttachments_Message(aMessage);
        }
        else if (type.equalsIgnoreCase(Message.MULTIPART)) {
            attachments = this.parseAttachments_Multipart(aMessage);
        }
        else {
            attachments = this.parseAttachments_Simple(aMessage);
        }
        return attachments;
    }
    
    private Attachment[] parseAttachments_Simple(final com.hunnysoft.jmime.Message aMessage) throws DecodeException {
        final Attachment[] attachments = { this.getAttachment((Entity)aMessage) };
        return attachments;
    }
    
    private Attachment[] parseAttachments_Message(final com.hunnysoft.jmime.Message aMessage) {
        Attachment[] attachments = null;
        final Headers headers = aMessage.headers();
        final Body body = aMessage.body();
        final com.hunnysoft.jmime.Message message = body.message();
        if (message != null) {
            final Attachment attachment = new Attachment();
            final String type = aMessage.headers().contentType().getType().toString();
            final String subtype = aMessage.headers().contentType().getSubtype().toString();
            attachment.setMediaType(type, subtype);
            attachment.setContent(message.getString());
            attachments = new Attachment[] { attachment };
        }
        else {
            final Attachment attachment = new Attachment();
            final String type = headers.contentType().getType().toString();
            final String subtype = headers.contentType().getSubtype().toString();
            attachment.setMediaType(type, subtype);
            attachment.setContent(body.getString());
            attachments = new Attachment[] { attachment };
        }
        return attachments;
    }
    
    private Attachment[] parseAttachments_Multipart(final com.hunnysoft.jmime.Message aMessage) throws DecodeException {
        final Vector attachments = new Vector();
        final Body body = aMessage.body();
        final int numParts = body.numBodyParts();
        if (numParts > 0) {
            final BodyPart part = body.bodyPartAt(0);
            if (!partContainsText((Entity)part)) {
                final Attachment attachment = this.getAttachment((Entity)part);
                attachments.add(attachment);
            }
        }
        for (int i = 1; i < numParts; ++i) {
            final BodyPart part2 = body.bodyPartAt(i);
            final Attachment attachment2 = this.getAttachment((Entity)part2);
            attachments.add(attachment2);
        }
        return (Attachment[]) attachments.toArray(new Attachment[attachments.size()]);
    }
    
    private Attachment getAttachment(final Entity aEntity) throws DecodeException {
        final Headers headers = aEntity.headers();
        final Body body = aEntity.body();
        final String type = headers.contentType().getType().toString();
        final String subtype = headers.contentType().getSubtype().toString();
        String filename = "";
        if (headers.hasField(Headers.CONTENT_DISPOSITION)) {
            final DispositionType dtype = headers.contentDisposition();
            for (int numParameters = dtype.numParameters(), i = 0; i < numParameters; ++i) {
                final Parameter param = dtype.parameterAt(i);
                if (param.getName().equalsIgnoreCase(Message.FILENAME)) {
                    filename = param.getValue().toString();
                    break;
                }
            }
        }
        if (filename.length() == 0) {
            final MediaType mtype = headers.contentType();
            for (int numParameters = mtype.numParameters(), i = 0; i < numParameters; ++i) {
                final Parameter param = mtype.parameterAt(i);
                if (param.getName().equalsIgnoreCase(Message.NAME)) {
                    filename = param.getValue().toString();
                    break;
                }
            }
        }
        final String description = "";
        ByteString content = body.getString();
        if (headers.hasField(Headers.CONTENT_TRANSFER_ENCODING)) {
            final ByteString cte = headers.contentTransferEncoding().getString();
            if (cte.equalsIgnoreCase(Message.BASE64)) {
                content = new Base64Decoder().decode(content);
            }
            else if (cte.equalsIgnoreCase(Message.QUOTED_PRINTABLE)) {
                content = new QuotedPrintableDecoder().decode(content);
            }
        }
        final Attachment attachment = new Attachment();
        attachment.setMediaType(type, subtype);
        attachment.setFileName(filename);
        attachment.setDescription(description);
        attachment.setContent(content);
        return attachment;
    }
    
    private static boolean partContainsText(final Entity aEntity) {
        boolean containsText = false;
        ByteString type = Message.TEXT;
        ByteString subtype = Message.PLAIN;
        final Headers headers = aEntity.headers();
        final Body body = aEntity.body();
        if (headers.hasField(Headers.CONTENT_TYPE)) {
            type = headers.contentType().getType();
            subtype = headers.contentType().getSubtype();
        }
        if (type.equalsIgnoreCase(Message.TEXT)) {
            containsText = true;
        }
        else if (type.equalsIgnoreCase(Message.MULTIPART)) {
            for (int numParts = body.numBodyParts(), i = 0; i < numParts; ++i) {
                final BodyPart part = body.bodyPartAt(i);
                if (partContainsText((Entity)part)) {
                    containsText = true;
                    break;
                }
            }
        }
        return containsText;
    }
    
    private void setSubject(final com.hunnysoft.jmime.Message aMessage, final ByteString aText, final String aCharset, final char aEncoding) {
        final int textLen = aText.length();
        final int charsetLen = aCharset.length();
        char encoding = '\0';
        int num8BitChars = 0;
        for (int i = 0; i < textLen; ++i) {
            final int ch = aText.byteAt(i) & 0xFF;
            if (ch < 32 || 126 < ch) {
                ++num8BitChars;
            }
        }
        if (aCharset.equalsIgnoreCase("US-ASCII") || (aCharset.regionMatches(true, 0, "ISO-8859", 0, 8) && num8BitChars == 0)) {
            encoding = 'N';
        }
        if (aEncoding == '\0') {
            if (aCharset.equalsIgnoreCase("ISO-8859-1") || aCharset.equalsIgnoreCase("ISO-8859-2") || aCharset.equalsIgnoreCase("ISO-8859-3") || aCharset.equalsIgnoreCase("ISO-8859-4") || aCharset.equalsIgnoreCase("ISO-8859-9")) {
                encoding = 'Q';
            }
            else {
                encoding = 'B';
            }
        }
        else if (aEncoding == 'q' || aEncoding == 'Q') {
            encoding = 'Q';
        }
        else if (aEncoding == 'b' || aEncoding == 'B') {
            encoding = 'B';
        }
        else {
            encoding = 'B';
        }
        if (encoding == 'N') {
            aMessage.headers().subject().setString(aText);
        }
        else if (encoding == 'Q') {
            aMessage.headers().subject().deleteAllEncodedWords();
            int startPos = 0;
            int endPos = 0;
            int count = 0;
            while (endPos < textLen) {
                final int ch2 = aText.byteAt(endPos) & 0xFF;
                if (ch2 < 32 || 126 < ch2 || ch2 == 61 || ch2 == 63) {
                    count += 2;
                }
                ++count;
                ++endPos;
                if (count + charsetLen + 7 > 64) {
                    final EncodedWord word = new EncodedWord();
                    word.setCharset(new ByteString(aCharset));
                    word.setEncodingType(encoding);
                    final ByteString s = new ByteString(aText, startPos, endPos - startPos);
                    word.setDecodedText(s);
                    aMessage.headers().subject().addEncodedWord(word);
                    count = 0;
                    startPos = endPos;
                }
            }
            if (count > 0) {
                final EncodedWord word2 = new EncodedWord();
                word2.setCharset(new ByteString(aCharset));
                word2.setEncodingType(encoding);
                final ByteString s2 = new ByteString(aText, startPos, endPos - startPos);
                word2.setDecodedText(s2);
                aMessage.headers().subject().addEncodedWord(word2);
            }
            aMessage.headers().subject().assemble();
        }
        else {
            aMessage.headers().subject().deleteAllEncodedWords();
            int startPos = 0;
            int endPos = 0;
            while (endPos < textLen) {
                endPos += 39;
                endPos = ((endPos < textLen) ? endPos : textLen);
                final EncodedWord word3 = new EncodedWord();
                word3.setCharset(new ByteString(aCharset));
                word3.setEncodingType(encoding);
                final ByteString s3 = new ByteString(aText, startPos, endPos - startPos);
                word3.setDecodedText(s3);
                aMessage.headers().subject().addEncodedWord(word3);
                startPos = endPos;
            }
            aMessage.headers().subject().assemble();
        }
    }
    
    private char recommendEncoding(final Text aText) {
        final ByteString bstr = aText.getText();
        final int bstrLen = bstr.length();
        boolean has8bitChars = false;
        for (int i = 0; i < bstrLen; ++i) {
            final int ch = bstr.byteAt(i) & 0xFF;
            if (ch < 32 || 126 < ch) {
                has8bitChars = true;
                break;
            }
        }
        char encoding;
        if (aText.getCharset().equalsIgnoreCase("US-ASCII")) {
            encoding = 'N';
        }
        else if (aText.getCharset().regionMatches(true, 0, "ISO-8859", 0, 8)) {
            if (!has8bitChars) {
                encoding = 'Q';
            }
            else if (aText.getCharset().equalsIgnoreCase("ISO-8859-1") || aText.getCharset().equalsIgnoreCase("ISO-8859-2") || aText.getCharset().equalsIgnoreCase("ISO-8859-3") || aText.getCharset().equalsIgnoreCase("ISO-8859-4") || aText.getCharset().equalsIgnoreCase("ISO-8859-9")) {
                encoding = 'Q';
            }
            else {
                encoding = 'B';
            }
        }
        else {
            encoding = 'B';
        }
        return encoding;
    }
    
    static {
        TEXT = new ByteString("text");
        MESSAGE = new ByteString("message");
        MULTIPART = new ByteString("multipart");
        PLAIN = new ByteString("plain");
        PARTIAL = new ByteString("partial");
        EXTERNAL_BODY = new ByteString("external-body");
        FILENAME = new ByteString("filename");
        NAME = new ByteString("name");
        BASE64 = new ByteString("base64");
        QUOTED_PRINTABLE = new ByteString("quoted-printable");
        NUMBER = new ByteString("number");
        TOTAL = new ByteString("total");
    }
}
