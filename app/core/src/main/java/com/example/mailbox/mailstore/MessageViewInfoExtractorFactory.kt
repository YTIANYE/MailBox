package com.example.mailbox.mailstore

import com.example.mailbox.CoreResourceProvider
import com.example.mailbox.message.extractors.AttachmentInfoExtractor
import com.example.mailbox.message.html.HtmlProcessorFactory
import com.example.mailbox.message.html.HtmlSettings

class MessageViewInfoExtractorFactory(
    private val attachmentInfoExtractor: AttachmentInfoExtractor,
    private val htmlProcessorFactory: HtmlProcessorFactory,
    private val resourceProvider: CoreResourceProvider
) {
    fun create(settings: HtmlSettings): MessageViewInfoExtractor {
        val htmlProcessor = htmlProcessorFactory.create(settings)
        return MessageViewInfoExtractor(attachmentInfoExtractor, htmlProcessor, resourceProvider)
    }
}
