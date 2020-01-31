package com.example.mailbox.message.html

class DisplayHtmlFactory {
    fun create(settings: HtmlSettings): DisplayHtml {
        return DisplayHtml(settings)
    }
}
