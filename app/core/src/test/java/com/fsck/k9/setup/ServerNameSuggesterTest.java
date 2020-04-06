package com.fsck.k9.setup;


import com.fsck.k9.preferences.Protocols;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ServerNameSuggesterTest {
    private ServerNameSuggester serverNameSuggester;


    @Before
    public void setUp() throws Exception {
        serverNameSuggester = new ServerNameSuggester();
    }

    @Test
    public void suggestServerName_forImapServer() throws Exception {
        String serverType = Protocols.IMAP;
        String domainPart = "fsck.org";

        String result = serverNameSuggester.suggestServerName(serverType, domainPart);

        assertEquals("imap.fsck.org", result);
    }

    @Test
    public void suggestServerName_forPop3Server() throws Exception {
        String serverType = Protocols.POP3;
        String domainPart = "fsck.org";

        String result = serverNameSuggester.suggestServerName(serverType, domainPart);

        assertEquals("pop3.fsck.org", result);
    }

    @Test
    public void suggestServerName_forWebDavServer() throws Exception {
        String serverType = Protocols.WEBDAV;
        String domainPart = "fsck.org";

        String result = serverNameSuggester.suggestServerName(serverType, domainPart);

        assertEquals("exchange.fsck.org", result);
    }

    @Test
    public void suggestServerName_forSmtpServer() throws Exception {
        String serverType = Protocols.SMTP;
        String domainPart = "fsck.org";

        String result = serverNameSuggester.suggestServerName(serverType, domainPart);

        assertEquals("smtp.fsck.org", result);
    }
}
