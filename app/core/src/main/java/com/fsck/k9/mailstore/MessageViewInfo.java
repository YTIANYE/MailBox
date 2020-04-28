package com.fsck.k9.mailstore;


import com.fsck.k9.mail.Message;
import com.fsck.k9.mail.Part;

import java.util.Collections;
import java.util.List;


/**
 * 用于 显示消息具体信息 的类
 * */

public class MessageViewInfo {
    public final Message message;
    public final boolean isMessageIncomplete;
    public final Part rootPart;
    public final String subject;    //消息主题
    public final boolean isSubjectEncrypted;
    public final AttachmentResolver attachmentResolver;
    public final String text;   //消息文本
    public final CryptoResultAnnotation cryptoResultAnnotation;
    public final List<AttachmentViewInfo> attachments;
    public final String extraText;
    public final List<AttachmentViewInfo> extraAttachments;

    /*构造函数*/
    public MessageViewInfo(
            Message message, boolean isMessageIncomplete, Part rootPart,
            String subject, boolean isSubjectEncrypted,
            String text, List<AttachmentViewInfo> attachments,
            CryptoResultAnnotation cryptoResultAnnotation,
            AttachmentResolver attachmentResolver,
            String extraText, List<AttachmentViewInfo> extraAttachments) {
        this.message = message;
        this.isMessageIncomplete = isMessageIncomplete;
        this.rootPart = rootPart;
        this.subject = subject;
        this.isSubjectEncrypted = isSubjectEncrypted;
        this.text = text;
        this.cryptoResultAnnotation = cryptoResultAnnotation;
        this.attachmentResolver = attachmentResolver;
        this.attachments = attachments;
        this.extraText = extraText;
        this.extraAttachments = extraAttachments;
    }

    static MessageViewInfo createWithExtractedContent(Message message, Part rootPart, boolean isMessageIncomplete,
            String text, List<AttachmentViewInfo> attachments, AttachmentResolver attachmentResolver) {
        return new MessageViewInfo(
                message, isMessageIncomplete, rootPart, null, false, text, attachments, null, attachmentResolver, null,
                Collections.<AttachmentViewInfo>emptyList());
    }

    public static MessageViewInfo createWithErrorState(Message message, boolean isMessageIncomplete) {
        return new MessageViewInfo(message, isMessageIncomplete, null, null, false, null, null, null, null, null, null);
    }

    /** 为元数据创造 messageViewInfo */
    public static MessageViewInfo createForMetadataOnly(Message message, boolean isMessageIncomplete) {
        return new MessageViewInfo(message, isMessageIncomplete, null, null, false, null, null, null, null, null, null);
    }

    MessageViewInfo withCryptoData(CryptoResultAnnotation rootPartAnnotation, String extraViewableText,
            List<AttachmentViewInfo> extraAttachmentInfos) {
        return new MessageViewInfo(
                message, isMessageIncomplete, rootPart, subject, isSubjectEncrypted, text, attachments,
                rootPartAnnotation,
                attachmentResolver,
                extraViewableText, extraAttachmentInfos
        );
    }

    MessageViewInfo withSubject(String subject, boolean isSubjectEncrypted) {
        return new MessageViewInfo(
                message, isMessageIncomplete, rootPart, subject, isSubjectEncrypted, text, attachments,
                cryptoResultAnnotation, attachmentResolver, extraText, extraAttachments
        );
    }
}
