package com.example.mailbox.backend.api

import com.example.mailbox.mail.Folder.FolderType

interface BackendStorage {
    fun getFolder(folderServerId: String): BackendFolder

    fun getFolderServerIds(): List<String>

    fun createFolders(folders: List<FolderInfo>)
    fun deleteFolders(folderServerIds: List<String>)
    fun changeFolder(folderServerId: String, name: String, type: FolderType)

    fun getExtraString(name: String): String?
    fun setExtraString(name: String, value: String)
    fun getExtraNumber(name: String): Long?
    fun setExtraNumber(name: String, value: Long)
}
