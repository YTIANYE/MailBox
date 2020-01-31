package com.example.mailbox.mail.store.pop3;


import com.example.mailbox.mail.MessagingException;


/**
 * Exception that is thrown if the server returns an error response.
 */
class Pop3ErrorResponse extends MessagingException {
    private static final long serialVersionUID = 3672087845857867174L;

    public Pop3ErrorResponse(String message) {
        super(message, true);
    }
}
