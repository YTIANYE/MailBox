package com.example.mailbox.crypto.openpgp;


import com.example.mailbox.mail.Body;
import com.example.mailbox.mail.BodyPart;
import com.example.mailbox.mail.Message;
import com.example.mailbox.mail.MessagingException;
import com.example.mailbox.mail.internet.MimeBodyPart;
import com.example.mailbox.mail.internet.MimeHeader;
import com.example.mailbox.mail.internet.MimeMessage;
import com.example.mailbox.mail.internet.MimeMultipart;
import com.example.mailbox.mail.internet.TextBody;
import com.example.mailbox.mailstore.BinaryMemoryBody;


public class MessageCreationHelper {
    public static BodyPart createPart(String mimeType) throws MessagingException {
        BinaryMemoryBody body = new BinaryMemoryBody(new byte[0], "utf-8");
        return new MimeBodyPart(body, mimeType);
    }

    public static Message createTextMessage(String mimeType, String text) {
        TextBody body = new TextBody(text);
        return createMessage(mimeType, body);
    }

    public static Message createMultipartMessage(String mimeType, BodyPart... parts) {
        MimeMultipart body = createMultipartBody(mimeType, parts);
        return createMessage(mimeType, body);
    }

    public static Message createMessage(String mimeType) {
        return createMessage(mimeType, null);
    }

    private static Message createMessage(String mimeType, Body body) {
        MimeMessage message = new MimeMessage();
        message.setBody(body);
        message.setHeader(MimeHeader.HEADER_CONTENT_TYPE, mimeType);

        return message;
    }

    private static MimeMultipart createMultipartBody(String mimeType, BodyPart[] parts) {
        MimeMultipart multipart = new MimeMultipart(mimeType, "boundary");
        for (BodyPart part : parts) {
            multipart.addBodyPart(part);
        }
        return multipart;
    }
}
