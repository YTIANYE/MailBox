package com.example.mailbox.crypto

import android.content.ContentValues
import com.example.mailbox.mail.Message
import com.example.mailbox.message.extractors.PreviewResult

interface EncryptionExtractor {
    fun extractEncryption(message: Message): EncryptionResult?
}

data class EncryptionResult(
    val encryptionType: String,
    val attachmentCount: Int,
    val previewResult: PreviewResult = PreviewResult.encrypted(),
    val textForSearchIndex: String? = null,
    val extraContentValues: ContentValues? = null
)
