package com.example.mailbox.message

interface CryptoStatus {
    val openPgpKeyId: Long?
    fun isProviderStateOk(): Boolean
    val isSenderPreferEncryptMutual: Boolean
    val isEncryptionEnabled: Boolean
    val isPgpInlineModeEnabled: Boolean
    val isSignOnly: Boolean
    fun isSigningEnabled(): Boolean
    fun isUserChoice(): Boolean
    val isReplyToEncrypted: Boolean
    fun hasRecipients(): Boolean
    val isEncryptAllDrafts: Boolean
    val isEncryptSubject: Boolean
    fun getRecipientAddresses(): Array<String>
}
