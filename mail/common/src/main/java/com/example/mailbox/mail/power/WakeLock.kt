package com.example.mailbox.mail.power

interface WakeLock {
    fun acquire(timeout: Long)
    fun acquire()
    fun setReferenceCounted(counted: Boolean)
    fun release()
}
