package com.example.mailbox.message.extractors;


import androidx.annotation.NonNull;

import com.example.mailbox.mail.Message;
import com.example.mailbox.mail.Part;


public class MessagePreviewCreator {
    private final TextPartFinder textPartFinder;
    private final PreviewTextExtractor previewTextExtractor;


    MessagePreviewCreator(TextPartFinder textPartFinder, PreviewTextExtractor previewTextExtractor) {
        this.textPartFinder = textPartFinder;
        this.previewTextExtractor = previewTextExtractor;
    }

    public static MessagePreviewCreator newInstance() {
        TextPartFinder textPartFinder = new TextPartFinder();
        PreviewTextExtractor previewTextExtractor = new PreviewTextExtractor();
        return new MessagePreviewCreator(textPartFinder, previewTextExtractor);
    }

    public PreviewResult createPreview(@NonNull Message message) {
        Part textPart = textPartFinder.findFirstTextPart(message);
        if (textPart == null || hasEmptyBody(textPart)) {
            return PreviewResult.none();
        }

        try {
            String previewText = previewTextExtractor.extractPreview(textPart);
            return PreviewResult.text(previewText);
        } catch (PreviewExtractionException e) {
            return PreviewResult.error();
        }
    }

    private boolean hasEmptyBody(Part textPart) {
        return textPart.getBody() == null;
    }
}
