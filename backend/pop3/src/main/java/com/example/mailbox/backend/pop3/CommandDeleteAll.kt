package com.example.mailbox.backend.pop3

import com.example.mailbox.mail.Flag
import com.example.mailbox.mail.Folder
import com.example.mailbox.mail.MessagingException
import com.example.mailbox.mail.store.pop3.Pop3Store

internal class CommandDeleteAll(private val pop3Store: Pop3Store) {

    @Throws(MessagingException::class)
    fun deleteAll(folderServerId: String) {
        val remoteFolder = pop3Store.getFolder(folderServerId)
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
