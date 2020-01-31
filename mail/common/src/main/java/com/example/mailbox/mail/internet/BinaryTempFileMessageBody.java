package com.example.mailbox.mail.internet;


import com.example.mailbox.mail.Body;
import com.example.mailbox.mail.MessagingException;
import org.apache.james.mime4j.util.MimeUtil;

/**
 * A {@link BinaryTempFileBody} extension containing a body of type message/rfc822.
 */
public class BinaryTempFileMessageBody extends BinaryTempFileBody implements Body {

    public BinaryTempFileMessageBody(String encoding) {
        super(encoding);
    }

    @Override
    public void setEncoding(String encoding) throws MessagingException {
        if (!MimeUtil.ENC_7BIT.equalsIgnoreCase(encoding)
                && !MimeUtil.ENC_8BIT.equalsIgnoreCase(encoding)) {
            throw new MessagingException("Incompatible content-transfer-encoding for a message/rfc822 body");
        }
        mEncoding = encoding;
    }
}
