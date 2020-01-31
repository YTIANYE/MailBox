package com.example.mailbox.backend.imap

import com.example.mailbox.mail.Folder
import com.example.mailbox.mail.Message
import com.example.mailbox.mail.store.imap.ImapStore

internal class CommandUploadMessage(private val imapStore: ImapStore) {

    fun uploadMessage(folderServerId: String, message: Message): String? {
        val folder = imapStore.getFolder(folderServerId)
        try {
            folder.open(Folder.OPEN_MODE_RW)

            val localUid = message.uid
            val uidMap = folder.appendMessages(listOf(message))

            return uidMap[localUid]
        } finally {
            folder.close()
        }
    }
}
