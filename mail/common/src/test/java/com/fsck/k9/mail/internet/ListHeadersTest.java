package com.fsck.k9.mail.internet;


import com.fsck.k9.mail.Address;
import com.fsck.k9.mail.K9LibRobolectricTestRunner;
import com.fsck.k9.mail.Message;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(K9LibRobolectricTestRunner.class)
public class ListHeadersTest {
    private static final String[] TEST_EMAIL_ADDRESSES = new String[] {
            "prettyandsimple@fsck.com",
            "very.common@fsck.com",
            "disposable.style.email.with+symbol@fsck.com",
            "other.email-with-dash@fsck.com",
            //TODO: Fix Address.parse()
            /*
            "\"much.more unusual\"@fsck.com",
            "\"very.unusual.@.unusual.com\"@fsck.com",
            //"very.(),:;<>[]\".VERY.\"very@\\ \"very\".unusual"@strange.fsck.com
            "\"very.(),:;<>[]\\\".VERY.\\\"very@\\\\ \\\"very\\\".unusual\"@strange.fsck.com",
            "admin@mailserver1",
            "#!$%&'*+-/=?^_`{}|~@fsck.org",
            "\"()<>[]:,;@\\\\\\\"!#$%&'*+-/=?^_`{}| ~.a\"@fsck.org",
            "\" \"@fsck.org",
            "fsck@localhost",
            "fsck@s.solutions",
            "user@com",
            "user@localserver",
            "user@[IPv6:2001:db8::1]"
             */
    };


    @Test
    public void getListPostAddresses_withMailTo_shouldReturnCorrectAddress() throws Exception {
        for (String emailAddress : TEST_EMAIL_ADDRESSES) {
            String headerValue = "<mailto:" + emailAddress + ">";
            Message message = buildMimeMessageWithListPostValue(headerValue);

            Address[] result = ListHeaders.getListPostAddresses(message);

            assertExtractedAddressMatchesEmail(emailAddress, result);
        }
    }

    @Test
    public void getListPostAddresses_withMailtoWithNote_shouldReturnCorrectAddress() throws Exception {
        for (String emailAddress : TEST_EMAIL_ADDRESSES) {
            String headerValue = "<mailto:" + emailAddress + "> (Postings are Moderated)";
            Message message = buildMimeMessageWithListPostValue(headerValue);

            Address[] result = ListHeaders.getListPostAddresses(message);

            assertExtractedAddressMatchesEmail(emailAddress, result);
        }
    }

    @Test
    public void getListPostAddresses_withMailtoWithSubject_shouldReturnCorrectAddress() throws Exception {
        for (String emailAddress : TEST_EMAIL_ADDRESSES) {
            String headerValue = "<mailto:" + emailAddress + "?subject=list%20posting>";
            Message message = buildMimeMessageWithListPostValue(headerValue);

            Address[] result = ListHeaders.getListPostAddresses(message);

            assertExtractedAddressMatchesEmail(emailAddress, result);
        }
    }

    @Test
    public void getListPostAddresses_withMessageWithNo_shouldReturnEmptyList() throws Exception {
        MimeMessage message = buildMimeMessageWithListPostValue("NO (posting not allowed on this list)");

        Address[] result = ListHeaders.getListPostAddresses(message);

        assertEquals(0, result.length);
    }

    @Test
    public void getListPostAddresses_shouldProvideAllListPostHeaders() throws Exception {
        MimeMessage message = buildMimeMessageWithListPostValue(
                "<mailto:list1@example.org>", "<mailto:list2@example.org>");

        Address[] result = ListHeaders.getListPostAddresses(message);

        assertNotNull(result);
        assertEquals(2, result.length);
        assertNotNull(result[0]);
        assertEquals("list1@fsck.org", result[0].getAddress());
        assertNotNull(result[1]);
        assertEquals("list2@fsck.org", result[1].getAddress());
    }

    @Test
    public void getListPostAddresses_withoutMailtoUriInBrackets_shouldReturnEmptyList() throws Exception {
        MimeMessage message = buildMimeMessageWithListPostValue("<x-mailto:something>");

        Address[] result = ListHeaders.getListPostAddresses(message);

        assertEquals(0, result.length);
    }

    private void assertExtractedAddressMatchesEmail(String emailAddress, Address[] result) {
        assertNotNull(result);
        assertEquals(1, result.length);
        assertNotNull(result[0]);
        assertEquals(emailAddress, result[0].getAddress());
    }

    private MimeMessage buildMimeMessageWithListPostValue(String... values) {
        MimeMessage message = new MimeMessage();
        for (String value : values) {
            message.addHeader("List-Post", value);
        }

        return message;
    }
}
