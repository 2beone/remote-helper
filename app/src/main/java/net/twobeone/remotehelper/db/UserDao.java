package net.twobeone.remotehelper.db;

import android.content.ContentValues;
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

    public long insert(User item) {

        ContentValues values = new ContentValues();
        values.put("name", item.name);
        values.put("age", item.age);
        values.put("birth", item.birth);
        values.put("sex", item.sex);
        values.put("mobile", item.mobile);
        values.put("emergency", item.emergency);
        values.put("address", item.address);
        values.put("detail_address", item.addressDetail);
        values.put("blood_type", item.bloodType);
        values.put("sickness", item.sickness);
        values.put("hospital", item.hospital);
        values.put("doctor", item.doctor);
        values.put("etc", item.etc);
        values.put("photo", item.imgPath);

        return getWritableDatabase().insert("user", null, values);
    }

    public int update(User item) {

        ContentValues values = new ContentValues();
        values.put("name", item.name);
        values.put("age", item.age);
        values.put("birth", item.birth);
        values.put("sex", item.sex);
        values.put("mobile", item.mobile);
        values.put("emergency", item.emergency);
        values.put("address", item.address);
        values.put("detail_address", item.addressDetail);
        values.put("blood_type", item.bloodType);
        values.put("sickness", item.sickness);
        values.put("hospital", item.hospital);
        values.put("doctor", item.doctor);
        values.put("etc", item.etc);
        values.put("photo", item.imgPath);

        return getWritableDatabase().update("user", values, null, new String[]{});
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
            item.addressDetail = cursor.getString(cursor.getColumnIndex("detail_address"));
            item.bloodType = cursor.getString(cursor.getColumnIndex("blood_type"));
            item.sickness = cursor.getString(cursor.getColumnIndex("sickness"));
            item.hospital = cursor.getString(cursor.getColumnIndex("hospital"));
            item.doctor = cursor.getString(cursor.getColumnIndex("doctor"));
            item.etc = cursor.getString(cursor.getColumnIndex("etc"));
            item.imgPath = cursor.getString(cursor.getColumnIndex("photo"));
        }
        cursor.close();
        return item;
    }
}
