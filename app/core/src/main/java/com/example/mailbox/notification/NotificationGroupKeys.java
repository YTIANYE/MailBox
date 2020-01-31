package com.example.mailbox.notification;


import com.example.mailbox.Account;


public class NotificationGroupKeys {
    private static final String NOTIFICATION_GROUP_KEY_PREFIX = "newMailNotifications-";
    
    
    public static String getGroupKey(Account account) {
        return NOTIFICATION_GROUP_KEY_PREFIX + account.getAccountNumber();
    }
}
