package com.example.mailbox.crypto

import androidx.lifecycle.LifecycleOwner
import org.koin.dsl.module
import org.openintents.openpgp.OpenPgpApiManager

val openPgpModule = module {
    factory { (lifecycleOwner: LifecycleOwner) ->
        OpenPgpApiManager(get(), lifecycleOwner)
    }
}
