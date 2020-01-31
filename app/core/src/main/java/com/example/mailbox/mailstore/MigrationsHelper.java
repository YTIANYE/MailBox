package com.example.mailbox.mailstore;


import java.util.List;

import android.content.Context;

import com.example.mailbox.Account;
import com.example.mailbox.Preferences;
import com.example.mailbox.mail.Flag;
import com.example.mailbox.preferences.Storage;


/**
 * Helper to allow accessing classes and methods that aren't visible or accessible to the 'migrations' package
 */
public interface MigrationsHelper {
    LocalStore getLocalStore();
    Storage getStorage();
    Preferences getPreferences();
    Account getAccount();
    Context getContext();
    String serializeFlags(List<Flag> flags);
}
