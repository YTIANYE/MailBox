package com.example.mailbox.job

import androidx.work.WorkManager
import com.example.mailbox.Preferences
import timber.log.Timber

class K9JobManager(
    private val workManager: WorkManager,
    private val preferences: Preferences,
    private val mailSyncWorkerManager: MailSyncWorkerManager
) {
    fun scheduleAllMailJobs() {
        Timber.v("scheduling all jobs")
        scheduleMailSync()
    }

    fun scheduleMailSync() {
        cancelAllMailSyncJobs()

        preferences.availableAccounts?.forEach { account ->
            mailSyncWorkerManager.scheduleMailSync(account)
        }
    }

    fun schedulePusherRefresh() {
        // Push is temporarily disabled. See GH-4253
    }

    private fun cancelAllMailSyncJobs() {
        Timber.v("canceling mail sync job")
        workManager.cancelAllWorkByTag(MailSyncWorkerManager.MAIL_SYNC_TAG)
    }
}
