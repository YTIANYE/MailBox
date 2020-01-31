package com.example.mailbox.autodiscovery

interface ConnectionSettingsDiscovery {
    fun discover(email: String): ConnectionSettings?
}
