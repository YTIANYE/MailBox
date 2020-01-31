package com.example.mailbox.backend.pop3;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.mailbox.backend.api.BackendFolder;
import com.example.mailbox.mail.Flag;
import com.example.mailbox.mail.Folder;
import com.example.mailbox.mail.Message;
import com.example.mailbox.mail.MessagingException;
import com.example.mailbox.mail.store.pop3.Pop3Folder;
import com.example.mailbox.mail.store.pop3.Pop3Store;
import org.jetbrains.annotations.NotNull;


class CommandSetFlag {
    private final Pop3Store pop3Store;


    CommandSetFlag(Pop3Store pop3Store) {
        this.pop3Store = pop3Store;
    }

    void setFlag(@NotNull String folderServerId, @NotNull List<String> messageServerIds, @NotNull Flag flag,
            boolean newState) throws MessagingException {

        Pop3Folder remoteFolder = pop3Store.getFolder(folderServerId);
        if (!remoteFolder.exists() || !remoteFolder.isFlagSupported(flag)) {
            return;
        }

        try {
            remoteFolder.open(Folder.OPEN_MODE_RW);
            if (remoteFolder.getMode() != Folder.OPEN_MODE_RW) {
                return;
            }
            List<Message> messages = new ArrayList<>();
            for (String uid : messageServerIds) {
                if (!uid.startsWith(BackendFolder.LOCAL_UID_PREFIX)) {
                    messages.add(remoteFolder.getMessage(uid));
                }
            }

            if (messages.isEmpty()) {
                return;
            }
            remoteFolder.setFlags(messages, Collections.singleton(flag), newState);
        } finally {
            remoteFolder.close();
        }
    }
}
