package net.twobeone.remotehelper.db;

import android.database.Cursor;

import net.twobeone.remotehelper.db.model.User;
import net.twobeone.remotehelper.sqlite.SQLiteDao;

public class UserDao extends SQLiteDao {

    private static UserDao sInstance;

    private UserDao() {

    }

    public static synchronized UserDao getInstance() {
        if (sInstance == null) {
            sInstance = new UserDao();
        }
        return sInstance;
    }

    public User select() {
        User item = null;
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM user ", new String[]{});
        if (cursor.moveToNext()) {
            item = new User();
            item.name = cursor.getString(cursor.getColumnIndex("name"));
            item.age = cursor.getString(cursor.getColumnIndex("age"));
            item.birth = cursor.getString(cursor.getColumnIndex("birth"));
            item.sex = cursor.getString(cursor.getColumnIndex("sex"));
            item.mobile = cursor.getString(cursor.getColumnIndex("mobile"));
            item.emergency = cursor.getString(cursor.getColumnIndex("emergency"));
            item.address = cursor.getString(cursor.getColumnIndex("address"));
            item.addressDetail = cursor.getString(cursor.getColumnIndex("addressDetail"));
            item.bloodType = cursor.getString(cursor.getColumnIndex("bloodType"));
            item.sickness = cursor.getString(cursor.getColumnIndex("sickness"));
            item.hospital = cursor.getString(cursor.getColumnIndex("hospital"));
            item.doctor = cursor.getString(cursor.getColumnIndex("doctor"));
            item.etc = cursor.getString(cursor.getColumnIndex("etc"));
            item.imgPath = cursor.getString(cursor.getColumnIndex("imgPath"));
        }
        cursor.close();
        return item;
    }
}
