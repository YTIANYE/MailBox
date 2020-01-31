package com.example.mailbox.storage.migrations;


import java.util.List;

import android.os.SystemClock;

import com.example.mailbox.mail.Folder;
import com.example.mailbox.mailstore.LocalFolder;
import com.example.mailbox.mailstore.LocalStore;
import com.example.mailbox.mailstore.MigrationsHelper;
import com.example.mailbox.preferences.StorageEditor;
import timber.log.Timber;


class MigrationTo42 {
    public static void from41MoveFolderPreferences(MigrationsHelper migrationsHelper) {
        try {
            LocalStore localStore = migrationsHelper.getLocalStore();

            long startTime = SystemClock.elapsedRealtime();
            StorageEditor editor = migrationsHelper.getPreferences().createStorageEditor();

            List<? extends Folder > folders = localStore.getPersonalNamespaces(true);
            for (Folder folder : folders) {
                if (folder instanceof LocalFolder) {
                    LocalFolder lFolder = (LocalFolder)folder;
                    lFolder.save(editor);
                }
            }

            editor.commit();
            long endTime = SystemClock.elapsedRealtime();
            Timber.i("Putting folder preferences for %d folders back into Preferences took %d ms",
                    folders.size(), endTime - startTime);
        } catch (Exception e) {
            Timber.e(e, "Could not replace Preferences in upgrade from DB_VERSION 41");
        }
    }
}
