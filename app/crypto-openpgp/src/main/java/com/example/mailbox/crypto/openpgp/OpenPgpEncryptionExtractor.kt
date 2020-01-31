package com.example.mailbox.crypto.openpgp

import com.example.mailbox.crypto.EncryptionExtractor
import com.example.mailbox.crypto.EncryptionResult
import com.example.mailbox.mail.Message
import com.example.mailbox.message.extractors.TextPartFinder

class OpenPgpEncryptionExtractor internal constructor(
    private val encryptionDetector: EncryptionDetector
) : EncryptionExtractor {

    override fun extractEncryption(message: Message): EncryptionResult? {
        return if (encryptionDetector.isEncrypted(message)) {
            EncryptionResult(ENCRYPTION_TYPE, 0)
        } else {
            null
        }
    }

    companion object {
        const val ENCRYPTION_TYPE = "openpgp"

        @JvmStatic
        fun newInstance(): OpenPgpEncryptionExtractor {
            val textPartFinder = TextPartFinder()
            val encryptionDetector = EncryptionDetector(textPartFinder)
            return OpenPgpEncryptionExtractor(encryptionDetector)
        }
    }
}
