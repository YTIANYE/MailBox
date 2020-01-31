package com.example.mailbox.backend.imap

import com.example.mailbox.mail.Flag
import com.example.mailbox.mail.Folder
import com.example.mailbox.mail.MessagingException
import com.example.mailbox.mail.store.imap.ImapStore

internal class CommandDeleteAll(private val imapStore: ImapStore) {

    @Throws(MessagingException::class)
    fun deleteAll(folderServerId: String) {
        val remoteFolder = imapStore.getFolder(folderServerId)
        if (!remoteFolder.exists()) {
            return
        }

        try {
            remoteFolder.open(Folder.OPEN_MODE_RW)
            remoteFolder.setFlags(setOf(Flag.DELETED), true)
        } finally {
            remoteFolder.close()
        }
    }
}
