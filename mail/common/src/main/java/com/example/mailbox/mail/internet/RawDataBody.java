package com.example.mailbox.mail.internet;


import com.example.mailbox.mail.Body;


/**
 * See {@link MimeUtility#decodeBody(Body)}
 */
public interface RawDataBody extends Body {
    String getEncoding();
}
