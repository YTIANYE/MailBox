package com.example.mailbox.crypto;


import com.example.mailbox.Identity;
import com.example.mailbox.helper.StringHelper;


public class OpenPgpApiHelper {

    /**
     * Create an "account name" from the supplied identity for use with the OpenPgp API's
     * <code>EXTRA_ACCOUNT_NAME</code>.
     *
     * @return A string with the following format:
     *         <code>display name &lt;user@example.com&gt;</code>
     */
    public static String buildUserId(Identity identity) {
        StringBuilder sb = new StringBuilder();

        String name = identity.getName();
        if (!StringHelper.isNullOrEmpty(name)) {
            sb.append(name).append(" ");
        }
        sb.append("<").append(identity.getEmail()).append(">");

        return sb.toString();
    }
}
