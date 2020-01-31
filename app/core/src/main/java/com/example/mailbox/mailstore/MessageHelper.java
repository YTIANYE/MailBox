package com.example.mailbox.mailstore;


import java.util.Stack;

import com.example.mailbox.mail.Body;
import com.example.mailbox.mail.BodyPart;
import com.example.mailbox.mail.MessagingException;
import com.example.mailbox.mail.Multipart;
import com.example.mailbox.mail.Part;
import com.example.mailbox.mail.internet.MimeBodyPart;


public class MessageHelper {

    public static boolean isCompletePartAvailable(Part part) {
        Stack<Part> partsToCheck = new Stack<>();
        partsToCheck.push(part);

        while (!partsToCheck.isEmpty()) {
            Part currentPart = partsToCheck.pop();
            Body body = currentPart.getBody();

            boolean isBodyMissing = body == null;
            if (isBodyMissing) {
                return false;
            }

            if (body instanceof Multipart) {
                Multipart multipart = (Multipart) body;
                for (BodyPart bodyPart : multipart.getBodyParts()) {
                    partsToCheck.push(bodyPart);
                }
            }
        }

        return true;
    }

    public static MimeBodyPart createEmptyPart() {
        try {
            return new MimeBodyPart(null);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
