package com.example.mailbox.message.extractors

import org.koin.dsl.module

val extractorModule = module {
    single { AttachmentInfoExtractor(get()) }
}
