package com.example.mailbox.mailstore

import com.example.mailbox.Account
import com.example.mailbox.Preferences

class K9BackendStorageFactory(
    private val preferences: Preferences,
    private val folderRepositoryManager: FolderRepositoryManager,
    private val localStoreProvider: LocalStoreProvider
) {
    fun createBackendStorage(account: Account): K9BackendStorage {
        val folderRepository = folderRepositoryManager.getFolderRepository(account)
        val localStore = localStoreProvider.getInstance(account)
        val specialFolderUpdater = SpecialFolderUpdater(preferences, folderRepository, account)
        val specialFolderListener = SpecialFolderBackendStorageListener(specialFolderUpdater)
        val autoExpandFolderListener = AutoExpandFolderBackendStorageListener(preferences, account)
        val listeners = listOf(specialFolderListener, autoExpandFolderListener)
        return K9BackendStorage(preferences, account, localStore, listeners)
    }
}
