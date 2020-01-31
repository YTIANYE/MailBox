package com.example.mailbox.mail.store.imap;

import java.io.IOException;

interface UntaggedHandler {
    void handleAsyncUntaggedResponse(ImapResponse response) throws IOException;
}
