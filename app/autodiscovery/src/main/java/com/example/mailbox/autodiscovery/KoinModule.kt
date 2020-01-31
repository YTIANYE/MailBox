package com.example.mailbox.autodiscovery

import com.example.mailbox.autodiscovery.providersxml.ProvidersXmlDiscovery
import com.example.mailbox.autodiscovery.providersxml.ProvidersXmlProvider
import org.koin.dsl.module

val autodiscoveryModule = module {
    factory { ProvidersXmlProvider(get()) }
    factory { ProvidersXmlDiscovery(get(), get()) }
}
