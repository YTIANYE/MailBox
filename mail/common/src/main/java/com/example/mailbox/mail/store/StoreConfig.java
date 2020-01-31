package com.example.mailbox.mail.store;


import com.example.mailbox.mail.NetworkType;

public interface StoreConfig {
    boolean isSubscribedFoldersOnly();
    boolean useCompression(NetworkType type);

    String getInboxFolder();
    String getOutboxFolder();
    String getDraftsFolder();

    int getMaximumAutoDownloadMessageSize();

    boolean isAllowRemoteSearch();
    boolean isRemoteSearchFullText();

    boolean isPushPollOnConnect();

    int getDisplayCount();

    int getIdleRefreshMinutes();
}
