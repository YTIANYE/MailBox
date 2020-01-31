package com.example.mailbox.mail;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.example.mailbox.mail.internet.BinaryTempFileBody;
import com.example.mailbox.mail.internet.BinaryTempFileMessageBody;
import com.example.mailbox.mail.internet.MimeUtility;
import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.util.MimeUtil;


public class DefaultBodyFactory implements BodyFactory {
    public Body createBody(String contentTransferEncoding, String contentType, InputStream inputStream)
            throws IOException {

        if (contentTransferEncoding != null) {
            contentTransferEncoding = MimeUtility.getHeaderParameter(contentTransferEncoding, null);
        }

        final BinaryTempFileBody tempBody;
        if (MimeUtil.isMessage(contentType)) {
            tempBody = new BinaryTempFileMessageBody(contentTransferEncoding);
        } else {
            tempBody = new BinaryTempFileBody(contentTransferEncoding);
        }

        OutputStream outputStream = tempBody.getOutputStream();
        try {
            copyData(inputStream, outputStream);
        } finally {
            outputStream.close();
        }

        return tempBody;
    }

    protected void copyData(InputStream inputStream, OutputStream outputStream) throws IOException {
        IOUtils.copy(inputStream, outputStream);
    }
}
