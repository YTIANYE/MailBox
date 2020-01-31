package com.example.mailbox.message.quote

import org.koin.dsl.module

val quoteModule = module {
    factory { QuoteHelper(get()) }
    factory { TextQuoteCreator(get(), get()) }
}
