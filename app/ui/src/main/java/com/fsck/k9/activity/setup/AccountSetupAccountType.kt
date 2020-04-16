package com.fsck.k9.activity.setup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.fsck.k9.Account
import com.fsck.k9.Preferences
import com.fsck.k9.activity.K9Activity
import com.fsck.k9.helper.EmailHelper.getDomainFromEmailAddress
import com.fsck.k9.preferences.Protocols
import com.fsck.k9.setup.ServerNameSuggester
import com.fsck.k9.ui.R
import org.koin.android.ext.android.inject
import java.net.URI

/**
 * Prompts the user to select an account type. The account type, along with the
 * passed in email address, password and makeDefault are then passed on to the
 * AccountSetupIncoming activity.
 *提示用户选择帐户类型。帐户类型，以及
 *输入电子邮件地址、密码及makeDefault，然后传送至
 * AccountSetupIncoming活动。
 */
class AccountSetupAccountType : K9Activity() {
    private val preferences: Preferences by inject()
    private val serverNameSuggester: ServerNameSuggester by inject()

    private lateinit var account: Account
    private var makeDefault = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayout(R.layout.account_setup_account_type)

        decodeArguments()
        Log.d("debugLog", "LOG_AccountSetupAccountType")
        Log.d("debugLog", "LOG_AccountSetupAccountType")
        setupPop3Account()  //去掉按钮触发   直接设置为POP3
        //findViewById<View>(R.id.pop).setOnClickListener { setupPop3Account() }
        //findViewById<View>(R.id.imap).setOnClickListener { setupImapAccount() }
    }

    private fun decodeArguments() {
        val accountUuid = intent.getStringExtra(EXTRA_ACCOUNT) ?: error("No account UUID provided")
        account = preferences.getAccount(accountUuid) ?: error("No account with given UUID found")
        makeDefault = intent.getBooleanExtra(EXTRA_MAKE_DEFAULT, false)
    }

    private fun setupPop3Account() {
        setupAccount(Protocols.POP3, "pop3+ssl+")
    }

/*    private fun setupImapAccount() {
        setupAccount(Protocols.IMAP, "imap+ssl+")
    }*/

    private fun setupAccount(serverType: String, schemePrefix: String) {    //设置类型 imap或者pop3
        setupStoreAndSmtpTransport(serverType, schemePrefix)
        returnAccountTypeSelectionResult()
    }

    private fun setupStoreAndSmtpTransport(serverType: String, schemePrefix: String) {
        val domainPart = getDomainFromEmailAddress(account.email) ?: error("Couldn't get domain from email address")

        setupStoreUri(serverType, domainPart, schemePrefix)
        setupTransportUri(domainPart)
    }

    private fun setupStoreUri(serverType: String, domainPart: String, schemePrefix: String) {
        val suggestedStoreServerName = serverNameSuggester.suggestServerName(serverType, domainPart)
        val storeUriForDecode = URI(account.storeUri)
        val storeUri = URI(
            schemePrefix, storeUriForDecode.userInfo, suggestedStoreServerName,
            storeUriForDecode.port, null, null, null
        )
        account.storeUri = storeUri.toString()
    }

    private fun setupTransportUri(domainPart: String) {
        val suggestedTransportServerName = serverNameSuggester.suggestServerName(Protocols.SMTP, domainPart)
        val transportUriForDecode = URI(account.transportUri)
        val transportUri = URI(
            "smtp+tls+", transportUriForDecode.userInfo, suggestedTransportServerName,
            transportUriForDecode.port, null, null, null
        )
        account.transportUri = transportUri.toString()
    }

    //收件服务器设置 活动的入口
    private fun returnAccountTypeSelectionResult() {
        AccountSetupIncoming.actionIncomingSettings(this, account, makeDefault)     //收件服务器设置 活动的入口
        finish()
    }

    companion object {
        private const val EXTRA_ACCOUNT = "account"
        private const val EXTRA_MAKE_DEFAULT = "makeDefault"

        //本活动入口
        @JvmStatic
        fun actionSelectAccountType(context: Context, account: Account, makeDefault: Boolean) {

            val intent = Intent(context, AccountSetupAccountType::class.java).apply {
                putExtra(EXTRA_ACCOUNT, account.uuid)
                putExtra(EXTRA_MAKE_DEFAULT, makeDefault)
            }
            context.startActivity(intent)
        }
    }
}
