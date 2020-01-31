package com.example.mailbox.helper

import com.example.mailbox.Account
import com.example.mailbox.Identity
import com.example.mailbox.mail.Message
import com.example.mailbox.mail.Message.RecipientType

object IdentityHelper {
    private val RECIPIENT_TYPES = listOf(
            RecipientType.TO,
            RecipientType.CC,
            RecipientType.X_ORIGINAL_TO,
            RecipientType.DELIVERED_TO,
            RecipientType.X_ENVELOPE_TO
    )

    /**
     * Find the identity a message was sent to.
     *
     * @param account
     * The account the message belongs to.
     * @param message
     * The message to get the recipients from.
     *
     * @return The identity the message was sent to, or the account's default identity if it
     * couldn't be determined which identity this message was sent to.
     *
     * @see Account.findIdentity
     */
    @JvmStatic
    fun getRecipientIdentityFromMessage(account: Account, message: Message): Identity {
        val recipient: Identity? = RECIPIENT_TYPES.asSequence()
                .flatMap { recipientType -> message.getRecipients(recipientType).asSequence() }
                .map { address -> account.findIdentity(address) }
                .filterNotNull()
                .firstOrNull()

        return recipient ?: account.getIdentity(0)
    }
}
