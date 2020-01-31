package com.example.mailbox.backend.api

import com.example.mailbox.mail.Folder

data class FolderInfo(val serverId: String, val name: String, val type: Folder.FolderType)
