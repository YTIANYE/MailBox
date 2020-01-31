package com.example.mailbox.backend.imap

import com.example.mailbox.mail.Folder
import com.example.mailbox.mail.store.imap.ImapStore

internal class CommandFindByMessageId(private val imapStore: ImapStore) {

    fun findByMessageId(folderServerId: String, messageId: String): String? {
        val folder = imapStore.getFolder(folderServerId)
        try {
            folder.open(Folder.OPEN_MODE_RW)
            return folder.getUidFromMessageId(messageId)
        } finally {
            folder.close()
        }
    }
}
