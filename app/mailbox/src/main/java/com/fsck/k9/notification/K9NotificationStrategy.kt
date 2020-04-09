package com.fsck.k9.notification

import com.fsck.k9.Account
import com.fsck.k9.K9
import com.fsck.k9.helper.Contacts
import com.fsck.k9.mail.Flag
import com.fsck.k9.mail.Message
import com.fsck.k9.mailstore.LocalFolder

class K9NotificationStrategy(val contacts: Contacts) : NotificationStrategy {

    override fun shouldNotifyForMessage(
        account: Account,
        localFolder: LocalFolder,
        message: Message,
        isOldMessage: Boolean
    ): Boolean {

        // If we don't even have an account name, don't show the notification.
        // (This happens during initial account setup)
        if (account.name == null) {
            return false
        }

        if (K9.isQuietTime && !K9.isNotificationDuringQuietTimeEnabled) {
            return false
        }

        // Do not notify if the user does not have notifications enabled or if the message has
        // been read.
        if (!account.isNotifyNewMail || message.isSet(Flag.SEEN) || isOldMessage) {
            return false
        }

        val aDisplayMode = account.folderDisplayMode
        val aNotifyMode = account.folderNotifyNewMailMode
        val fDisplayClass = localFolder.displayClass
        val fNotifyClass = localFolder.notifyClass

        if (LocalFolder.isModeMismatch(aDisplayMode, fDisplayClass)) {
            // Never notify a folder that isn't displayed
            return false
        }

        if (LocalFolder.isModeMismatch(aNotifyMode, fNotifyClass)) {
            // Do not notify folders in the wrong class
            return false
        }

        // No notification for new messages in Trash, Drafts, Spam or Sent folder.
        // But do notify if it's the INBOX (see issue 1817).
        val folder = message.folder
        if (folder != null) {
            val folderServerId = folder.serverId
            if (folderServerId != account.inboxFolder && (folderServerId == account.trashFolder ||
                            folderServerId == account.draftsFolder ||
                            folderServerId == account.spamFolder ||
                            folderServerId == account.sentFolder)) {
                return false
            }
        }

        // Don't notify if the sender address matches one of our identities and the user chose not
        // to be notified for such messages.
        return if (account.isAnIdentity(message.from) && !account.isNotifySelfNewMail) {
            false
        } else !account.isNotifyContactsMailOnly || contacts.isAnyInContacts(message.from)
    }
}
