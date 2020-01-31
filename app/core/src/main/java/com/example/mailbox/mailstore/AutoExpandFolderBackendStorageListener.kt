package com.example.mailbox.mailstore

import com.example.mailbox.Account
import com.example.mailbox.Preferences
import com.example.mailbox.backend.api.FolderInfo
import com.example.mailbox.mail.Folder

/**
 * Reset an Account's auto-expand folder when the currently configured folder was removed.
 */
class AutoExpandFolderBackendStorageListener(
    private val preferences: Preferences,
    private val account: Account
) : BackendStorageListener {

    override fun onFoldersCreated(folders: List<FolderInfo>) = Unit

    override fun onFoldersDeleted(folderServerIds: List<String>) {
        if (account.autoExpandFolder in folderServerIds) {
            account.autoExpandFolder = null
            preferences.saveAccount(account)
        }
    }

    override fun onFolderChanged(folderServerId: String, name: String, type: Folder.FolderType) = Unit
}
