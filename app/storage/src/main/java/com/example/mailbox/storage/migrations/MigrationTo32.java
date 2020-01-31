package com.example.mailbox.storage.migrations;


import android.database.sqlite.SQLiteDatabase;


class MigrationTo32 {
    public static void updateDeletedColumnFromFlags(SQLiteDatabase db) {
        db.execSQL("UPDATE messages SET deleted = 1 WHERE flags LIKE '%DELETED%'");
    }
}
