package com.example.mailbox.message;


import java.util.List;

import com.example.mailbox.crypto.MessageCryptoStructureDetector;
import com.example.mailbox.mail.Message;
import com.example.mailbox.mail.Part;


public class ComposePgpInlineDecider {
    public boolean shouldReplyInline(Message localMessage) {
        // TODO more criteria for this? maybe check the User-Agent header?
        return messageHasPgpInlineParts(localMessage);
    }

    private boolean messageHasPgpInlineParts(Message localMessage) {
        List<Part> inlineParts = MessageCryptoStructureDetector.findPgpInlineParts(localMessage);
        return !inlineParts.isEmpty();
    }
}
