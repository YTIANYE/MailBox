package com.fsck.k9.storage.migrations

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.fsck.k9.mail.FetchProfile
import com.fsck.k9.mail.MessagingException
import com.fsck.k9.mailstore.LocalFolder
import com.fsck.k9.mailstore.LocalStore
import timber.log.Timber

/**  将localStore 中的消息存入数据库 */
internal class FullTextIndexer(val localStore: LocalStore, val database: SQLiteDatabase) {
    private val fulltextCreator = localStore.messageFulltextCreator
    private val fetchProfile = FetchProfile().apply { add(FetchProfile.Item.BODY) }

    /**向所有文件夹中 插入 全部消息*/
    fun indexAllMessages() {
        try {
            val folders = localStore.getPersonalNamespaces(true)
            for (folder in folders) {
                indexFolder(folder) //向文件夹中插入消息
            }
        } catch (e: MessagingException) {
            Timber.e(e, "error indexing fulltext - skipping rest, fts index is incomplete!")
        }
    }

    /** 向文件夹中 插入消息*/
    private fun indexFolder(folder: LocalFolder) {
        val messageUids = folder.allMessageUids
        for (messageUid in messageUids) {
            indexMessage(folder, messageUid)
        }
    }

    /**  向 fulltext 表中插入数据 */
    private fun indexMessage(folder: LocalFolder, messageUid: String?) {
        val localMessage = folder.getMessage(messageUid)    //本地消息
        folder.fetch(listOf(localMessage), fetchProfile, null)

        val fulltext = fulltextCreator.createFulltext(localMessage) //提取邮件正文
        if (fulltext.isNullOrEmpty()) {
            Timber.d("no fulltext for msg id %d :(", localMessage.databaseId)
        } else {
            Timber.d("fulltext for msg id %d is %d chars long", localMessage.databaseId, fulltext.length)

            val values = ContentValues().apply {
                put("docid", localMessage.databaseId)   //设置 docid 为本地localMessage的id
                put("fulltext", fulltext)
            }
            database.insert("messages_fulltext", null, values)//向 fulltext 表中插入数据
        }
    }
}
