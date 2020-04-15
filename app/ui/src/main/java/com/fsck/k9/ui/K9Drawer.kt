package com.fsck.k9.ui

import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.fsck.k9.Account
import com.fsck.k9.K9
import com.fsck.k9.Preferences
import com.fsck.k9.activity.MessageList
import com.fsck.k9.helper.Contacts
import com.fsck.k9.mailstore.DisplayFolder
import com.fsck.k9.mailstore.Folder
import com.fsck.k9.ui.folders.FolderIconProvider
import com.fsck.k9.ui.folders.FolderNameFormatter
import com.fsck.k9.ui.folders.FoldersLiveData
import com.fsck.k9.ui.messagelist.MessageListViewModel
import com.fsck.k9.ui.messagelist.MessageListViewModelFactory
import com.fsck.k9.ui.settings.SettingsActivity
import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.IconicsSize
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.iconics.utils.colorRes
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.Drawer.OnDrawerItemClickListener
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import java.util.ArrayList
import java.util.HashSet
import org.koin.core.KoinComponent
import org.koin.core.inject

class K9Drawer(private val parent: MessageList, savedInstanceState: Bundle?) : KoinComponent {
    private val folderNameFormatter: FolderNameFormatter by inject()
    private val preferences: Preferences by inject()
    private val themeManager: ThemeManager by inject()
    private val resources: Resources by inject()

    private val drawer: Drawer
    private val accountHeader: AccountHeader
    private val folderIconProvider: FolderIconProvider = FolderIconProvider(parent.theme)

    private val userFolderDrawerIds = ArrayList<Long>()
    private var unifiedInboxSelected: Boolean = false
    private var accentColor: Int = 0
    private var selectedColor: Int = 0
    private var openedFolderServerId: String? = null

    private var foldersLiveData: FoldersLiveData? = null
    private val foldersObserver = Observer<List<DisplayFolder>> { folders ->
        setUserFolders(folders)
    }

    val layout: DrawerLayout
        get() = drawer.drawerLayout

    val isOpen: Boolean
        get() = drawer.isDrawerOpen

    init {
        accountHeader = buildAccountHeader()//创建账户头

        drawer = DrawerBuilder()
                .withActivity(parent)
                .withOnDrawerItemClickListener(createItemClickListener())
                .withOnDrawerListener(parent.createOnDrawerListener())
                .withSavedInstance(savedInstanceState)
                .withAccountHeader(accountHeader)
                .build()

        addFooterItems()
    }


    /**
     * 创建账户头
     * 位于抽屉顶部
     *
     * */
    private fun buildAccountHeader(): AccountHeader {
        val headerBuilder = AccountHeaderBuilder()
                .withActivity(parent)
                .withHeaderBackground(R.drawable.drawer_header_background)//抽屉 上部背景图

        if (!K9.isHideSpecialAccounts) {
            headerBuilder.addProfiles(ProfileDrawerItem()
                    .withNameShown(true)
                    .withName(R.string.integrated_inbox_title)//全局收件箱
                    .withEmail(parent.getString(R.string.integrated_inbox_detail))//全局收件箱中的所有邮件
                    .withIcon(IconicsDrawable(parent, FontAwesome.Icon.faw_users)
                            .colorRes(R.color.material_drawer_background)
                            .backgroundColor(IconicsColor.colorInt(Color.GRAY))
                            .size(IconicsSize.dp(56))
                            .padding(IconicsSize.dp(8)))
                    .withSelected(unifiedInboxSelected)
                    .withIdentifier(DRAWER_ID_UNIFIED_INBOX)
            )
        }

        val photoUris = HashSet<Uri>()

        for (account in preferences.accounts) {
            val drawerId = (account.accountNumber + 1 shl DRAWER_ACCOUNT_SHIFT).toLong()

            val pdi = ProfileDrawerItem()
                    .withNameShown(true)
                    .withName(account.description)
                    .withEmail(account.email)
                    .withIdentifier(drawerId)
                    .withSelected(false)
                    .withTag(account)

            val photoUri = Contacts.getInstance(parent).getPhotoUri(account.email)
            if (photoUri != null && !photoUris.contains(photoUri)) {
                photoUris.add(photoUri)
                pdi.withIcon(photoUri)
            } else {
                pdi.withIcon(IconicsDrawable(parent, FontAwesome.Icon.faw_user_alt)
                        .colorRes(R.color.material_drawer_background)
                        .backgroundColor(IconicsColor.colorInt(account.chipColor))
                        .size(IconicsSize.dp(56))
                        .padding(IconicsSize.dp(14))
                )
            }
            headerBuilder.addProfiles(pdi)
        }

        return headerBuilder
                .withOnAccountHeaderListener(object : AccountHeader.OnAccountHeaderListener {
                    override fun onProfileChanged(view: View?, profile: IProfile<*>, current: Boolean): Boolean {
                        if (profile.identifier == DRAWER_ID_UNIFIED_INBOX) {//全局收件箱
                            parent.openUnifiedInbox()
                            return false
                        } else {    //其他收件箱
                            val account = (profile as ProfileDrawerItem).tag as Account
                            parent.openRealAccount(account)
                            updateUserAccountsAndFolders(account)
                            return false
                        }
                    }
                })
                .build()
    }

    /**
     * 抽屉 底部选项
     * 添加管理文件夹和设置选项
     *
     * */

    private fun addFooterItems() {
        /*去掉管理文件夹 */

/*        if (!unifiedInboxSelected) {
            drawer.addStickyFooterItem(
                PrimaryDrawerItem()
                    .withName(R.string.folders_action)//管理文件夹
                    .withIcon(folderIconProvider.iconFolderResId)
                    .withIdentifier(DRAWER_ID_FOLDERS)
                    .withSelectable(false)
            )
        }*/

        drawer.addStickyFooterItem(PrimaryDrawerItem()
            .withName(R.string.preferences_action)//设置  抽屉底部
            .withIcon(getResId(R.attr.iconActionSettings))
            .withIdentifier(DRAWER_ID_PREFERENCES)
            .withSelectable(false)
        )
    }

    private fun getResId(resAttribute: Int): Int {
        val typedValue = TypedValue()
        val found = parent.theme.resolveAttribute(resAttribute, typedValue, true)
        if (!found) {
            throw AssertionError("Couldn't find resource with attribute $resAttribute")
        }
        return typedValue.resourceId
    }

    private fun getFolderDisplayName(folder: Folder): String {
        return folderNameFormatter.displayName(folder)
    }

    /**
     * 更新账户和文件夹
     * */
    fun updateUserAccountsAndFolders(account: Account?) {
        if (account == null) {
            selectUnifiedInbox()
        } else {
            unifiedInboxSelected = false
            getDrawerColorsForAccount(account).let { drawerColors ->
                accentColor = drawerColors.accentColor
                selectedColor = drawerColors.selectedColor
            }

            accountHeader.setActiveProfile((account.accountNumber + 1 shl DRAWER_ACCOUNT_SHIFT).toLong())
            accountHeader.headerBackgroundView.setColorFilter(account.chipColor, PorterDuff.Mode.MULTIPLY)
            val viewModelProvider = ViewModelProviders.of(parent, MessageListViewModelFactory())
            val viewModel = viewModelProvider.get(MessageListViewModel::class.java)

            removeFoldersObserver()
            foldersLiveData = viewModel.getFolders(account).apply {
                observe(parent, foldersObserver)
            }

            updateFooterItems()     //更新底部
        }
    }

    private fun removeFoldersObserver() {
        foldersLiveData?.removeObserver(foldersObserver)
        foldersLiveData = null
    }


    /**
     * 抽屉 更新底部
     * */
    private fun updateFooterItems() {
        drawer.removeAllStickyFooterItems()
        addFooterItems()    //添加管理文件夹和 设置
    }

    private fun createItemClickListener(): OnDrawerItemClickListener {
        return object : OnDrawerItemClickListener {
            override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                when (drawerItem.identifier) {
                    DRAWER_ID_PREFERENCES -> SettingsActivity.launch(parent)//进入设置活动
                    DRAWER_ID_FOLDERS -> parent.launchManageFoldersScreen()//进入管理文件夹活动
                    else -> {
                        val folder = drawerItem.tag as Folder
                        parent.openFolder(folder.serverId)
                    }
                }
                return false
            }
        }
    }


    /**
     * 设置用户的文件夹
     */
    private fun setUserFolders(folders: List<DisplayFolder>?) {
        clearUserFolders()

        if (folders == null) {
            return
        }

        var openedFolderDrawerId: Long = -1
        for (displayFolder in folders) {
            val folder = displayFolder.folder
            val drawerId = folder.id shl DRAWER_FOLDER_SHIFT

            val drawerItem = PrimaryDrawerItem()
                    .withIcon(folderIconProvider.getFolderIcon(folder.type))
                    .withIdentifier(drawerId)
                    .withTag(folder)
                    .withSelectedColor(selectedColor)
                    .withSelectedTextColor(accentColor)
                    .withName(getFolderDisplayName(folder))

            val unreadCount = displayFolder.unreadCount
            if (unreadCount > 0) {
                drawerItem.withBadge(unreadCount.toString())
            }

            drawer.addItem(drawerItem)

            userFolderDrawerIds.add(drawerId)

            if (folder.serverId == openedFolderServerId) {
                openedFolderDrawerId = drawerId
            }
        }

        if (openedFolderDrawerId != -1L) {
            drawer.setSelection(openedFolderDrawerId, false)
        }
    }

    private fun clearUserFolders() {
        drawer.removeAllItems()
        userFolderDrawerIds.clear()
    }

    fun selectFolder(folderServerId: String) {
        unifiedInboxSelected = false
        openedFolderServerId = folderServerId
        for (drawerId in userFolderDrawerIds) {     //设置文件夹
            val folder = drawer.getDrawerItem(drawerId)!!.tag as Folder
            if (folder.serverId == folderServerId) {
                drawer.setSelection(drawerId, false)
                return
            }
        }
        updateFooterItems()//更新抽屉底部选项
    }

    fun deselect() {
        unifiedInboxSelected = false
        openedFolderServerId = null
        drawer.deselect()
    }


    /**
     * 文件夹设置
     * */
    fun selectUnifiedInbox() {
        unifiedInboxSelected = true
        openedFolderServerId = null
        accentColor = 0 // Unified inbox does not have folders, so color does not matter
        selectedColor = 0
        accountHeader.setActiveProfile(DRAWER_ID_UNIFIED_INBOX)//全局收件箱
        accountHeader.headerBackgroundView.setColorFilter(0xFFFFFFFFL.toInt(), PorterDuff.Mode.MULTIPLY)
        removeFoldersObserver()
        clearUserFolders()
        updateFooterItems()     //更新抽屉底部选项
    }

    private data class DrawerColors(
        val accentColor: Int,
        val selectedColor: Int
    )

    private fun getDrawerColorsForAccount(account: Account): DrawerColors {
        val baseColor = if (themeManager.appTheme == Theme.DARK) {
            getDarkThemeAccentColor(account.chipColor)
        } else {
            account.chipColor
        }
        return DrawerColors(
            accentColor = baseColor,
            selectedColor = baseColor.and(0xffffff).or(0x22000000)
        )
    }

    private fun getDarkThemeAccentColor(color: Int): Int {
        val lightColors = resources.getIntArray(R.array.account_colors)
        val darkColors = resources.getIntArray(R.array.drawer_account_accent_color_dark_theme)
        val index = lightColors.indexOf(color)
        return if (index == -1) color else darkColors[index]
    }

    fun open() {
        drawer.openDrawer()
    }

    fun close() {
        drawer.closeDrawer()
    }

    fun lock() {
        drawer.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun unlock() {
        drawer.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    companion object {
        // Bit shift for identifiers of user folders items, to leave space for other items
        private const val DRAWER_FOLDER_SHIFT: Int = 2
        private const val DRAWER_ACCOUNT_SHIFT: Int = 16

        private const val DRAWER_ID_UNIFIED_INBOX: Long = 0 //全局收件箱
        private const val DRAWER_ID_PREFERENCES: Long = 1   //设置
        private const val DRAWER_ID_FOLDERS: Long = 2   //管理文件夹
    }
}
