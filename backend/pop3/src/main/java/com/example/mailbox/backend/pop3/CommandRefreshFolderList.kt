package com.example.mailbox.backend.pop3

import com.example.mailbox.backend.api.BackendStorage
import com.example.mailbox.backend.api.FolderInfo
import com.example.mailbox.mail.Folder.FolderType
import com.example.mailbox.mail.store.pop3.Pop3Folder

internal class CommandRefreshFolderList(private val backendStorage: BackendStorage) {
    fun refreshFolderList() {
        val folderServerIds = backendStorage.getFolderServerIds()
        if (Pop3Folder.INBOX !in folderServerIds) {
            val inbox = FolderInfo(Pop3Folder.INBOX, Pop3Folder.INBOX, FolderType.INBOX)
            backendStorage.createFolders(listOf(inbox))
        }
    }
}
