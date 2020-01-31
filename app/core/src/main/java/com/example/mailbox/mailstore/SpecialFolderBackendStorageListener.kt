package com.example.mailbox.mailstore

import com.example.mailbox.backend.api.FolderInfo
import com.example.mailbox.mail.Folder

/**
 * Update special folders when folders are added, removed, or changed.
 */
class SpecialFolderBackendStorageListener(
    private val specialFolderUpdater: SpecialFolderUpdater
) : BackendStorageListener {

    override fun onFoldersCreated(folders: List<FolderInfo>) {
        if (folders.any { it.type != Folder.FolderType.REGULAR }) {
            specialFolderUpdater.updateSpecialFolders()
        }
    }

    override fun onFoldersDeleted(folderServerIds: List<String>) {
        specialFolderUpdater.updateSpecialFolders()
    }

    override fun onFolderChanged(folderServerId: String, name: String, type: Folder.FolderType) {
        specialFolderUpdater.updateSpecialFolders()
    }
}
