package com.example.mailbox.search

import org.koin.dsl.module

val searchModule = module {
    single { AccountSearchConditions() }
}
