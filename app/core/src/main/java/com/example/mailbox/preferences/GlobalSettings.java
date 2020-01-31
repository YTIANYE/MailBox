package com.example.mailbox.preferences;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;

import com.example.mailbox.Account;
import com.example.mailbox.Account.SortType;
import com.example.mailbox.DI;
import com.example.mailbox.FontSizes;
import com.example.mailbox.K9;
import com.example.mailbox.K9.NotificationHideSubject;
import com.example.mailbox.K9.NotificationQuickDelete;
import com.example.mailbox.K9.SplitViewMode;
import com.example.mailbox.K9.AppTheme;
import com.example.mailbox.K9.SubTheme;
import com.example.mailbox.core.R;
import com.example.mailbox.preferences.Settings.BooleanSetting;
import com.example.mailbox.preferences.Settings.ColorSetting;
import com.example.mailbox.preferences.Settings.EnumSetting;
import com.example.mailbox.preferences.Settings.FontSizeSetting;
import com.example.mailbox.preferences.Settings.IntegerRangeSetting;
import com.example.mailbox.preferences.Settings.InvalidSettingValueException;
import com.example.mailbox.preferences.Settings.PseudoEnumSetting;
import com.example.mailbox.preferences.Settings.SettingsDescription;
import com.example.mailbox.preferences.Settings.SettingsUpgrader;
import com.example.mailbox.preferences.Settings.V;
import com.example.mailbox.preferences.Settings.WebFontSizeSetting;

import static com.example.mailbox.K9.LockScreenNotificationVisibility;


public class GlobalSettings {
    static final Map<String, TreeMap<Integer, SettingsDescription>> SETTINGS;
    private static final Map<Integer, SettingsUpgrader> UPGRADERS;

    static {
        Map<String, TreeMap<Integer, SettingsDescription>> s = new LinkedHashMap<>();

        /*
         * When adding new settings here, be sure to increment {@link Settings.VERSION}
         * and use that for whatever you add here.
         */

        s.put("animations", Settings.versions(
                new V(1, new BooleanSetting(false))
        ));
        s.put("backgroundOperations", Settings.versions(
                new V(1, new EnumSetting<>(K9.BACKGROUND_OPS.class, K9.BACKGROUND_OPS.WHEN_CHECKED_AUTO_SYNC))
        ));
        s.put("changeRegisteredNameColor", Settings.versions(
                new V(1, new BooleanSetting(false))
        ));
        s.put("confirmDelete", Settings.versions(
                new V(1, new BooleanSetting(false))
        ));
        s.put("confirmDeleteStarred", Settings.versions(
                new V(2, new BooleanSetting(false))
        ));
        s.put("confirmSpam", Settings.versions(
                new V(1, new BooleanSetting(false))
        ));
        s.put("confirmMarkAllRead", Settings.versions(
                new V(44, new BooleanSetting(true))
        ));
        s.put("enableDebugLogging", Settings.versions(
                new V(1, new BooleanSetting(false))
        ));
        s.put("enableSensitiveLogging", Settings.versions(
                new V(1, new BooleanSetting(false))
        ));
        s.put("fontSizeAccountDescription", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeAccountName", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeFolderName", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeFolderStatus", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeMessageComposeInput", Settings.versions(
                new V(5, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeMessageListDate", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeMessageListPreview", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeMessageListSender", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeMessageListSubject", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeMessageViewAdditionalHeaders", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeMessageViewCC", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeMessageViewContent", Settings.versions(
                new V(1, new WebFontSizeSetting(3)),
                new V(31, null)
        ));
        s.put("fontSizeMessageViewDate", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeMessageViewSender", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeMessageViewSubject", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeMessageViewTime", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("fontSizeMessageViewTo", Settings.versions(
                new V(1, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("gesturesEnabled", Settings.versions(
                new V(1, new BooleanSetting(true)),
                new V(4, new BooleanSetting(false))
        ));
        s.put("hideSpecialAccounts", Settings.versions(
                new V(1, new BooleanSetting(false))
        ));
        s.put("keyguardPrivacy", Settings.versions(
                new V(1, new BooleanSetting(false)),
                new V(12, null)
        ));
        s.put("language", Settings.versions(
                new V(1, new LanguageSetting())
        ));
        s.put("messageListPreviewLines", Settings.versions(
                new V(1, new IntegerRangeSetting(1, 100, 2))
        ));
        s.put("messageListStars", Settings.versions(
                new V(1, new BooleanSetting(true))
        ));
        s.put("messageViewFixedWidthFont", Settings.versions(
                new V(1, new BooleanSetting(false))
        ));
        s.put("messageViewReturnToList", Settings.versions(
                new V(1, new BooleanSetting(false))
        ));
        s.put("messageViewShowNext", Settings.versions(
                new V(1, new BooleanSetting(false))
        ));
        s.put("quietTimeEnabled", Settings.versions(
                new V(1, new BooleanSetting(false))
        ));
        s.put("quietTimeEnds", Settings.versions(
                new V(1, new TimeSetting("7:00"))
        ));
        s.put("quietTimeStarts", Settings.versions(
                new V(1, new TimeSetting("21:00"))
        ));
        s.put("registeredNameColor", Settings.versions(
                new V(1, new ColorSetting(0xFF00008F))
        ));
        s.put("showContactName", Settings.versions(
                new V(1, new BooleanSetting(false))
        ));
        s.put("showCorrespondentNames", Settings.versions(
                new V(1, new BooleanSetting(true))
        ));
        s.put("sortTypeEnum", Settings.versions(
                new V(10, new EnumSetting<>(SortType.class, Account.DEFAULT_SORT_TYPE))
        ));
        s.put("sortAscending", Settings.versions(
                new V(10, new BooleanSetting(Account.DEFAULT_SORT_ASCENDING))
        ));
        s.put("theme", Settings.versions(
                new V(1, new LegacyThemeSetting(AppTheme.LIGHT)),
                new V(58, new ThemeSetting(AppTheme.FOLLOW_SYSTEM))
        ));
        s.put("messageViewTheme", Settings.versions(
                new V(16, new LegacyThemeSetting(AppTheme.LIGHT)),
                new V(24, new SubThemeSetting(SubTheme.USE_GLOBAL))
        ));
        s.put("useVolumeKeysForListNavigation", Settings.versions(
                new V(1, new BooleanSetting(false))
        ));
        s.put("useVolumeKeysForNavigation", Settings.versions(
                new V(1, new BooleanSetting(false))
        ));
        s.put("wrapFolderNames", Settings.versions(
                new V(22, new BooleanSetting(false))
        ));
        s.put("notificationHideSubject", Settings.versions(
                new V(12, new EnumSetting<>(NotificationHideSubject.class, NotificationHideSubject.NEVER))
        ));
        s.put("useBackgroundAsUnreadIndicator", Settings.versions(
                new V(19, new BooleanSetting(true)),
                new V(59, new BooleanSetting(false))
        ));
        s.put("threadedView", Settings.versions(
                new V(20, new BooleanSetting(true))
        ));
        s.put("splitViewMode", Settings.versions(
                new V(23, new EnumSetting<>(SplitViewMode.class, SplitViewMode.NEVER))
        ));
        s.put("messageComposeTheme", Settings.versions(
                new V(24, new SubThemeSetting(SubTheme.USE_GLOBAL))
        ));
        s.put("fixedMessageViewTheme", Settings.versions(
                new V(24, new BooleanSetting(true))
        ));
        s.put("showContactPicture", Settings.versions(
                new V(25, new BooleanSetting(true))
        ));
        s.put("autofitWidth", Settings.versions(
                new V(28, new BooleanSetting(true))
        ));
        s.put("colorizeMissingContactPictures", Settings.versions(
                new V(29, new BooleanSetting(true))
        ));
        s.put("messageViewDeleteActionVisible", Settings.versions(
                new V(30, new BooleanSetting(true))
        ));
        s.put("messageViewArchiveActionVisible", Settings.versions(
                new V(30, new BooleanSetting(false))
        ));
        s.put("messageViewMoveActionVisible", Settings.versions(
                new V(30, new BooleanSetting(false))
        ));
        s.put("messageViewCopyActionVisible", Settings.versions(
                new V(30, new BooleanSetting(false))
        ));
        s.put("messageViewSpamActionVisible", Settings.versions(
                new V(30, new BooleanSetting(false))
        ));
        s.put("fontSizeMessageViewContentPercent", Settings.versions(
                new V(31, new IntegerRangeSetting(40, 250, 100))
        ));
        s.put("hideUserAgent", Settings.versions(
                new V(32, new BooleanSetting(false))
        ));
        s.put("hideTimeZone", Settings.versions(
                new V(32, new BooleanSetting(false))
        ));
        s.put("lockScreenNotificationVisibility", Settings.versions(
                new V(37, new EnumSetting<>(LockScreenNotificationVisibility.class,
                        LockScreenNotificationVisibility.MESSAGE_COUNT))
        ));
        s.put("confirmDeleteFromNotification", Settings.versions(
                new V(38, new BooleanSetting(true))
        ));
        s.put("messageListSenderAboveSubject", Settings.versions(
                new V(38, new BooleanSetting(false))
        ));
        s.put("notificationQuickDelete", Settings.versions(
                new V(38, new EnumSetting<>(NotificationQuickDelete.class, NotificationQuickDelete.NEVER))
        ));
        s.put("notificationDuringQuietTimeEnabled", Settings.versions(
                new V(39, new BooleanSetting(true))
        ));
        s.put("confirmDiscardMessage", Settings.versions(
                new V(40, new BooleanSetting(true))
        ));
        s.put("pgpInlineDialogCounter", Settings.versions(
                new V(43, new IntegerRangeSetting(0, Integer.MAX_VALUE, 0))
        ));
        s.put("pgpSignOnlyDialogCounter", Settings.versions(
                new V(45, new IntegerRangeSetting(0, Integer.MAX_VALUE, 0))
        ));
        s.put("fontSizeMessageViewBCC", Settings.versions(
                new V(48, new FontSizeSetting(FontSizes.FONT_DEFAULT))
        ));
        s.put("hideHostnameWhenConnecting", Settings.versions(
                new V(49, new BooleanSetting(false)),
                new V(56, null)
        ));

        SETTINGS = Collections.unmodifiableMap(s);

        Map<Integer, SettingsUpgrader> u = new HashMap<>();
        u.put(12, new SettingsUpgraderV12());
        u.put(24, new SettingsUpgraderV24());
        u.put(31, new SettingsUpgraderV31());
        u.put(58, new SettingsUpgraderV58());

        UPGRADERS = Collections.unmodifiableMap(u);
    }

    static Map<String, Object> validate(int version, Map<String, String> importedSettings) {
        return Settings.validate(version, SETTINGS, importedSettings, false);
    }

    public static Set<String> upgrade(int version, Map<String, Object> validatedSettings) {
        return Settings.upgrade(version, UPGRADERS, SETTINGS, validatedSettings);
    }

    public static Map<String, String> convert(Map<String, Object> settings) {
        return Settings.convert(settings, SETTINGS);
    }

    static Map<String, String> getGlobalSettings(Storage storage) {
        Map<String, String> result = new HashMap<>();
        for (String key : SETTINGS.keySet()) {
            String value = storage.getString(key, null);
            if (value != null) {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * Upgrades the settings from version 11 to 12
     *
     * Map the 'keyguardPrivacy' value to the new NotificationHideSubject enum.
     */
    private static class SettingsUpgraderV12 implements SettingsUpgrader {

        @Override
        public Set<String> upgrade(Map<String, Object> settings) {
            Boolean keyguardPrivacy = (Boolean) settings.get("keyguardPrivacy");
            if (keyguardPrivacy != null && keyguardPrivacy) {
                // current setting: only show subject when unlocked
                settings.put("notificationHideSubject", NotificationHideSubject.WHEN_LOCKED);
            } else {
                // always show subject [old default]
                settings.put("notificationHideSubject", NotificationHideSubject.NEVER);
            }
            return new HashSet<>(Collections.singletonList("keyguardPrivacy"));
        }
    }

    /**
     * Upgrades the settings from version 23 to 24.
     *
     * <p>
     * Set <em>messageViewTheme</em> to {@link SubTheme#USE_GLOBAL} if <em>messageViewTheme</em> has
     * the same value as <em>theme</em>.
     * </p>
     */
    private static class SettingsUpgraderV24 implements SettingsUpgrader {

        @Override
        public Set<String> upgrade(Map<String, Object> settings) {
            SubTheme messageViewTheme = (SubTheme) settings.get("messageViewTheme");
            AppTheme theme = (AppTheme) settings.get("theme");
            if ((theme == AppTheme.LIGHT && messageViewTheme == SubTheme.LIGHT) ||
                    (theme == AppTheme.DARK && messageViewTheme == SubTheme.DARK)) {
                settings.put("messageViewTheme", SubTheme.USE_GLOBAL);
            }

            return null;
        }
    }

    /**
     * Upgrades the settings from version 30 to 31.
     *
     * <p>
     * Convert value from <em>fontSizeMessageViewContent</em> to
     * <em>fontSizeMessageViewContentPercent</em>.
     * </p>
     */
    public static class SettingsUpgraderV31 implements SettingsUpgrader {

        @Override
        public Set<String> upgrade(Map<String, Object> settings) {
            int oldSize = (Integer) settings.get("fontSizeMessageViewContent");

            int newSize = convertFromOldSize(oldSize);

            settings.put("fontSizeMessageViewContentPercent", newSize);

            return new HashSet<>(Collections.singletonList("fontSizeMessageViewContent"));
        }

        public static int convertFromOldSize(int oldSize) {
            switch (oldSize) {
                case 1: {
                    return 40;
                }
                case 2: {
                    return 75;
                }
                case 4: {
                    return 175;
                }
                case 5: {
                    return 250;
                }
                case 3:
                default: {
                    return 100;
                }
            }
        }
    }

    /**
     * Upgrades the settings from version 57 to 58.
     *
     * <p>
     * Set <em>theme</em> to {@link AppTheme#FOLLOW_SYSTEM} if <em>theme</em> has the value {@link AppTheme#LIGHT}.
     * </p>
     */
    private static class SettingsUpgraderV58 implements SettingsUpgrader {

        @Override
        public Set<String> upgrade(Map<String, Object> settings) {
            AppTheme theme = (AppTheme) settings.get("theme");
            if (theme == AppTheme.LIGHT) {
                settings.put("theme", AppTheme.FOLLOW_SYSTEM);
            }

            return null;
        }
    }

    private static class LanguageSetting extends PseudoEnumSetting<String> {
        private final Context context = DI.get(Context.class);
        private final Map<String, String> mapping;

        LanguageSetting() {
            super("");

            Map<String, String> mapping = new HashMap<>();
            String[] values = context.getResources().getStringArray(R.array.language_values);
            for (String value : values) {
                if (value.length() == 0) {
                    mapping.put("", "default");
                } else {
                    mapping.put(value, value);
                }
            }
            this.mapping = Collections.unmodifiableMap(mapping);
        }

        @Override
        protected Map<String, String> getMapping() {
            return mapping;
        }

        @Override
        public String fromString(String value) throws InvalidSettingValueException {
            if (mapping.containsKey(value)) {
                return value;
            }

            throw new InvalidSettingValueException();
        }
    }

    static class LegacyThemeSetting extends SettingsDescription<AppTheme> {
        private static final String THEME_LIGHT = "light";
        private static final String THEME_DARK = "dark";

        LegacyThemeSetting(AppTheme defaultValue) {
            super(defaultValue);
        }

        @Override
        public AppTheme fromString(String value) throws InvalidSettingValueException {
            try {
                return K9.AppTheme.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new InvalidSettingValueException();
            }
        }

        @Override
        public AppTheme fromPrettyString(String value) throws InvalidSettingValueException {
            if (THEME_LIGHT.equals(value)) {
                return AppTheme.LIGHT;
            } else if (THEME_DARK.equals(value)) {
                return AppTheme.DARK;
            }

            throw new InvalidSettingValueException();
        }

        @Override
        public String toPrettyString(AppTheme value) {
            switch (value) {
                case LIGHT: return THEME_LIGHT;
                case DARK: return THEME_DARK;
            }

            throw new AssertionError("Unexpected case: " + value);
        }

        @Override
        public String toString(AppTheme value) {
            return value.name();
        }
    }

    private static class ThemeSetting extends SettingsDescription<AppTheme> {
        private static final String THEME_LIGHT = "light";
        private static final String THEME_DARK = "dark";
        private static final String THEME_FOLLOW_SYSTEM = "follow_system";

        ThemeSetting(AppTheme defaultValue) {
            super(defaultValue);
        }

        @Override
        public AppTheme fromString(String value) throws InvalidSettingValueException {
            try {
                return AppTheme.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new InvalidSettingValueException();
            }
        }

        @Override
        public AppTheme fromPrettyString(String value) throws InvalidSettingValueException {
            if (THEME_LIGHT.equals(value)) {
                return AppTheme.LIGHT;
            } else if (THEME_DARK.equals(value)) {
                return AppTheme.DARK;
            } else if (THEME_FOLLOW_SYSTEM.equals(value)) {
                return AppTheme.FOLLOW_SYSTEM;
            }

            throw new InvalidSettingValueException();
        }

        @Override
        public String toPrettyString(AppTheme value) {
            switch (value) {
                case LIGHT: return THEME_LIGHT;
                case DARK: return THEME_DARK;
                case FOLLOW_SYSTEM: return THEME_FOLLOW_SYSTEM;
            }

            throw new AssertionError("Unexpected case: " + value);
        }

        @Override
        public String toString(AppTheme value) {
            return value.name();
        }
    }

    private static class SubThemeSetting extends SettingsDescription<SubTheme> {
        private static final String THEME_LIGHT = "light";
        private static final String THEME_DARK = "dark";
        private static final String THEME_USE_GLOBAL = "use_global";

        SubThemeSetting(SubTheme defaultValue) {
            super(defaultValue);
        }

        @Override
        public SubTheme fromString(String value) throws InvalidSettingValueException {
            try {
                return SubTheme.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new InvalidSettingValueException();
            }
        }

        @Override
        public SubTheme fromPrettyString(String value) throws InvalidSettingValueException {
            if (THEME_LIGHT.equals(value)) {
                return SubTheme.LIGHT;
            } else if (THEME_DARK.equals(value)) {
                return SubTheme.DARK;
            } else if (THEME_USE_GLOBAL.equals(value)) {
                return SubTheme.USE_GLOBAL;
            }

            throw new InvalidSettingValueException();
        }

        @Override
        public String toPrettyString(SubTheme value) {
            switch (value) {
                case LIGHT: return THEME_LIGHT;
                case DARK: return THEME_DARK;
                case USE_GLOBAL: return THEME_USE_GLOBAL;
            }

            throw new AssertionError("Unexpected case: " + value);
        }

        @Override
        public String toString(SubTheme value) {
            return value.name();
        }
    }

    private static class TimeSetting extends SettingsDescription<String> {
        private static final String VALIDATION_EXPRESSION = "[0-2]*[0-9]:[0-5]*[0-9]";

        TimeSetting(String defaultValue) {
            super(defaultValue);
        }

        @Override
        public String fromString(String value) throws InvalidSettingValueException {
            if (!value.matches(VALIDATION_EXPRESSION)) {
                throw new InvalidSettingValueException();
            }
            return value;
        }
    }
}
