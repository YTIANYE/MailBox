package com.example.mailbox

import com.example.mailbox.autocrypt.autocryptModule
import com.example.mailbox.controller.controllerModule
import com.example.mailbox.crypto.openPgpModule
import com.example.mailbox.helper.helperModule
import com.example.mailbox.job.jobModule
import com.example.mailbox.mailstore.mailStoreModule
import com.example.mailbox.message.extractors.extractorModule
import com.example.mailbox.message.html.htmlModule
import com.example.mailbox.message.quote.quoteModule
import com.example.mailbox.notification.coreNotificationModule
import com.example.mailbox.search.searchModule

val coreModules = listOf(
        mainModule,
        openPgpModule,
        autocryptModule,
        mailStoreModule,
        searchModule,
        extractorModule,
        htmlModule,
        quoteModule,
        coreNotificationModule,
        controllerModule,
        jobModule,
        helperModule
)
