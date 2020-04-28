package com.fsck.k9

import android.app.Application
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.fsck.k9.activity.MessageCompose
import com.fsck.k9.controller.MessagingController
import com.fsck.k9.external.MessageProvider
import com.fsck.k9.ui.ThemeManager
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject


class App : Application() {
    private val messagingController: MessagingController by inject()
    private val messagingListenerProvider: MessagingListenerProvider by inject()
    private val themeManager: ThemeManager by inject()

    override fun onCreate() {
        Core.earlyInit(this)

        super.onCreate()

        //添加用于数据库查看
        Stetho.initializeWithDefaults(this)
        OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()

        DI.start(this, coreModules + uiModules + appModules)

        K9.init(this)//Unable to create application com.fsck.k9.App: j.c.c.f.c: Could not create instance for [type:Single,primary_type:'com.fsck.k9.v']
        Core.init(this)
        MessageProvider.init()
        themeManager.init()

        messagingListenerProvider.listeners.forEach { listener ->
            messagingController.addListener(listener)
        }
    }

    companion object {
        val appConfig = AppConfig(
                componentsToDisable = listOf(MessageCompose::class.java)
        )
    }
}
