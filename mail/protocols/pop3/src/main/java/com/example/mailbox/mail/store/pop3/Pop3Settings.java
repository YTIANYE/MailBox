package com.example.mailbox.mail.store.pop3;


import com.example.mailbox.mail.AuthType;
import com.example.mailbox.mail.ConnectionSecurity;


interface Pop3Settings {
    String getHost();

    int getPort();

    ConnectionSecurity getConnectionSecurity();

    AuthType getAuthType();

    String getUsername();

    String getPassword();

    String getClientCertificateAlias();
}
