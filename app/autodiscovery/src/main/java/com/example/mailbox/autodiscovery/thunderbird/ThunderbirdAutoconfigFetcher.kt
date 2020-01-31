package com.example.mailbox.autodiscovery.thunderbird

import com.example.mailbox.helper.EmailHelper
import java.io.InputStream
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class ThunderbirdAutoconfigFetcher(private val okHttpClient: OkHttpClient) {

    fun fetchAutoconfigFile(email: String): InputStream? {
        val url = getAutodiscoveryAddress(email)
        val request = Request.Builder().url(url).build()

        val response = okHttpClient.newCall(request).execute()

        return if (response.isSuccessful) {
            response.body?.byteStream()
        } else {
            null
        }
    }

    companion object {
        // address described at:
        // https://developer.mozilla.org/en-US/docs/Mozilla/Thunderbird/Autoconfiguration#Configuration_server_at_ISP
        internal fun getAutodiscoveryAddress(email: String): HttpUrl {
            val domain = EmailHelper.getDomainFromEmailAddress(email)
            requireNotNull(domain) { "Couldn't extract domain from email address: $email" }

            return HttpUrl.Builder()
                    .scheme("https")
                    .host(domain)
                    .addEncodedPathSegments(".well-known/autoconfig/mail/config-v1.1.xml")
                    .addQueryParameter("emailaddress", email)
                    .build()
        }
    }
}
