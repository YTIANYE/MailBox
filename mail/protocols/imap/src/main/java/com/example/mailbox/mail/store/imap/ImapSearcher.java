package com.example.mailbox.mail.store.imap;


import java.io.IOException;
import java.util.List;

import com.example.mailbox.mail.MessagingException;


interface ImapSearcher {
    List<ImapResponse> search() throws IOException, MessagingException;
}
