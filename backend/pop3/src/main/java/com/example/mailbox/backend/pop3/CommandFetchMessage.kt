package com.example.mailbox.backend.pop3

import com.example.mailbox.mail.FetchProfile
import com.example.mailbox.mail.Folder.OPEN_MODE_RW
import com.example.mailbox.mail.Message
import com.example.mailbox.mail.store.pop3.Pop3Store

internal class CommandFetchMessage(private val pop3Store: Pop3Store) {

    fun fetchMessage(folderServerId: String, messageServerId: String, fetchProfile: FetchProfile): Message {
        val folder = pop3Store.getFolder(folderServerId)
        try {
            folder.open(OPEN_MODE_RW)

            val message = folder.getMessage(messageServerId)
            folder.fetch(listOf(message), fetchProfile, null)

            return message
        } finally {
            folder.close()
        }
    }
}
