package com.example.mailbox.message.extractors;


import java.util.ArrayList;
import java.util.List;

import com.example.mailbox.mail.Message;
import com.example.mailbox.mail.MessagingException;
import com.example.mailbox.mail.Part;
import com.example.mailbox.mail.internet.MessageExtractor;


public class AttachmentCounter {

    public static AttachmentCounter newInstance() {
        return new AttachmentCounter();
    }

    public int getAttachmentCount(Message message) throws MessagingException {
        List<Part> attachmentParts = new ArrayList<>();
        MessageExtractor.findViewablesAndAttachments(message, null, attachmentParts);

        return attachmentParts.size();
    }
}
