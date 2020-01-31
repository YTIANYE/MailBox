package com.example.mailbox.message

interface Attachment {
    val state: LoadingState
    val fileName: String?
    val contentType: String?
    val name: String?
    val size: Long?

    enum class LoadingState {
        URI_ONLY,
        METADATA,
        COMPLETE,
        CANCELLED
    }
}
