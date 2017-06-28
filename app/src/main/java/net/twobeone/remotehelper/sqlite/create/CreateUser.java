package net.twobeone.remotehelper.sqlite.create;

import android.database.sqlite.SQLiteDatabase;

import net.twobeone.remotehelper.sqlite.SQLiteHelper;

public class CreateUser implements SQLiteHelper.Creator {

    @Override
    public void create(SQLiteDatabase db) {

        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE IF NOT EXISTS user (                                                                      ");
        sql.append("      name              TEXT                                                                            ");
        sql.append("    , age               TEXT                                                                            ");
        sql.append("    , birth             TEXT                                                                            ");
        sql.append("    , sex               TEXT                                                                            ");
        sql.append("    , mobile            TEXT                                                                            ");
        sql.append("    , emergency         TEXT                                                                            ");
        sql.append("    , address           TEXT                                                                            ");
        sql.append("    , detail_address    TEXT                                                                            ");
        sql.append("    , blood_type        TEXT                                                                            ");
        sql.append("    , sickness          TEXT                                                                            ");
        sql.append("    , hospital          TEXT                                                                            ");
        sql.append("    , doctor            TEXT                                                                            ");
        sql.append("    , etc               TEXT                                                                            ");
        sql.append("    , photo             TEXT                                                                            ");
        sql.append(" );                                                                                                     ");

        db.execSQL(sql.toString());
    }
}
