package com.example.mailbox.message.html

import org.koin.dsl.module

val htmlModule = module {
    single { HtmlProcessorFactory(get(), get()) }
    single { HtmlSanitizer() }
    single { DisplayHtmlFactory() }
}
