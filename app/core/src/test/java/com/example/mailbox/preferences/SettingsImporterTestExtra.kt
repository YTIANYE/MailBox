@file:JvmName("MessagingControllerTestExtra")
package com.example.mailbox.preferences

import com.example.mailbox.Account
import com.example.mailbox.backend.BackendFactory
import com.example.mailbox.backend.BackendManager
import com.example.mailbox.backend.api.Backend
import com.example.mailbox.backend.imap.ImapStoreUriCreator
import com.example.mailbox.backend.imap.ImapStoreUriDecoder
import com.example.mailbox.mail.ServerSettings
import com.example.mailbox.mail.transport.smtp.SmtpTransportUriCreator
import com.example.mailbox.mail.transport.smtp.SmtpTransportUriDecoder
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

fun setUpBackendManager() {
    val backendFactory = object : BackendFactory {
        override val transportUriPrefix = "smtp"

        override fun createBackend(account: Account): Backend {
            throw UnsupportedOperationException("not implemented")
        }

        override fun decodeStoreUri(storeUri: String): ServerSettings {
            return ImapStoreUriDecoder.decode(storeUri)
        }

        override fun createStoreUri(serverSettings: ServerSettings): String {
            return ImapStoreUriCreator.create(serverSettings)
        }

        override fun decodeTransportUri(transportUri: String): ServerSettings {
            return SmtpTransportUriDecoder.decodeSmtpUri(transportUri)
        }

        override fun createTransportUri(serverSettings: ServerSettings): String {
            return SmtpTransportUriCreator.createSmtpUri(serverSettings)
        }
    }

    loadKoinModules(module {
        single { BackendManager(mapOf("imap" to backendFactory)) }
    })
}
