/*
 * Copyright 2014 Pierre Chabardes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.twobeone.remotehelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.twobeone.remotehelper.adapter.Status_Item_Adapter;
import net.twobeone.remotehelper.handler.SQLiteHelper;

import java.io.File;

/**
 * Created by Administrator on 2017-06-09.
 */

public class SignUpActivity extends Activity {

    private ImageButton back;
    private TextView title_text;
    private ImageButton insert;
    private ImageButton cancel;
    private ImageButton edit_basic;
    private ImageButton edit_mypic;
    private EditText name;
    private EditText age;
    private EditText birth;
    private EditText sex;
    private EditText mobile;
    private EditText emergency;
    private EditText addr;
    private EditText detail_addr;
    private EditText blood_type;
    private EditText sickness;
    private EditText hospital;
    private EditText doctor;
    private EditText etc;

    SQLiteDatabase db;
    SQLiteHelper helper;

    private static final int PIC_FROM_CAMERA = 0;
    private static final int PIC_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;
    private static final int SEARCH_ADDRESS_ACTIVITY = 3;
    private ArrayAdapter<String> adapter = null;
    private ImageView iv_UserPhoto;
    private Uri mImageCaptureUri;
    private String absoultePath;
    private String regid;
    private String response;
    private String msg;
    private String status;

    private ListView basic_listview;
    private Status_Item_Adapter basic_adapter;
    private ListView health_listview;
    private Status_Item_Adapter health_adapter;
    private LinearLayout basic_edit;
    private LinearLayout health_edit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_signup);
//
//        Intent intent = getIntent();
//        regid = intent.getExtras().getString("regid");
//        Log.e("SSSSS", "regid = " + regid);
//
//        helper = new SQLiteHelper(getApplicationContext(), "userinfo.db", null, 1);
//
//        insert = (ImageButton) findViewById(R.id.insert);
//        cancel = (ImageButton) findViewById(R.id.cancel);
//        edit_basic = (ImageButton) findViewById(R.id.edit_basic);
//        edit_mypic = (ImageButton) findViewById(R.id.edit_mypic);
//        iv_UserPhoto = (ImageView) findViewById(R.id.mypic);
//        iv_UserPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
//        iv_UserPhoto.setClipToOutline(true);
//
//        if (select("photo") != "") {
//            Log.e("JH", "photo is not null :::: " + select("photo"));
//            try {
//                File before = new File(select("photo"));
//                mImageCaptureUri = Uri.fromFile(before);
//                iv_UserPhoto.setImageURI(mImageCaptureUri);
//            } catch (Exception e) {
//                // TODO: handle exception
//            }
//        }
//
//        basic_adapter = new Status_Item_Adapter();
//        health_adapter = new Status_Item_Adapter();
//
//        basic_edit = (LinearLayout) findViewById(R.id.basic_edit);
//        health_edit = (LinearLayout) findViewById(R.id.health_edit);
//
//        basic_listview = (ListView) findViewById(R.id.basic_list);
//        health_listview = (ListView) findViewById(R.id.health_list);
//
//        basic_listview.setAdapter(basic_adapter);
//        health_listview.setAdapter(health_adapter);
//
//        // 기본정보
//        name = (EditText) findViewById(R.id.edit_name);
//        name.setText(select("_name"));
//        age = (EditText) findViewById(R.id.edit_age);
//        age.setText(select("age"));
//        birth = (EditText) findViewById(R.id.edit_birth);
//        birth.setText(select("birth"));
//        sex = (EditText) findViewById(R.id.edit_sex);
//        sex.setText(select("sex"));
//        mobile = (EditText) findViewById(R.id.edit_mobile);
//        mobile.setText(select("mobile"));
//        emergency = (EditText) findViewById(R.id.edit_emergency);
//        emergency.setText(select("emergency"));
//        addr = (EditText) findViewById(R.id.edit_addr);
//        addr.setText(select("address"));
//        detail_addr = (EditText) findViewById(R.id.edit_detail_addr);
//        detail_addr.setText(select("detail_address"));
//
//        // 건강정보
//        blood_type = (EditText) findViewById(R.id.edit_blood);
//        blood_type.setText(select("blood_type"));
//        sickness = (EditText) findViewById(R.id.edit_sick);
//        sickness.setText(select("sickness"));
//        hospital = (EditText) findViewById(R.id.edit_hospital);
//        hospital.setText(select("hospital"));
//        doctor = (EditText) findViewById(R.id.edit_doctor);
//        doctor.setText(select("doctor"));
//        etc = (EditText) findViewById(R.id.edit_etc);
//        etc.setText(select("etc"));
//
//        setList();
//        listViewHeightSet(basic_adapter, basic_listview);
//        listViewHeightSet(health_adapter, health_listview);
//
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private String select(String column) {
        db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select " + column + " from user_infomation", null);
        String result = "";

        while (c.moveToNext()) {
            result = c.getString(c.getColumnIndex(column));
        }

        return result;
    }

    private static void listViewHeightSet(BaseAdapter listAdapter, ListView listView) {
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void setList() {
        basic_adapter.deleteItem();
        basic_adapter.addItem("이름 : ", select("_name"));
        basic_adapter.addItem("나이 : ", select("age"));
        basic_adapter.addItem("생년월일 : ", select("birth"));
        basic_adapter.addItem("성별 : ", select("sex"));
        basic_adapter.addItem("연락처 : ", select("mobile"));
        basic_adapter.addItem("긴급연락처 : ", select("emergency"));
        basic_adapter.addItem("거주지주소 : ", select("address") + " " + select("detail_address"));

        health_adapter.deleteItem();
        health_adapter.addItem("혈액형 : ", select("blood_type"));
        health_adapter.addItem("질환 : ", select("sickness"));
        health_adapter.addItem("주치병원 : ", select("hospital"));
        health_adapter.addItem("주치의 : ", select("doctor"));
        health_adapter.addItem("기타사항 : ", select("etc"));
    }
}
