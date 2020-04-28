package com.fsck.k9.storage.migrations;


import android.database.sqlite.SQLiteDatabase;


class MigrationTo55 {
    static void createFtsSearchTable(SQLiteDatabase db) {
        //创建虚拟表 message_fulltext
        db.execSQL("CREATE VIRTUAL TABLE messages_fulltext USING fts4 (fulltext)");
    }
}
