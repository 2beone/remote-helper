package net.twobeone.remotehelper.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.sqlite.create.CreateUser;

import java.util.ArrayList;
import java.util.List;

public final class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteHelper sInstance;

    private SQLiteHelper(Context context) {
        super(context, Constants.DATABASE_FILE_NAME, null, Constants.DATABASE_VERSION_CODE);
    }

    public static synchronized SQLiteHelper initialize(Context context) {
        if (sInstance == null) {
            sInstance = new SQLiteHelper(context);
        }
        return sInstance;
    }

    public static synchronized SQLiteHelper getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(SQLiteHelper.class.getSimpleName() + " is not initialized, call initialize(..) method first.");
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        List<Creator> creators = new ArrayList<>();
        creators.add(new CreateUser());

        db.beginTransaction();
        try {
            for (Creator creator : creators) {
                creator.create(db);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        List<Upgrader> upgraders = new ArrayList<>();

        db.beginTransaction();
        try {
            for (Upgrader upgrader : upgraders) {
                if (upgrader.oldVersion >= oldVersion && upgrader.newVersion <= newVersion) {
                    upgrader.upgrade(db);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public interface Creator {
        void create(SQLiteDatabase db);
    }

    public static abstract class Upgrader {

        int oldVersion;
        int newVersion;

        public Upgrader(int oldVersion, int newVersion) {
            this.oldVersion = oldVersion;
            this.newVersion = newVersion;
        }

        public abstract void upgrade(SQLiteDatabase db);
    }
}