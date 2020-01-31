package com.example.mailbox.backend.pop3

import com.example.mailbox.backend.api.Backend
import com.example.mailbox.backend.api.BackendStorage
import com.example.mailbox.backend.api.SyncConfig
import com.example.mailbox.backend.api.SyncListener
import com.example.mailbox.mail.BodyFactory
import com.example.mailbox.mail.FetchProfile
import com.example.mailbox.mail.Flag
import com.example.mailbox.mail.Message
import com.example.mailbox.mail.Part
import com.example.mailbox.mail.PushReceiver
import com.example.mailbox.mail.Pusher
import com.example.mailbox.mail.store.pop3.Pop3Store
import com.example.mailbox.mail.transport.smtp.SmtpTransport

class Pop3Backend(
    accountName: String,
    backendStorage: BackendStorage,
    private val pop3Store: Pop3Store,
    private val smtpTransport: SmtpTransport
) : Backend {
    private val pop3Sync: Pop3Sync = Pop3Sync(accountName, backendStorage, pop3Store)
    private val commandRefreshFolderList = CommandRefreshFolderList(backendStorage)
    private val commandSetFlag = CommandSetFlag(pop3Store)
    private val commandDeleteAll = CommandDeleteAll(pop3Store)
    private val commandFetchMessage = CommandFetchMessage(pop3Store)

    override val supportsSeenFlag = false
    override val supportsExpunge = false
    override val supportsMove = false
    override val supportsCopy = false
    override val supportsUpload = false
    override val supportsTrashFolder = false
    override val supportsSearchByDate = false
    override val isPushCapable = false
    override val isDeleteMoveToTrash = false

    override fun refreshFolderList() {
        commandRefreshFolderList.refreshFolderList()
    }

    override fun sync(folder: String, syncConfig: SyncConfig, listener: SyncListener) {
        pop3Sync.sync(folder, syncConfig, listener)
    }

    override fun downloadMessage(syncConfig: SyncConfig, folderServerId: String, messageServerId: String) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun setFlag(folderServerId: String, messageServerIds: List<String>, flag: Flag, newState: Boolean) {
        commandSetFlag.setFlag(folderServerId, messageServerIds, flag, newState)
    }

    override fun markAllAsRead(folderServerId: String) {
        throw UnsupportedOperationException("not supported")
    }

    override fun expunge(folderServerId: String) {
        throw UnsupportedOperationException("not supported")
    }

    override fun expungeMessages(folderServerId: String, messageServerIds: List<String>) {
        throw UnsupportedOperationException("not supported")
    }

    override fun deleteMessages(folderServerId: String, messageServerIds: List<String>) {
        commandSetFlag.setFlag(folderServerId, messageServerIds, Flag.DELETED, true)
    }

    override fun deleteAllMessages(folderServerId: String) {
        commandDeleteAll.deleteAll(folderServerId)
    }

    override fun moveMessages(
        sourceFolderServerId: String,
        targetFolderServerId: String,
        messageServerIds: List<String>
    ): Map<String, String>? {
        throw UnsupportedOperationException("not supported")
    }

    override fun copyMessages(
        sourceFolderServerId: String,
        targetFolderServerId: String,
        messageServerIds: List<String>
    ): Map<String, String>? {
        throw UnsupportedOperationException("not supported")
    }

    override fun search(
        folderServerId: String,
        query: String?,
        requiredFlags: Set<Flag>?,
        forbiddenFlags: Set<Flag>?
    ): List<String> {
        throw UnsupportedOperationException("not supported")
    }

    override fun fetchMessage(folderServerId: String, messageServerId: String, fetchProfile: FetchProfile): Message {
        return commandFetchMessage.fetchMessage(folderServerId, messageServerId, fetchProfile)
    }

    override fun fetchPart(folderServerId: String, messageServerId: String, part: Part, bodyFactory: BodyFactory) {
        throw UnsupportedOperationException("not supported")
    }

    override fun findByMessageId(folderServerId: String, messageId: String): String? {
        return null
    }

    override fun uploadMessage(folderServerId: String, message: Message): String? {
        throw UnsupportedOperationException("not supported")
    }

    override fun createPusher(receiver: PushReceiver): Pusher {
        throw UnsupportedOperationException("not supported")
    }

    override fun checkIncomingServerSettings() {
        pop3Store.checkSettings()
    }

    override fun sendMessage(message: Message) {
        smtpTransport.sendMessage(message)
    }

    override fun checkOutgoingServerSettings() {
        smtpTransport.checkSettings()
    }
}
