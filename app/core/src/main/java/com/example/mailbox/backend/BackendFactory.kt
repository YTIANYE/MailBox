package com.example.mailbox.backend

import com.example.mailbox.Account
import com.example.mailbox.backend.api.Backend
import com.example.mailbox.mail.ServerSettings

interface BackendFactory {
    fun createBackend(account: Account): Backend

    fun decodeStoreUri(storeUri: String): ServerSettings
    fun createStoreUri(serverSettings: ServerSettings): String

    val transportUriPrefix: String
    fun decodeTransportUri(transportUri: String): ServerSettings
    fun createTransportUri(serverSettings: ServerSettings): String
}
