package com.fsck.k9.ui.settings.import

import com.fsck.k9.ui.settings.import.SettingsListItem.GeneralSettings

class SettingsImportUiModel {
    var settingsList: List<SettingsListItem> = emptyList()
    var isSettingsListVisible = false
    var isSettingsListEnabled = true
    var importButton: ButtonState = ButtonState.DISABLED
    var closeButton: ButtonState = ButtonState.GONE
    var closeButtonLabel: CloseButtonLabel = CloseButtonLabel.OK
    var isPickDocumentButtonVisible = true
    var isPickDocumentButtonEnabled = true
    var isLoadingProgressVisible = false
    var isImportProgressVisible = false
    var statusText = StatusText.HIDDEN

    val hasImportStarted
        get() = importButton == ButtonState.GONE

    val hasDocumentBeenRead
        get() = isSettingsListVisible

    val wasAccountImportSuccessful
        get() = hasImportStarted && settingsList.any { it !is GeneralSettings && it.importStatus.isSuccess }

    fun enablePickDocumentButton() {
        isPickDocumentButtonEnabled = true
    }

    fun disablePickDocumentButton() {
        statusText = StatusText.HIDDEN
        isPickDocumentButtonEnabled = false
    }

    private fun enableImportButton() {
        importButton = ButtonState.ENABLED
        isImportProgressVisible = false
        isSettingsListEnabled = true
    }

    private fun disableImportButton() {
        importButton = ButtonState.DISABLED
        isImportProgressVisible = false
    }

    fun showLoadingProgress() {
        isLoadingProgressVisible = true
        isPickDocumentButtonVisible = false
        isSettingsListEnabled = false
        statusText = StatusText.HIDDEN
    }

    fun showImportingProgress() {
        isImportProgressVisible = true
        isSettingsListEnabled = false
        importButton = ButtonState.INVISIBLE
        statusText = StatusText.IMPORTING_PROGRESS
    }

    private fun showSuccessText() {
        importButton = ButtonState.GONE
        closeButton = ButtonState.ENABLED
        closeButtonLabel = CloseButtonLabel.OK
        isImportProgressVisible = false
        isSettingsListEnabled = true
        statusText = StatusText.IMPORT_SUCCESS
    }

    private fun showPasswordRequiredText() {
        importButton = ButtonState.GONE
        closeButton = ButtonState.ENABLED
        closeButtonLabel = CloseButtonLabel.LATER
        isImportProgressVisible = false
        isSettingsListEnabled = true
        statusText = StatusText.IMPORT_SUCCESS_PASSWORD_REQUIRED
    }

    fun showReadFailureText() {
        isLoadingProgressVisible = false
        isPickDocumentButtonVisible = true
        isPickDocumentButtonEnabled = true
        statusText = StatusText.IMPORT_READ_FAILURE
        importButton = ButtonState.DISABLED
    }

    fun showImportErrorText() {
        isLoadingProgressVisible = false
        isImportProgressVisible = false
        isSettingsListVisible = false
        isPickDocumentButtonVisible = true
        isPickDocumentButtonEnabled = true
        statusText = StatusText.IMPORT_FAILURE
        importButton = ButtonState.DISABLED
    }

    private fun showPartialImportErrorText() {
        importButton = ButtonState.GONE
        closeButton = ButtonState.ENABLED
        closeButtonLabel = CloseButtonLabel.OK
        isImportProgressVisible = false
        isSettingsListEnabled = true
        statusText = StatusText.IMPORT_PARTIAL_FAILURE
    }

    fun initializeSettingsList(list: List<SettingsListItem>) {
        settingsList = list
        isSettingsListVisible = true
        isLoadingProgressVisible = false
        isPickDocumentButtonVisible = false
        updateImportButtonFromSelection()
    }

    fun toggleSettingsListItemSelection(position: Int) {
        val settingsListItem = settingsList[position]
        settingsListItem.selected = !settingsListItem.selected
        statusText = StatusText.HIDDEN
        updateImportButtonFromSelection()
    }

    fun setSettingsListState(position: Int, status: ImportStatus) {
        settingsList[position].importStatus = status
        settingsList[position].enabled = status == ImportStatus.IMPORT_SUCCESS_PASSWORD_REQUIRED
    }

    private fun updateImportButtonFromSelection() {
        if (isImportProgressVisible) return

        val atLeastOnceSelected = settingsList.any { it.selected }
        if (atLeastOnceSelected) {
            enableImportButton()
        } else {
            disableImportButton()
        }
    }

    fun updateCloseButtonAndImportStatusText() {
        val errorsOnly = settingsList.none { it.importStatus.isSuccess }
        if (errorsOnly) {
            showImportErrorText()
            return
        }

        val passwordsMissing = settingsList.any { it.importStatus == ImportStatus.IMPORT_SUCCESS_PASSWORD_REQUIRED }
        if (passwordsMissing) {
            showPasswordRequiredText()
            return
        }

        val partialImportError = settingsList.any { it.importStatus == ImportStatus.IMPORT_FAILURE }
        if (partialImportError) {
            showPartialImportErrorText()
        } else {
            showSuccessText()
        }
    }
}

sealed class SettingsListItem {
    var selected: Boolean = true
    var enabled: Boolean = true
    var importStatus: ImportStatus = ImportStatus.NOT_AVAILABLE

    class GeneralSettings : SettingsListItem()
    class Account(val accountIndex: Int, var displayName: String) : SettingsListItem()
}

enum class ImportStatus {
    NOT_AVAILABLE,
    NOT_SELECTED,
    IMPORT_SUCCESS,
    IMPORT_SUCCESS_PASSWORD_REQUIRED,
    IMPORT_FAILURE;

    val isSuccess: Boolean
        get() = this == IMPORT_SUCCESS || this == IMPORT_SUCCESS_PASSWORD_REQUIRED
}

enum class ButtonState {
    DISABLED,
    ENABLED,
    INVISIBLE,
    GONE
}

enum class StatusText {
    HIDDEN,
    IMPORTING_PROGRESS,
    IMPORT_SUCCESS,
    IMPORT_SUCCESS_PASSWORD_REQUIRED,
    IMPORT_READ_FAILURE,
    IMPORT_PARTIAL_FAILURE,
    IMPORT_FAILURE
}

enum class CloseButtonLabel {
    OK,
    LATER
}
