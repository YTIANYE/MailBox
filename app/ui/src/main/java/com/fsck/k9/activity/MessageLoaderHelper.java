package com.fsck.k9.activity;


import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;

import com.fsck.k9.Account;
import com.fsck.k9.Preferences;
import com.fsck.k9.autocrypt.AutocryptOperations;
import com.fsck.k9.controller.MessageReference;
import com.fsck.k9.controller.MessagingController;
import com.fsck.k9.controller.MessagingListener;
import com.fsck.k9.controller.SimpleMessagingListener;
import com.fsck.k9.helper.RetainFragment;
import com.fsck.k9.mail.Flag;
import com.fsck.k9.mail.MessagingException;
import com.fsck.k9.mailstore.LocalMessage;
import com.fsck.k9.mailstore.MessageCryptoAnnotations;
import com.fsck.k9.mailstore.MessageViewInfo;
import com.fsck.k9.mailstore.MessageViewInfoExtractor;
import com.fsck.k9.ui.crypto.MessageCryptoCallback;
import com.fsck.k9.ui.crypto.MessageCryptoHelper;
import com.fsck.k9.ui.crypto.OpenPgpApiFactory;
import com.fsck.k9.ui.message.LocalMessageExtractorLoader;
import com.fsck.k9.ui.message.LocalMessageLoader;

import org.openintents.openpgp.OpenPgpDecryptionResult;

import timber.log.Timber;


/** This class is responsible for loading a message start to finish, and
 * retaining or reloading the loading state on configuration changes.
 * 该类负责从开始到结束加载消息，并在配置更改时保留或重新加载加载状态。
 *
 * In particular, it takes care of the following:
 *  - load raw message data from the database, using LocalMessageLoader                    使用LocalMessageLoader从数据库加载原始消息数据
 *  - download partial message content if it is missing using MessagingController          使用MessagingController下载部分缺失的消息内容
 *  - apply crypto operations if applicable, using MessageCryptoHelper                      如果可以，使用MessageCryptoHelper应用加密操作
 *  - extract MessageViewInfo from the message and crypto data using DecodeMessageLoader    使用DecodeMessageLoader从消息和加密数据中提取MessageViewInfo
 *  - download complete message content for partially downloaded messages if requested      如果被请求，下载部分已下载消息的完整消息内容
 *
 * No state is retained in this object itself. Instead, state is stored in the
 * message loaders and the MessageCryptoHelper which is stored in a
 * RetainFragment. The public interface is intended for use by an Activity or
 * Fragment, which should construct a new instance of this class in onCreate,
 * then call asyncStartOrResumeLoadingMessage to start or resume loading the
 * message, receiving callbacks when it is loaded.
 *
 * 此对象本身不保留任何状态。相反，状态存储在消息加载器中，而MessageCryptoHelper存储在一个RetainFragment中。
 * 公共接口用于活动或片段，它们应该在onCreate中构造该类的新实例，
 * 然后调用asyncStartOrResumeLoadingMessage来开始或继续加载消息，在加载消息时接收回调。
 *
 * When the Activity or Fragment is ultimately destroyed, it should call
 * onDestroy, which stops loading and deletes all state kept in loaders and
 * fragments by this object. If it is only destroyed for a configuration
 * change, it should call onDestroyChangingConfigurations, which cancels any
 * further callbacks from this object but retains the loading state to resume
 * from at the next call to asyncStartOrResumeLoadingMessage.
 *
 * 当活动或片段最终被销毁时，它应该调用onDestroy，这将停止加载并删除该对象在加载器和片段中保存的所有状态。
 * 如果它只是因为配置更改而被销毁，那么它应该调用onDestroyChangingConfigurations，它取消该对象的任何进一步回调，
 * 但保留加载状态，以便在下一次调用asyncStartOrResumeLoadingMessage时恢复加载状态。
 *
 * If the message is already loaded, a call to asyncStartOrResumeLoadingMessage
 * will typically load by starting the decode message loader, retrieving the
 * already cached LocalMessage. This message will be passed to the retained
 * CryptoMessageHelper instance, returning the already cached
 * MessageCryptoAnnotations. These two objects will be checked against the
 * retained DecodeMessageLoader, returning the final result. At each
 * intermediate step, the input of the respective loaders will be checked for
 * consistency, reloading if there is a mismatch.
 *
 * 如果消息已经加载，对asyncStartOrResumeLoadingMessage的调用通常会通过启动解码消息加载器来加载，检索已经缓存的LocalMessage。
 * 此消息将传递到保留的CryptoMessageHelper实例，返回已缓存的MessageCryptoAnnotations。
 * 这两个对象将根据保留的DecodeMessageLoader进行检查，并返回最终结果。
 * 在每个中间步骤，将检查各自装入器的输入是否一致，如果不匹配，则重新装入。
 *
 */
public class MessageLoaderHelper {
    private static final int LOCAL_MESSAGE_LOADER_ID = 1;
    private static final int DECODE_MESSAGE_LOADER_ID = 2;


    // injected state - all of this may be cleared to avoid data leakage!
    //注入状态-所有这些都可以清除，以避免数据泄漏!
    private Context context;
    private FragmentManager fragmentManager;
    private LoaderManager loaderManager;
    @Nullable // make this explicitly nullable, make sure to cancel/ignore any operation if this is null
    //使此值显式为空，如果为空，请确保取消/忽略任何操作
    private MessageLoaderCallbacks callback;
    private final MessageViewInfoExtractor messageViewInfoExtractor;
    private Handler handler = new Handler(Looper.getMainLooper());

    // transient state  过渡状态
    private boolean onlyLoadMetadata;
    private MessageReference messageReference;
    private Account account;

    private LocalMessage localMessage;
    private MessageCryptoAnnotations messageCryptoAnnotations;
    private OpenPgpDecryptionResult cachedDecryptionResult;

    private MessageCryptoHelper messageCryptoHelper;


    public MessageLoaderHelper(Context context, LoaderManager loaderManager, FragmentManager fragmentManager,
                               @NonNull MessageLoaderCallbacks callback, MessageViewInfoExtractor messageViewInfoExtractor) {
        this.context = context;
        this.loaderManager = loaderManager;
        this.fragmentManager = fragmentManager;
        this.callback = callback;
        this.messageViewInfoExtractor = messageViewInfoExtractor;
    }


    // public interface
    @UiThread
    /**  显示具体消息内容 */
    public void asyncStartOrResumeLoadingMessage(MessageReference messageReference, Parcelable cachedDecryptionResult) {
        onlyLoadMetadata = false;
        this.messageReference = messageReference;
        this.account = Preferences.getPreferences(context).getAccount(messageReference.getAccountUuid());

        if (cachedDecryptionResult != null) {
            if (cachedDecryptionResult instanceof OpenPgpDecryptionResult) {
                this.cachedDecryptionResult = (OpenPgpDecryptionResult) cachedDecryptionResult;
            } else {
                Timber.e("Got decryption result of unknown type - ignoring");
            }
        }

        startOrResumeLocalMessageLoader();
    }

    @UiThread
    public void asyncStartOrResumeLoadingMessageMetadata(MessageReference messageReference) {
        onlyLoadMetadata = true;
        this.messageReference = messageReference;
        this.account = Preferences.getPreferences(context).getAccount(messageReference.getAccountUuid());

        startOrResumeLocalMessageLoader();
    }

    @UiThread
    public void asyncReloadMessage() {
        startOrResumeLocalMessageLoader();
    }

    @UiThread
    public void asyncRestartMessageCryptoProcessing() {
        cancelAndClearCryptoOperation();
        cancelAndClearDecodeLoader();

        String openPgpProvider = account.getOpenPgpProvider();
        if (openPgpProvider != null) {
            startOrResumeCryptoOperation(openPgpProvider);
        } else {
            startOrResumeDecodeMessage();
        }
    }

    /** Cancels all loading processes, prevents future callbacks, and destroys all loading state.
     * 取消所有加载过程，防止将来的回调，并销毁所有加载状态。
     * */
    @UiThread
    public void onDestroy() {
        if (messageCryptoHelper != null) {
            messageCryptoHelper.cancelIfRunning();
        }

        callback = null;
        context = null;
        fragmentManager = null;
        loaderManager = null;
    }

    /** Prevents future callbacks, but retains loading state to pick up from in a call to
     * asyncStartOrResumeLoadingMessage in a new instance of this class.
     * 防止将来的回调，但是保留加载状态，以便在这个类的新实例中从对asyncStartOrResumeLoadingMessage的调用中拾取
     * */
    @UiThread
    public void onDestroyChangingConfigurations() {
        cancelAndClearDecodeLoader();

        if (messageCryptoHelper != null) {
            messageCryptoHelper.detachCallback();
        }

        callback = null;
        context = null;
        fragmentManager = null;
        loaderManager = null;
    }

    @UiThread
    public void downloadCompleteMessage() {
        startDownloadingMessageBody(true);
    }

    @UiThread
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        messageCryptoHelper.onActivityResult(requestCode, resultCode, data);
    }


    // load from database   从数据库加载

    private void startOrResumeLocalMessageLoader() {
        LocalMessageLoader loader =
                (LocalMessageLoader) loaderManager.<LocalMessage>getLoader(LOCAL_MESSAGE_LOADER_ID);
        boolean isLoaderStale = (loader == null) || !loader.isCreatedFor(messageReference);

        if (isLoaderStale) {
            Timber.d("Creating new local message loader");//创建新的本地消息加载程序
            cancelAndClearCryptoOperation();
            cancelAndClearDecodeLoader();
            loaderManager.restartLoader(LOCAL_MESSAGE_LOADER_ID, null, localMessageLoaderCallback);
        } else {
            Timber.d("Reusing local message loader");//重用本地消息加载程序
            loaderManager.initLoader(LOCAL_MESSAGE_LOADER_ID, null, localMessageLoaderCallback);
        }
    }

    @UiThread
    private void onLoadMessageFromDatabaseFinished() {
        if (callback == null) {
            throw new IllegalStateException("unexpected call when callback is already detached");
        }

        callback.onMessageDataLoadFinished(localMessage);

        boolean downloadedCompletely = localMessage.isSet(Flag.X_DOWNLOADED_FULL);
        boolean downloadedPartially = localMessage.isSet(Flag.X_DOWNLOADED_PARTIAL);
        boolean messageIncomplete = !downloadedCompletely && !downloadedPartially;
        if (messageIncomplete) {
            startDownloadingMessageBody(false);
            return;
        }

        if (onlyLoadMetadata) { //meta 元
            //从localMessage中取出messageViewInfo
            MessageViewInfo messageViewInfo = MessageViewInfo.createForMetadataOnly(localMessage, !downloadedCompletely);
            onDecodeMessageFinished(messageViewInfo);
            return;
        }

        String openPgpProvider = account.getOpenPgpProvider();
        if (openPgpProvider != null) {
            startOrResumeCryptoOperation(openPgpProvider);
            return;
        }

        startOrResumeDecodeMessage();
    }

    private void onLoadMessageFromDatabaseFailed() {
        if (callback == null) {
            throw new IllegalStateException("unexpected call when callback is already detached");
        }
        callback.onMessageDataLoadFailed();
    }

    private void cancelAndClearLocalMessageLoader() {
        loaderManager.destroyLoader(LOCAL_MESSAGE_LOADER_ID);
    }

    private LoaderCallbacks<LocalMessage> localMessageLoaderCallback = new LoaderCallbacks<LocalMessage>() {
        @Override
        public Loader<LocalMessage> onCreateLoader(int id, Bundle args) {
            if (id != LOCAL_MESSAGE_LOADER_ID) {
                throw new IllegalStateException("loader id must be message loader id");
            }

            MessagingController messagingController = MessagingController.getInstance(context);
            return new LocalMessageLoader(context, messagingController, account, messageReference, onlyLoadMetadata);
        }

        @Override
        public void onLoadFinished(Loader<LocalMessage> loader, LocalMessage message) {
            if (loader.getId() != LOCAL_MESSAGE_LOADER_ID) {
                throw new IllegalStateException("loader id must be message loader id");
            }

            localMessage = message; //包含messageReference但不包含具体text  包含preview
            if (message == null) {
                onLoadMessageFromDatabaseFailed();
            } else {
                onLoadMessageFromDatabaseFinished();
            }
        }

        @Override
        public void onLoaderReset(Loader<LocalMessage> loader) {
            if (loader.getId() != LOCAL_MESSAGE_LOADER_ID) {
                throw new IllegalStateException("loader id must be message loader id");
            }
            // Do nothing
        }
    };


    // process with crypto helper
    //密码处理助手

    private void startOrResumeCryptoOperation(String openPgpProvider) {
        RetainFragment<MessageCryptoHelper> retainCryptoHelperFragment = getMessageCryptoHelperRetainFragment(true);
        if (retainCryptoHelperFragment.hasData()) {
            messageCryptoHelper = retainCryptoHelperFragment.getData();
        }
        if (messageCryptoHelper == null || !messageCryptoHelper.isConfiguredForOpenPgpProvider(openPgpProvider)) {
            messageCryptoHelper = new MessageCryptoHelper(
                    context, new OpenPgpApiFactory(), AutocryptOperations.getInstance(), openPgpProvider);
            retainCryptoHelperFragment.setData(messageCryptoHelper);
        }
        messageCryptoHelper.asyncStartOrResumeProcessingMessage(
                localMessage, messageCryptoCallback, cachedDecryptionResult, !account.isOpenPgpHideSignOnly());
    }

    private void cancelAndClearCryptoOperation() {
        RetainFragment<MessageCryptoHelper> retainCryptoHelperFragment = getMessageCryptoHelperRetainFragment(false);
        if (retainCryptoHelperFragment != null) {
            if (retainCryptoHelperFragment.hasData()) {
                messageCryptoHelper = retainCryptoHelperFragment.getData();
                messageCryptoHelper.cancelIfRunning();
                messageCryptoHelper = null;
            }
            retainCryptoHelperFragment.clearAndRemove(fragmentManager);
        }
    }

    private RetainFragment<MessageCryptoHelper> getMessageCryptoHelperRetainFragment(boolean createIfNotExists) {
        if (createIfNotExists) {
            return RetainFragment.findOrCreate(fragmentManager, "crypto_helper_" + messageReference.hashCode());
        } else {
            return RetainFragment.findOrNull(fragmentManager, "crypto_helper_" + messageReference.hashCode());
        }
    }

    private MessageCryptoCallback messageCryptoCallback = new MessageCryptoCallback() {
        @Override
        public void onCryptoHelperProgress(int current, int max) {
            if (callback == null) {
                throw new IllegalStateException("unexpected call when callback is already detached");
            }

            callback.setLoadingProgress(current, max);
        }

        @Override
        public void onCryptoOperationsFinished(MessageCryptoAnnotations annotations) {
            if (callback == null) {
                throw new IllegalStateException("unexpected call when callback is already detached");
            }

            messageCryptoAnnotations = annotations;
            startOrResumeDecodeMessage();
        }

        @Override
        public void startPendingIntentForCryptoHelper(IntentSender si, int requestCode, Intent fillIntent,
                int flagsMask, int flagValues, int extraFlags) {
            if (callback == null) {
                throw new IllegalStateException("unexpected call when callback is already detached");
            }

            callback.startIntentSenderForMessageLoaderHelper(si, requestCode, fillIntent,
                    flagsMask, flagValues, extraFlags);
        }
    };


    // decode message
    //解码信息

    private void startOrResumeDecodeMessage() {
        LocalMessageExtractorLoader loader =
                (LocalMessageExtractorLoader) loaderManager.<MessageViewInfo>getLoader(DECODE_MESSAGE_LOADER_ID);
        boolean isLoaderStale = (loader == null) || !loader.isCreatedFor(localMessage, messageCryptoAnnotations);

        if (isLoaderStale) {
            Timber.d("Creating new decode message loader");
            loaderManager.restartLoader(DECODE_MESSAGE_LOADER_ID, null, decodeMessageLoaderCallback);
        } else {
            Timber.d("Reusing decode message loader");
            loaderManager.initLoader(DECODE_MESSAGE_LOADER_ID, null, decodeMessageLoaderCallback);
        }
    }

    //此时已经获得了 messageViewInfo
    private void onDecodeMessageFinished(MessageViewInfo messageViewInfo) {
        if (callback == null) {
            throw new IllegalStateException("unexpected call when callback is already detached");
        }

        if (messageViewInfo == null) {
            messageViewInfo = createErrorStateMessageViewInfo();
            callback.onMessageViewInfoLoadFailed(messageViewInfo);
            return;
        }

        if (messageViewInfo.isSubjectEncrypted && !localMessage.hasCachedDecryptedSubject()) {
            try {
                localMessage.setCachedDecryptedSubject(messageViewInfo.subject);
            } catch (MessagingException e) {
                throw new AssertionError(e);
            }
        }

        callback.onMessageViewInfoLoadFinished(messageViewInfo);
    }

    @NonNull
    private MessageViewInfo createErrorStateMessageViewInfo() {
        boolean isMessageIncomplete = !localMessage.isSet(Flag.X_DOWNLOADED_FULL);
        return MessageViewInfo.createWithErrorState(localMessage, isMessageIncomplete);
    }

    private void cancelAndClearDecodeLoader() {
        loaderManager.destroyLoader(DECODE_MESSAGE_LOADER_ID);
    }

    private LoaderCallbacks<MessageViewInfo> decodeMessageLoaderCallback = new LoaderCallbacks<MessageViewInfo>() {
        @Override
        public Loader<MessageViewInfo> onCreateLoader(int id, Bundle args) {
            if (id != DECODE_MESSAGE_LOADER_ID) {
                throw new IllegalStateException("loader id must be message decoder id");
            }
            return new LocalMessageExtractorLoader(context, localMessage, messageCryptoAnnotations,
                    messageViewInfoExtractor);//localMessage包含localStore包含database
        }

        @Override
        public void onLoadFinished(Loader<MessageViewInfo> loader, MessageViewInfo messageViewInfo) {
            if (loader.getId() != DECODE_MESSAGE_LOADER_ID) {
                throw new IllegalStateException("loader id must be message decoder id");
            }
            onDecodeMessageFinished(messageViewInfo);
        }

        @Override
        public void onLoaderReset(Loader<MessageViewInfo> loader) {
            if (loader.getId() != DECODE_MESSAGE_LOADER_ID) {
                throw new IllegalStateException("loader id must be message decoder id");
            }
            // Do nothing
        }
    };


    // download missing body

    private void startDownloadingMessageBody(boolean downloadComplete) {
        if (downloadComplete) {
            MessagingController.getInstance(context).loadMessageRemote(
                    account, messageReference.getFolderServerId(), messageReference.getUid(), downloadMessageListener);
        } else {
            MessagingController.getInstance(context).loadMessageRemotePartial(
                    account, messageReference.getFolderServerId(), messageReference.getUid(), downloadMessageListener);
        }
    }

    private void onMessageDownloadFinished() {
        if (callback == null) {
            return;
        }

        cancelAndClearLocalMessageLoader();
        cancelAndClearDecodeLoader();
        cancelAndClearCryptoOperation();

        startOrResumeLocalMessageLoader();
    }

    private void onDownloadMessageFailed(final Throwable t) {
        if (callback == null) {
            return;
        }

        if (t instanceof IllegalArgumentException) {
            callback.onDownloadErrorMessageNotFound();
        } else {
            callback.onDownloadErrorNetworkError();
        }
    }

    MessagingListener downloadMessageListener = new SimpleMessagingListener() {
        @Override
        public void loadMessageRemoteFinished(final Account account, final String folderServerId, final String uid) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!messageReference.equals(account.getUuid(), folderServerId, uid)) {
                        return;
                    }
                    onMessageDownloadFinished();
                }
            });
        }

        @Override
        public void loadMessageRemoteFailed(Account account, String folderServerId, String uid, final Throwable t) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownloadMessageFailed(t);
                }
            });
        }
    };


    // callback interface   回调接口

    public interface MessageLoaderCallbacks {
        void onMessageDataLoadFinished(LocalMessage message);
        void onMessageDataLoadFailed();

        void onMessageViewInfoLoadFinished(MessageViewInfo messageViewInfo);
        void onMessageViewInfoLoadFailed(MessageViewInfo messageViewInfo);

        void setLoadingProgress(int current, int max);

        void startIntentSenderForMessageLoaderHelper(IntentSender si, int requestCode, Intent fillIntent, int flagsMask,
                                                     int flagValues, int extraFlags);

        void onDownloadErrorMessageNotFound();
        void onDownloadErrorNetworkError();
    }

}
