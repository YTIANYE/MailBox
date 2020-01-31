package com.example.mailbox

import android.content.Context
import com.example.mailbox.helper.Contacts
import com.example.mailbox.mail.power.PowerManager
import com.example.mailbox.mail.ssl.DefaultTrustedSocketFactory
import com.example.mailbox.mail.ssl.LocalKeyStore
import com.example.mailbox.mail.ssl.TrustManagerFactory
import com.example.mailbox.mail.ssl.TrustedSocketFactory
import com.example.mailbox.mailstore.LocalStoreProvider
import com.example.mailbox.power.TracingPowerManager
import com.example.mailbox.setup.ServerNameSuggester
import org.koin.dsl.module

val mainModule = module {
    single { Preferences.getPreferences(get()) }
    single { get<Context>().resources }
    single { LocalStoreProvider() }
    single<PowerManager> { TracingPowerManager.getPowerManager(get()) }
    single { Contacts.getInstance(get()) }
    single { LocalKeyStore.createInstance(get()) }
    single { TrustManagerFactory.createInstance(get()) }
    single { LocalKeyStoreManager(get()) }
    single<TrustedSocketFactory> { DefaultTrustedSocketFactory(get(), get()) }
    single { Clock.INSTANCE }
    factory { ServerNameSuggester() }
}
