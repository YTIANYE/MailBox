package com.example.mailbox.mail;


import java.util.List;

import com.example.mailbox.mail.power.WakeLock;


public interface PushReceiver {
    void syncFolder(String folderServerId);
    void messagesArrived(String folderServerId, List<Message> mess);
    void messagesFlagsChanged(String folderServerId, List<Message> mess);
    void messagesRemoved(String folderServerId, List<Message> mess);
    String getPushState(String folderServerId);
    void pushError(String errorMessage, Exception e);
    void authenticationFailed();
    void setPushActive(String folderServerId, boolean enabled);
    void sleep(WakeLock wakeLock, long millis);
}
