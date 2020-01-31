package com.example.mailbox.backend.webdav

import com.example.mailbox.mail.Flag
import com.example.mailbox.mail.Folder
import com.example.mailbox.mail.MessagingException
import com.example.mailbox.mail.store.webdav.WebDavStore

internal class CommandDeleteAll(private val webDavStore: WebDavStore) {

    @Throws(MessagingException::class)
    fun deleteAll(folderServerId: String) {
        val remoteFolder = webDavStore.getFolder(folderServerId)
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
