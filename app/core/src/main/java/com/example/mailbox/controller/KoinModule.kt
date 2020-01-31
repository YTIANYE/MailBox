package com.example.mailbox.controller

import org.koin.core.qualifier.named
import org.koin.dsl.module

val controllerModule = module {
    single {
        MessagingController(get(), get(), get(), get(), get(), get(), get(), get(), get(named("controllerExtensions")))
    }
    single<UnreadMessageCountProvider> { DefaultUnreadMessageCountProvider(get(), get(), get(), get()) }
}
