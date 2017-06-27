package net.twobeone.remotehelper.sqlite;

import android.database.sqlite.SQLiteDatabase;

public class SQLiteDao {

    protected SQLiteDatabase getWritableDatabase() {
        return SQLiteHelper.getInstance().getWritableDatabase();
    }

    protected SQLiteDatabase getReadableDatabase() {
        return SQLiteHelper.getInstance().getReadableDatabase();
    }
}
