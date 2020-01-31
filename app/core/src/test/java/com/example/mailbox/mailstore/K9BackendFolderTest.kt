package com.example.mailbox.mailstore

import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.core.content.contentValuesOf
import com.example.mailbox.Account
import com.example.mailbox.K9RobolectricTest
import com.example.mailbox.Preferences
import com.example.mailbox.backend.api.BackendFolder
import com.example.mailbox.backend.api.FolderInfo
import com.example.mailbox.mail.Address
import com.example.mailbox.mail.Flag
import com.example.mailbox.mail.Folder.FolderType
import com.example.mailbox.mail.internet.MimeMessage
import com.example.mailbox.mail.internet.MimeMessageHelper
import com.example.mailbox.mail.internet.TextBody
import com.example.mailbox.provider.EmailProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.core.inject

class K9BackendFolderTest : K9RobolectricTest() {
    val preferences: Preferences by inject()
    val localStoreProvider: LocalStoreProvider by inject()

    val account: Account = createAccount()
    val backendFolder = createBackendFolder()
    val database: LockableDatabase = localStoreProvider.getInstance(account).database

    @Before
    fun setUp() {
        // Set EmailProvider.CONTENT_URI so LocalStore.notifyChange() won't crash
        EmailProvider.CONTENT_URI = Uri.parse("content://dummy")
    }

    @After
    fun tearDown() {
        preferences.deleteAccount(account)
    }

    @Test
    fun getMessageFlags() {
        val flags = setOf(Flag.SEEN, Flag.DRAFT, Flag.X_DOWNLOADED_FULL)
        createMessage(MESSAGE_SERVER_ID, flags)

        val messageFlags = backendFolder.getMessageFlags(MESSAGE_SERVER_ID)

        assertEquals(flags, messageFlags)
    }

    @Test
    fun getMessageFlags_withFlagsColumnSetToNull_shouldBeTreatedAsEmpty() {
        createMessage(MESSAGE_SERVER_ID)
        setFlagsColumnToNull()

        val messageFlags = backendFolder.getMessageFlags(MESSAGE_SERVER_ID)

        assertTrue(messageFlags.isEmpty())
    }

    @Test
    fun getMessageFlags_withFlagsColumnSetToNull_shouldReadSpecialColumnFlags() {
        val flags = setOf(Flag.SEEN, Flag.FLAGGED, Flag.ANSWERED, Flag.FORWARDED)
        createMessage(MESSAGE_SERVER_ID, flags)
        setFlagsColumnToNull()

        val messageFlags = backendFolder.getMessageFlags(MESSAGE_SERVER_ID)

        assertEquals(flags, messageFlags)
    }

    @Test
    fun getLastUid() {
        createMessage("200")
        createMessage("123")

        val lastUid = backendFolder.getLastUid()

        assertEquals(200L, lastUid)
    }

    fun createAccount(): Account {
        // FIXME: This is a hack to get Preferences into a state where it's safe to call newAccount()
        preferences.clearAccounts()

        return preferences.newAccount()
    }

    fun createBackendFolder(): BackendFolder {
        val localStore: LocalStore = localStoreProvider.getInstance(account)
        val backendStorage = K9BackendStorage(preferences, account, localStore, emptyList())
        backendStorage.createFolders(listOf(FolderInfo(FOLDER_SERVER_ID, FOLDER_NAME, FOLDER_TYPE)))

        val folderServerIds = backendStorage.getFolderServerIds()
        assertTrue(FOLDER_SERVER_ID in folderServerIds)

        return K9BackendFolder(preferences, account, localStore, FOLDER_SERVER_ID)
    }

    fun createMessage(messageServerId: String, flags: Set<Flag> = emptySet()) {
        val message = MimeMessage().apply {
            subject = "Test message"
            setFrom(Address("alice@domain.example"))
            setHeader("To", "bob@domain.example")
            MimeMessageHelper.setBody(this, TextBody("Hello Bob!"))

            uid = messageServerId
            setFlags(flags, true)
        }

        backendFolder.saveCompleteMessage(message)

        val messageServerIds = backendFolder.getAllMessagesAndEffectiveDates().keys
        assertTrue(messageServerId in messageServerIds)
    }

    private fun setFlagsColumnToNull() {
        dbOperation { db ->
            val numberOfUpdatedRows = db.update(
                    "messages",
                    contentValuesOf("flags" to null),
                    "uid = ?",
                    arrayOf(MESSAGE_SERVER_ID)
            )
            assertEquals(1, numberOfUpdatedRows)
        }
    }

    private fun dbOperation(action: (SQLiteDatabase) -> Unit) = database.execute(false, action)

    companion object {
        const val FOLDER_SERVER_ID = "testFolder"
        const val FOLDER_NAME = "Test Folder"
        val FOLDER_TYPE = FolderType.INBOX
        const val MESSAGE_SERVER_ID = "msg001"
    }
}
