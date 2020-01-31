package com.example.mailbox.mailstore

data class OutboxState(
    val sendState: SendState,
    val numberOfSendAttempts: Int,
    val sendError: String?,
    val sendErrorTimestamp: Long
)
