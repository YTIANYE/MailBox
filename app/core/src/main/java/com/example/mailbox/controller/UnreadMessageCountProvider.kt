package com.example.mailbox.controller

import android.content.Context
import com.example.mailbox.Account
import com.example.mailbox.Preferences
import com.example.mailbox.mail.MessagingException
import com.example.mailbox.mailstore.LocalStoreProvider
import com.example.mailbox.search.AccountSearchConditions
import com.example.mailbox.search.LocalSearch
import com.example.mailbox.search.SearchAccount
import timber.log.Timber

interface UnreadMessageCountProvider {
    fun getUnreadMessageCount(account: Account): Int
    fun getUnreadMessageCount(searchAccount: SearchAccount): Int
}

internal class DefaultUnreadMessageCountProvider(
    private val context: Context,
    private val preferences: Preferences,
    private val accountSearchConditions: AccountSearchConditions,
    private val localStoreProvider: LocalStoreProvider
) : UnreadMessageCountProvider {
    override fun getUnreadMessageCount(account: Account): Int {
        if (!account.isAvailable(context)) {
            return 0
        }

        return try {
            val localStore = localStoreProvider.getInstance(account)

            val search = LocalSearch()
            accountSearchConditions.excludeSpecialFolders(account, search)
            accountSearchConditions.limitToDisplayableFolders(account, search)

            localStore.getUnreadMessageCount(search)
        } catch (e: MessagingException) {
            Timber.e(e, "Unable to getUnreadMessageCount for account: %s", account)
            0
        }
    }

    override fun getUnreadMessageCount(searchAccount: SearchAccount): Int {
        val search = searchAccount.relatedSearch
        val accounts = getAccountsFromLocalSearch(search)

        var unreadMessageCount = 0
        for (account in accounts) {
            unreadMessageCount += getUnreadMessageCountWithLocalSearch(account, search)
        }

        return unreadMessageCount
    }

    private fun getUnreadMessageCountWithLocalSearch(account: Account, search: LocalSearch): Int {
        return try {
            val localStore = localStoreProvider.getInstance(account)
            localStore.getUnreadMessageCount(search)
        } catch (e: MessagingException) {
            Timber.e(e, "Unable to getUnreadMessageCount for account: %s", account)
            0
        }
    }

    private fun getAccountsFromLocalSearch(search: LocalSearch): List<Account> {
        return if (search.searchAllAccounts()) {
            preferences.accounts
        } else {
            preferences.accounts.filter { it.uuid in search.accountUuids }
        }
    }
}
