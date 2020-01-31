package com.example.mailbox.backend.webdav;


import java.util.Collections;

import com.example.mailbox.mail.Flag;
import com.example.mailbox.mail.Folder;
import com.example.mailbox.mail.MessagingException;
import com.example.mailbox.mail.store.webdav.WebDavFolder;
import com.example.mailbox.mail.store.webdav.WebDavStore;
import org.jetbrains.annotations.NotNull;


class CommandMarkAllAsRead {
    private final WebDavStore webDavStore;


    CommandMarkAllAsRead(WebDavStore webDavStore) {
        this.webDavStore = webDavStore;
    }

    void markAllAsRead(@NotNull String folderServerId) throws MessagingException {
        WebDavFolder remoteFolder = webDavStore.getFolder(folderServerId);
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
