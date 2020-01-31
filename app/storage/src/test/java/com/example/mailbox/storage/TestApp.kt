package com.example.mailbox.storage

import android.app.Application
import com.example.mailbox.AppConfig
import com.example.mailbox.Core
import com.example.mailbox.CoreResourceProvider
import com.example.mailbox.DI
import com.example.mailbox.K9
import com.example.mailbox.coreModules
import com.example.mailbox.crypto.EncryptionExtractor
import com.example.mailbox.preferences.K9StoragePersister
import com.example.mailbox.preferences.StoragePersister
import com.nhaarman.mockito_kotlin.mock
import org.koin.dsl.module

class TestApp : Application() {
    override fun onCreate() {
        Core.earlyInit(this)

        super.onCreate()
        DI.start(this, coreModules + storageModule + testModule)

        K9.init(this)
        Core.init(this)
    }
}

val testModule = module {
    single { AppConfig(emptyList()) }
    single { mock<CoreResourceProvider>() }
    single { mock<EncryptionExtractor>() }
    single<StoragePersister> { K9StoragePersister(get()) }
}
