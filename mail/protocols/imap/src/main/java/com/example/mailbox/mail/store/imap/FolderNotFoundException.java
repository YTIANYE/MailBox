package com.example.mailbox.mail.store.imap;


import com.example.mailbox.mail.MessagingException;


public class FolderNotFoundException extends MessagingException {
    private final String folderServerId;


    public FolderNotFoundException(String folderServerId) {
        super("Folder not found: " + folderServerId);
        this.folderServerId = folderServerId;
    }

    public String getFolderServerId() {
        return folderServerId;
    }
}
