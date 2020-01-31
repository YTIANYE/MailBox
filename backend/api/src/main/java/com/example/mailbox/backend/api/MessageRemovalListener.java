package com.example.mailbox.backend.api;

import com.example.mailbox.mail.Message;

public interface MessageRemovalListener {
    void messageRemoved(Message message);
}
