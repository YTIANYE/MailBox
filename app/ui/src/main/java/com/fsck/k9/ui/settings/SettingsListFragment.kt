package com.fsck.k9.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fsck.k9.Account
import com.fsck.k9.ui.R
import com.fsck.k9.ui.observeNotNull
import com.fsck.k9.ui.settings.account.AccountSettingsActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_settings_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel


    /**
     * 打开设置的fragment
     * 抽屉底部
     * */
class SettingsListFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var settingsAdapter: GroupAdapter<ViewHolder>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeSettingsList()
        populateSettingsList()
    }

    private fun initializeSettingsList() {
        settingsAdapter = GroupAdapter()
        settingsAdapter.setOnItemClickListener { item, _ ->
            handleItemClick(item)
        }

        with(settings_list) {
            adapter = settingsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun populateSettingsList() {
        viewModel.accounts.observeNotNull(this) { accounts ->
            if (accounts.isEmpty()) {
                launchOnboarding()
            } else {
                populateSettingsList(accounts)
            }
        }
    }


    private fun populateSettingsList(accounts: List<Account>) {
        settingsAdapter.clear()


        /**
         * 常规设置模块
         */
/*        val generalSection = Section().apply {


            val generalSettingsActionItem = SettingsActionItem(
                    getString(R.string.general_settings_title),
                    R.id.action_settingsListScreen_to_generalSettingsScreen,
                    R.attr.iconSettingsGeneral
            )
            add(generalSettingsActionItem)
        }
        settingsAdapter.add(generalSection)*/


        /**
         * 设置中的 账户模块
         *
         */
        val accountSection = Section().apply {
            for (account in accounts) {
                add(AccountItem(account))
            }

            val addAccountActionItem = SettingsActionItem(
                    getString(R.string.add_account_action),//添加账户
                    R.id.action_settingsListScreen_to_addAccountScreen,
                    R.attr.iconSettingsAccountAdd
            )
            add(addAccountActionItem)
        }
        //账户
        accountSection.setHeader(SettingsDividerItem(getString(R.string.accounts_title)))
        settingsAdapter.add(accountSection)

        /**
         * 设置中的 备份模块
         * */
/*        val backupSection = Section().apply {
            val exportSettingsActionItem = SettingsActionItem(
                    getString(R.string.settings_export_title),
                    R.id.action_settingsListScreen_to_settingsExportScreen,
                    R.attr.iconSettingsExport
            )
            add(exportSettingsActionItem)

            val importSettingsActionItem = SettingsActionItem(
                    getString(R.string.settings_import_title),
                    R.id.action_settingsListScreen_to_settingsImportScreen,
                    R.attr.iconSettingsImport
            )
            add(importSettingsActionItem)
        }
        *//*备份*//*
        backupSection.setHeader(SettingsDividerItem(getString(R.string.settings_list_backup_category)))
        //设置备份模块
        settingsAdapter.add(backupSection)*/

        /**
         * 设置中的关于模块
         */

/*        val miscSection = Section().apply {
            val accountActionItem = SettingsActionItem(
                getString(R.string.about_action),   //关于
                R.id.action_settingsListScreen_to_aboutScreen,
                R.attr.iconSettingsAbout
            )
            add(accountActionItem)
        }
        //杂项
        miscSection.setHeader(SettingsDividerItem(getString(R.string.settings_list_miscellaneous_category)))
        settingsAdapter.add(miscSection)*/

    }

    private fun handleItemClick(item: Item<*>) {
        when (item) {
            is AccountItem -> launchAccountSettings(item.account)
            is SettingsActionItem -> findNavController().navigate(item.navigationAction)
        }
    }

    private fun launchAccountSettings(account: Account) {
        AccountSettingsActivity.start(requireActivity(), account.uuid)  //账户设置活动
    }

    private fun launchOnboarding() {
        findNavController().navigate(R.id.action_settingsListScreen_to_onboardingScreen)    //添加新的账户        设置中
        requireActivity().finish()
    }
}
