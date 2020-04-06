package com.fsck.k9.ui.messagelist

import com.fsck.k9.Account

/**
 * Decides which folder to display when an account is selected.
 */
class DefaultFolderProvider {
    fun getDefaultFolder(account: Account): String {
        // Until the UI can handle the case where no remote folders have been fetched yet, we fall back to the Outbox
        // which should always exist.
        return account.autoExpandFolder ?: account.inboxFolder ?: account.outboxFolder
    }
}
