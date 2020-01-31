package com.example.mailbox.message;


import java.util.List;

import com.example.mailbox.crypto.MessageCryptoStructureDetector;
import com.example.mailbox.mail.Message;
import com.example.mailbox.mail.Part;


public class ComposePgpEnableByDefaultDecider {
    public boolean shouldEncryptByDefault(Message localMessage) {
        return messageIsEncrypted(localMessage);
    }

    private boolean messageIsEncrypted(Message localMessage) {
        List<Part> encryptedParts = MessageCryptoStructureDetector.findMultipartEncryptedParts(localMessage);
        return !encryptedParts.isEmpty();
    }
}
