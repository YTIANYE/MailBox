package com.example.mailbox.backend.imap;


import java.util.List;

import com.example.mailbox.mail.Folder;
import com.example.mailbox.mail.MessagingException;
import com.example.mailbox.mail.store.imap.ImapStore;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;


class CommandExpunge {
    private final ImapStore imapStore;


    CommandExpunge(ImapStore imapStore) {
        this.imapStore = imapStore;
    }

    void expunge(@NotNull String folderServerId) throws MessagingException {
        Timber.d("processPendingExpunge: folder = %s", folderServerId);

        Folder remoteFolder = imapStore.getFolder(folderServerId);
        try {
            if (!remoteFolder.exists()) {
                return;
            }
            remoteFolder.open(Folder.OPEN_MODE_RW);
            if (remoteFolder.getMode() != Folder.OPEN_MODE_RW) {
                return;
            }
            remoteFolder.expunge();

            Timber.d("processPendingExpunge: complete for folder = %s", folderServerId);
        } finally {
            remoteFolder.close();
        }
    }

    void expungeMessages(@NotNull String folderServerId, @NotNull List<String> messageServerIds)
            throws MessagingException {
        Folder remoteFolder = imapStore.getFolder(folderServerId);
        try {
            if (!remoteFolder.exists()) {
                return;
            }
            remoteFolder.open(Folder.OPEN_MODE_RW);
            if (remoteFolder.getMode() != Folder.OPEN_MODE_RW) {
                return;
            }
            remoteFolder.expungeUids(messageServerIds);
        } finally {
            remoteFolder.close();
        }
    }
}
