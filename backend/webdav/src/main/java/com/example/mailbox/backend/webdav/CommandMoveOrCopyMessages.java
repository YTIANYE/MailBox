package com.example.mailbox.backend.webdav;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.example.mailbox.backend.api.BackendFolder;
import com.example.mailbox.mail.Folder;
import com.example.mailbox.mail.Message;
import com.example.mailbox.mail.MessagingException;
import com.example.mailbox.mail.store.webdav.WebDavFolder;
import com.example.mailbox.mail.store.webdav.WebDavStore;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;


class CommandMoveOrCopyMessages {
    private final WebDavStore webDavStore;


    CommandMoveOrCopyMessages(WebDavStore webDavStore) {
        this.webDavStore = webDavStore;
    }

    Map<String, String> moveMessages(@NotNull String sourceFolderServerId, @NotNull String targetFolderServerId,
            @NotNull List<String> messageServerIds) throws MessagingException {
        return moveOrCopyMessages(sourceFolderServerId, targetFolderServerId, messageServerIds, false);
    }

    Map<String, String> copyMessages(@NotNull String sourceFolderServerId, @NotNull String targetFolderServerId,
            @NotNull List<String> messageServerIds) throws MessagingException {
        return moveOrCopyMessages(sourceFolderServerId, targetFolderServerId, messageServerIds, true);
    }

    private Map<String, String> moveOrCopyMessages(String srcFolder, String destFolder, Collection<String> uids,
            boolean isCopy) throws MessagingException {
        WebDavFolder remoteSrcFolder = null;
        WebDavFolder remoteDestFolder = null;

        try {
            remoteSrcFolder = webDavStore.getFolder(srcFolder);

            List<Message> messages = new ArrayList<>();

            for (String uid : uids) {
                // TODO: Local messages should never end up here. Throw in debug builds.
                if (!uid.startsWith(BackendFolder.LOCAL_UID_PREFIX)) {
                    messages.add(remoteSrcFolder.getMessage(uid));
                }
            }

            if (messages.isEmpty()) {
                Timber.i("processingPendingMoveOrCopy: no remote messages to move, skipping");
                return null;
            }

            if (!remoteSrcFolder.exists()) {
                throw new MessagingException("processingPendingMoveOrCopy: remoteFolder " + srcFolder +
                        " does not exist", true);
            }

            remoteSrcFolder.open(Folder.OPEN_MODE_RW);
            if (remoteSrcFolder.getMode() != Folder.OPEN_MODE_RW) {
                throw new MessagingException("processingPendingMoveOrCopy: could not open remoteSrcFolder " +
                        srcFolder + " read/write", true);
            }

            Timber.d("processingPendingMoveOrCopy: source folder = %s, %d messages, " +
                    "destination folder = %s, isCopy = %s", srcFolder, messages.size(), destFolder, isCopy);


            remoteDestFolder = webDavStore.getFolder(destFolder);

            if (isCopy) {
                return remoteSrcFolder.copyMessages(messages, remoteDestFolder);
            } else {
                return remoteSrcFolder.moveMessages(messages, remoteDestFolder);
            }
        } finally {
            closeFolder(remoteSrcFolder);
            closeFolder(remoteDestFolder);
        }
    }

    private static void closeFolder(WebDavFolder folder) {
        if (folder != null) {
            folder.close();
        }
    }
}
