package com.example.mailbox.mailstore;


public interface LocalPart {
    String getAccountUuid();
    long getPartId();
    long getSize();
    LocalMessage getMessage();
}
