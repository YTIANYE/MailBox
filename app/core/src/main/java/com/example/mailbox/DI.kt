package com.example.mailbox

import android.app.Application
import com.example.mailbox.core.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.java.KoinJavaComponent.get as koinGet
import org.koin.java.KoinJavaComponent.getKoin

object DI {
    private const val DEBUG = false

    @JvmStatic fun start(application: Application, modules: List<Module>) {
        startKoin {
            if (BuildConfig.DEBUG && DEBUG) {
                androidLogger()
            }

            androidContext(application)
            modules(modules)
        }
    }

    @JvmStatic
    fun <T : Any> get(clazz: Class<T>): T {
        return koinGet(clazz)
    }
}

interface EarlyInit

// Copied from ComponentCallbacks.inject()
inline fun <reified T : Any> EarlyInit.inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = lazy { getKoin().get<T>(qualifier, parameters) }
