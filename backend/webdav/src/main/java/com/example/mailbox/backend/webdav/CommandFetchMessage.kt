package com.example.mailbox.backend.webdav

import com.example.mailbox.mail.FetchProfile
import com.example.mailbox.mail.Message
import com.example.mailbox.mail.store.webdav.WebDavStore

internal class CommandFetchMessage(private val webDavStore: WebDavStore) {

    fun fetchMessage(folderServerId: String, messageServerId: String, fetchProfile: FetchProfile): Message {
        val folder = webDavStore.getFolder(folderServerId)
        try {
            val message = folder.getMessage(messageServerId)

            folder.fetch(listOf(message), fetchProfile, null)

            return message
        } finally {
            folder.close()
        }
    }
}
