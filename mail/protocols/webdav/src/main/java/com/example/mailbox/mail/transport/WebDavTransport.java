package com.example.mailbox.mail.transport;


import java.util.Collections;

import com.example.mailbox.mail.K9MailLib;
import com.example.mailbox.mail.Message;
import com.example.mailbox.mail.MessagingException;
import com.example.mailbox.mail.Transport;
import com.example.mailbox.mail.ssl.TrustManagerFactory;
import com.example.mailbox.mail.store.StoreConfig;
import com.example.mailbox.mail.store.webdav.WebDavStore;
import com.example.mailbox.mail.store.webdav.WebDavStoreSettings;
import timber.log.Timber;

public class WebDavTransport extends Transport {
    private WebDavStore store;

    public WebDavTransport(TrustManagerFactory trustManagerFactory, WebDavStoreSettings serverSettings, StoreConfig storeConfig) {
        store = new WebDavStore(trustManagerFactory, serverSettings, storeConfig);

        if (K9MailLib.isDebug())
            Timber.d(">>> New WebDavTransport creation complete");
    }

    @Override
    public void open() throws MessagingException {
        if (K9MailLib.isDebug())
            Timber.d( ">>> open called on WebDavTransport ");

        store.getHttpClient();
    }

    @Override
    public void close() {
    }

    @Override
    public void sendMessage(Message message) throws MessagingException {
        store.sendMessages(Collections.singletonList(message));
    }

    public void checkSettings() throws MessagingException {
        store.checkSettings();
    }
}
