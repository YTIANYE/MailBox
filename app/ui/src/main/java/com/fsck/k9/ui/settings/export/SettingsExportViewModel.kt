package com.fsck.k9.ui.settings.export

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsck.k9.Preferences
import com.fsck.k9.helper.SingleLiveEvent
import com.fsck.k9.helper.measureRealtimeMillis
import com.fsck.k9.preferences.SettingsExporter
import com.fsck.k9.ui.helper.CoroutineScopeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private typealias AccountUuid = String
private typealias AccountNumber = Int

class SettingsExportViewModel(val context: Context, val preferences: Preferences) : CoroutineScopeViewModel() {
    private val uiModelLiveData = MutableLiveData<SettingsExportUiModel>()
    private val actionLiveData = SingleLiveEvent<Action>()

    private val uiModel = SettingsExportUiModel()
    private var accountsMap: Map<AccountNumber, AccountUuid> = emptyMap()
    private var savedSelection: SavedListItemSelection? = null
    private var contentUri: Uri? = null

    private val includeGeneralSettings: Boolean
        get() {
            return savedSelection?.includeGeneralSettings
                    ?: uiModel.settingsList.first { it is SettingsListItem.GeneralSettings }.selected
        }

    private val selectedAccounts: Set<AccountUuid>
        get() {
            return savedSelection?.selectedAccountUuids
                    ?: uiModel.settingsList.asSequence()
                            .filterIsInstance<SettingsListItem.Account>()
                            .filter { it.selected }
                            .map {
                                accountsMap[it.accountNumber] ?: error("Unknown account number: ${it.accountNumber}")
                            }
                            .toSet()
        }

    fun getActionEvents(): LiveData<Action> = actionLiveData

    fun getUiModel(): LiveData<SettingsExportUiModel> {
        if (uiModelLiveData.value == null) {
            uiModelLiveData.value = uiModel

            launch {
                val accounts = withContext(Dispatchers.IO) { preferences.accounts }

                accountsMap = accounts.map { it.accountNumber to it.uuid }.toMap()

                val listItems = savedSelection.let { savedState ->
                    val generalSettings = SettingsListItem.GeneralSettings.apply {
                        selected = savedState == null || savedState.includeGeneralSettings
                    }

                    val accountListItems = accounts.map { account ->
                        SettingsListItem.Account(account.accountNumber, account.displayName, account.email).apply {
                            selected = savedState == null || account.uuid in savedState.selectedAccountUuids
                        }
                    }

                    listOf(generalSettings) + accountListItems
                }

                updateUiModel {
                    initializeSettingsList(listItems)
                }
            }
        }

        return uiModelLiveData
    }

    fun initializeFromSavedState(savedInstanceState: Bundle) {
        savedSelection = SavedListItemSelection(
                includeGeneralSettings = savedInstanceState.getBoolean(STATE_INCLUDE_GENERAL_SETTINGS),
                selectedAccountUuids = savedInstanceState.getStringArray(STATE_SELECTED_ACCOUNTS)?.toSet() ?: emptySet()
        )

        uiModel.apply {
            isSettingsListEnabled = savedInstanceState.getBoolean(STATE_SETTINGS_LIST_ENABLED)
            exportButton = ButtonState.valueOf(
                    savedInstanceState.getString(STATE_EXPORT_BUTTON, ButtonState.DISABLED.name)
            )
            isShareButtonVisible = savedInstanceState.getBoolean(STATE_SHARE_BUTTON_VISIBLE)
            isProgressVisible = savedInstanceState.getBoolean(STATE_PROGRESS_VISIBLE)
            statusText = StatusText.valueOf(savedInstanceState.getString(STATE_STATUS_TEXT, StatusText.HIDDEN.name))
        }

        contentUri = savedInstanceState.getParcelable(STATE_CONTENT_URI)
    }

    fun onExportButtonClicked() {
        updateUiModel {
            disableExportButton()
        }

        startExportSettings()
    }

    fun onShareButtonClicked() {
        sendActionEvent(Action.ShareDocument(contentUri!!, SETTINGS_MIME_TYPE))
    }

    private fun startExportSettings() {
        val exportFileName = SettingsExporter.generateDatedExportFileName()
        sendActionEvent(Action.PickDocument(exportFileName, SETTINGS_MIME_TYPE))
    }

    fun onDocumentPicked(contentUri: Uri) {
        this.contentUri = contentUri

        updateUiModel {
            showProgress()
        }

        val includeGeneralSettings = this.includeGeneralSettings
        val selectedAccounts = this.selectedAccounts

        launch {
            try {
                val elapsed = measureRealtimeMillis {
                    withContext(Dispatchers.IO) {
                        SettingsExporter.exportToUri(context, includeGeneralSettings, selectedAccounts, contentUri)
                    }
                }

                if (elapsed < MIN_PROGRESS_DURATION) {
                    delay(MIN_PROGRESS_DURATION - elapsed)
                }

                updateUiModel {
                    showSuccessText()
                }
            } catch (e: Exception) {
                updateUiModel {
                    showFailureText()
                }
            }
        }
    }

    fun onDocumentPickCanceled() {
        updateUiModel {
            enableExportButton()
        }
    }

    fun saveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_SETTINGS_LIST_ENABLED, uiModel.isSettingsListEnabled)
        outState.putString(STATE_EXPORT_BUTTON, uiModel.exportButton.name)
        outState.putBoolean(STATE_SHARE_BUTTON_VISIBLE, uiModel.isShareButtonVisible)
        outState.putBoolean(STATE_PROGRESS_VISIBLE, uiModel.isProgressVisible)
        outState.putString(STATE_STATUS_TEXT, uiModel.statusText.name)

        outState.putBoolean(STATE_INCLUDE_GENERAL_SETTINGS, includeGeneralSettings)
        outState.putStringArray(STATE_SELECTED_ACCOUNTS, selectedAccounts.toTypedArray())

        outState.putParcelable(STATE_CONTENT_URI, contentUri)
    }

    fun onSettingsListItemSelected(position: Int, isSelected: Boolean) {
        savedSelection = null

        updateUiModel {
            setSettingsListItemSelection(position, isSelected)
        }
    }

    private fun updateUiModel(block: SettingsExportUiModel.() -> Unit) {
        uiModel.block()
        uiModelLiveData.value = uiModel
    }

    private fun sendActionEvent(action: Action) {
        actionLiveData.value = action
    }

    companion object {
        private const val MIN_PROGRESS_DURATION = 1000L
        private const val SETTINGS_MIME_TYPE = "application/octet-stream"

        private const val STATE_SETTINGS_LIST_ENABLED = "settingsListEnabled"
        private const val STATE_EXPORT_BUTTON = "exportButton"
        private const val STATE_SHARE_BUTTON_VISIBLE = "shareButtonVisible"
        private const val STATE_PROGRESS_VISIBLE = "progressVisible"
        private const val STATE_STATUS_TEXT = "statusText"
        private const val STATE_INCLUDE_GENERAL_SETTINGS = "includeGeneralSettings"
        private const val STATE_SELECTED_ACCOUNTS = "selectedAccounts"
        private const val STATE_CONTENT_URI = "contentUri"
    }
}

sealed class Action {
    class PickDocument(val fileNameSuggestion: String, val mimeType: String) : Action()
    class ShareDocument(val contentUri: Uri, val mimeType: String) : Action()
}

private data class SavedListItemSelection(
    val includeGeneralSettings: Boolean,
    val selectedAccountUuids: Set<AccountUuid>
)
