package com.example.mailbox.helper

import org.koin.dsl.module

val helperModule = module {
    single { ClipboardManager(get()) }
}
