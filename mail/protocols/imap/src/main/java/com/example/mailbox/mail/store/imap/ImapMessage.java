package com.example.mailbox.mail.store.imap;


import java.util.Collections;

import com.example.mailbox.mail.Flag;
import com.example.mailbox.mail.Folder;
import com.example.mailbox.mail.MessagingException;
import com.example.mailbox.mail.internet.MimeMessage;


public class ImapMessage extends MimeMessage {
    ImapMessage(String uid, Folder folder) {
        this.mUid = uid;
        this.mFolder = folder;
    }

    public void setSize(int size) {
        this.mSize = size;
    }

    public void setFlagInternal(Flag flag, boolean set) throws MessagingException {
        super.setFlag(flag, set);
    }

    @Override
    public void setFlag(Flag flag, boolean set) throws MessagingException {
        super.setFlag(flag, set);
        mFolder.setFlags(Collections.singletonList(this), Collections.singleton(flag), set);
    }
}
