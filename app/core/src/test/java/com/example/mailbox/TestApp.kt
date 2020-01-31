package com.example.mailbox

import android.app.Application
import com.example.mailbox.crypto.EncryptionExtractor
import com.example.mailbox.preferences.InMemoryStoragePersister
import com.example.mailbox.preferences.StoragePersister
import com.example.mailbox.storage.storageModule
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
    single<StoragePersister> { InMemoryStoragePersister() }
}
