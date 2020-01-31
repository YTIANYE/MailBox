package com.example.mailbox.autocrypt

interface AutocryptStringProvider {
    fun transferMessageSubject(): String
    fun transferMessageBody(): String
}
