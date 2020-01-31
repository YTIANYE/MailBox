package com.example.mailbox.autodiscovery

import com.example.mailbox.mail.ServerSettings

data class ConnectionSettings(val incoming: ServerSettings, val outgoing: ServerSettings)
