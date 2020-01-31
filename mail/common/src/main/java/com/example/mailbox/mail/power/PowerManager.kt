package com.example.mailbox.mail.power

interface PowerManager {
    fun newWakeLock(tag: String): WakeLock
}
