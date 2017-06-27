package net.twobeone.remotehelper;

import android.app.Application;

import net.twobeone.remotehelper.sqlite.SQLiteHelper;

public final class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SQLiteHelper.initialize(this).getReadableDatabase();
    }

    @Override
    public void onTerminate() {
        SQLiteHelper.getInstance().close();
        super.onTerminate();
    }
}
