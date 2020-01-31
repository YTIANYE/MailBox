package com.example.mailbox.backend.imap

import com.example.mailbox.mail.Flag
import com.example.mailbox.mail.store.imap.ImapStore

internal class CommandSearch(private val imapStore: ImapStore) {

    fun search(
        folderServerId: String,
        query: String?,
        requiredFlags: Set<Flag>?,
        forbiddenFlags: Set<Flag>?
    ): List<String> {
        val folder = imapStore.getFolder(folderServerId)
        try {
            return folder.search(query, requiredFlags, forbiddenFlags)
                    .sortedWith(UidReverseComparator())
                    .map { it.uid }
        } finally {
            folder.close()
        }
    }
}
