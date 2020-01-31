package com.example.mailbox.backend.imap

import com.example.mailbox.backend.api.BackendStorage
import com.example.mailbox.backend.api.FolderInfo
import com.example.mailbox.mail.store.imap.ImapStore

internal class CommandRefreshFolderList(
    private val backendStorage: BackendStorage,
    private val imapStore: ImapStore
) {
    fun refreshFolderList() {
        val foldersOnServer = imapStore.personalNamespaces
        val oldFolderServerIds = backendStorage.getFolderServerIds()

        val foldersToCreate = mutableListOf<FolderInfo>()
        for (folder in foldersOnServer) {
            if (folder.serverId !in oldFolderServerIds) {
                foldersToCreate.add(FolderInfo(folder.serverId, folder.name, folder.type))
            } else {
                backendStorage.changeFolder(folder.serverId, folder.name, folder.type)
            }
        }
        backendStorage.createFolders(foldersToCreate)

        val newFolderServerIds = foldersOnServer.map { it.serverId }
        val removedFolderServerIds = oldFolderServerIds - newFolderServerIds
        backendStorage.deleteFolders(removedFolderServerIds)
    }
}
