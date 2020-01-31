package com.example.mailbox.backend.imap;


import java.util.Collections;

import com.example.mailbox.mail.Flag;
import com.example.mailbox.mail.Folder;
import com.example.mailbox.mail.MessagingException;
import com.example.mailbox.mail.store.imap.ImapFolder;
import com.example.mailbox.mail.store.imap.ImapStore;
import org.jetbrains.annotations.NotNull;


class CommandMarkAllAsRead {
    private final ImapStore imapStore;


    CommandMarkAllAsRead(ImapStore imapStore) {
        this.imapStore = imapStore;
    }

    void markAllAsRead(@NotNull String folderServerId) throws MessagingException {
        ImapFolder remoteFolder = imapStore.getFolder(folderServerId);
        if (!remoteFolder.exists()) {
            return;
        }

        try {
            remoteFolder.open(Folder.OPEN_MODE_RW);
            if (remoteFolder.getMode() != Folder.OPEN_MODE_RW) {
                return;
            }

            remoteFolder.setFlags(Collections.singleton(Flag.SEEN), true);
        } finally {
            remoteFolder.close();
        }
    }
}
