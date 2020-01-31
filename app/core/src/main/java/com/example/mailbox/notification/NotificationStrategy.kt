package com.example.mailbox.notification

import com.example.mailbox.Account
import com.example.mailbox.mail.Message
import com.example.mailbox.mailstore.LocalFolder

interface NotificationStrategy {

    fun shouldNotifyForMessage(
        account: Account,
        localFolder: LocalFolder,
        message: Message,
        isOldMessage: Boolean
    ): Boolean
}
