package com.example.mailbox.notification;


import com.example.mailbox.controller.MessageReference;


class NotificationContent {
    public final MessageReference messageReference;
    public final String sender;
    public final String subject;
    public final CharSequence preview;
    public final CharSequence summary;
    public final boolean starred;


    public NotificationContent(MessageReference messageReference, String sender, String subject, CharSequence preview,
            CharSequence summary, boolean starred) {
        this.messageReference = messageReference;
        this.sender = sender;
        this.subject = subject;
        this.preview = preview;
        this.summary = summary;
        this.starred = starred;
    }
}
