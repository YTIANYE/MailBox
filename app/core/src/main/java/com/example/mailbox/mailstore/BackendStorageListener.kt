package com.example.mailbox.mailstore

import com.example.mailbox.backend.api.FolderInfo
import com.example.mailbox.mail.Folder.FolderType

interface BackendStorageListener {
    fun onFoldersCreated(folders: List<FolderInfo>)
    fun onFoldersDeleted(folderServerIds: List<String>)
    fun onFolderChanged(folderServerId: String, name: String, type: FolderType)
}
