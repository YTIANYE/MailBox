package com.example.mailbox.mailstore

import com.example.mailbox.Account

class FolderRepositoryManager(
    private val localStoreProvider: LocalStoreProvider,
    private val specialFolderSelectionStrategy: SpecialFolderSelectionStrategy
) {
    fun getFolderRepository(account: Account) = FolderRepository(localStoreProvider, specialFolderSelectionStrategy, account)
}
