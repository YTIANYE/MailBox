package com.example.mailbox.backend.webdav

import com.example.mailbox.mail.Folder
import com.example.mailbox.mail.Message
import com.example.mailbox.mail.store.webdav.WebDavStore

internal class CommandUploadMessage(private val webDavStore: WebDavStore) {

    fun uploadMessage(folderServerId: String, message: Message): String? {
        val folder = webDavStore.getFolder(folderServerId)
        try {
            folder.open(Folder.OPEN_MODE_RW)

            folder.appendMessages(listOf(message))

            return null
        } finally {
            folder.close()
        }
    }
}
