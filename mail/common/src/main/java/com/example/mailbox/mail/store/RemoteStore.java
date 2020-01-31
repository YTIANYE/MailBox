package com.example.mailbox.mail.store;


import java.util.List;

import com.example.mailbox.mail.Folder;
import com.example.mailbox.mail.Message;
import com.example.mailbox.mail.MessagingException;
import com.example.mailbox.mail.ssl.TrustedSocketFactory;


public abstract class RemoteStore {
    public static final int SOCKET_CONNECT_TIMEOUT = 30000;
    public static final int SOCKET_READ_TIMEOUT = 60000;

    protected StoreConfig mStoreConfig;
    protected TrustedSocketFactory mTrustedSocketFactory;


    public RemoteStore(StoreConfig storeConfig, TrustedSocketFactory trustedSocketFactory) {
        mStoreConfig = storeConfig;
        mTrustedSocketFactory = trustedSocketFactory;
    }

    public abstract Folder<? extends Message> getFolder(String name);

    public abstract List<? extends Folder > getPersonalNamespaces() throws MessagingException;

    public abstract void checkSettings() throws MessagingException;

    public boolean isCopyCapable() {
        return false;
    }

    public boolean isMoveCapable() {
        return false;
    }

    public boolean isPushCapable() {
        return false;
    }

    public boolean isExpungeCapable() {
        return false;
    }

    public boolean isSeenFlagSupported() {
        return true;
    }

    public void sendMessages(List<? extends Message> messages) throws MessagingException { }
}
