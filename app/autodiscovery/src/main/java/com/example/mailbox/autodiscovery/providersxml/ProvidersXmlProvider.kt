package com.example.mailbox.autodiscovery.providersxml

import android.content.Context
import android.content.res.XmlResourceParser
import com.example.mailbox.autodiscovery.R

class ProvidersXmlProvider(private val context: Context) {
    fun getXml(): XmlResourceParser {
        return context.resources.getXml(R.xml.providers)
    }
}
