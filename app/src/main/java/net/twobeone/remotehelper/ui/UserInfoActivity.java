package net.twobeone.remotehelper.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.databinding.ActivityUserInfoBinding;
import net.twobeone.remotehelper.db.UserDao;
import net.twobeone.remotehelper.db.model.User;
import net.twobeone.remotehelper.ui.adapter.Status_Item_Adapter;
import net.twobeone.remotehelper.widget.RoundImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class UserInfoActivity extends BaseActivity {

    private Context mContext;

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

    private static final int PIC_FROM_CAMERA = 0;
    private static final int PIC_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;
    private static final int SEARCH_ADDRESS_ACTIVITY = 3;
    private ArrayAdapter<String> adapter = null;
    private RoundImageView iv_UserPhoto;
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
    private ActivityUserInfoBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = this;

        insert = (ImageButton) findViewById(R.id.insert);
        insert.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_insert));

        cancel = (ImageButton) findViewById(R.id.cancel);
        cancel.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_cancel));
        edit_basic = (ImageButton) findViewById(R.id.edit_basic);
        edit_basic.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_edit));
        edit_mypic = (ImageButton) findViewById(R.id.edit_mypic);
        edit_mypic.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_photo_edit));
        iv_UserPhoto = (RoundImageView) findViewById(R.id.user_img);
        iv_UserPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
        iv_UserPhoto.setImageResource(R.drawable.user_default);

        basic_adapter = new Status_Item_Adapter();
        health_adapter = new Status_Item_Adapter();

        basic_edit = (LinearLayout) findViewById(R.id.basic_edit);
        health_edit = (LinearLayout) findViewById(R.id.health_edit);

        basic_listview = (ListView) findViewById(R.id.basic_list);
        health_listview = (ListView) findViewById(R.id.health_list);

        basic_listview.setAdapter(basic_adapter);
        health_listview.setAdapter(health_adapter);


        name = (EditText) findViewById(R.id.edit_name);
        age = (EditText) findViewById(R.id.edit_age);
        birth = (EditText) findViewById(R.id.edit_birth);
        sex = (EditText) findViewById(R.id.edit_sex);
        mobile = (EditText) findViewById(R.id.edit_mobile);
        emergency = (EditText) findViewById(R.id.edit_emergency);
        addr = (EditText) findViewById(R.id.edit_addr);
        detail_addr = (EditText) findViewById(R.id.edit_detail_addr);

        blood_type = (EditText) findViewById(R.id.edit_blood);
        sickness = (EditText) findViewById(R.id.edit_sick);
        hospital = (EditText) findViewById(R.id.edit_hospital);
        doctor = (EditText) findViewById(R.id.edit_doctor);
        etc = (EditText) findViewById(R.id.edit_etc);

        selectItem();

        setList();

        listViewHeightSet(basic_adapter, basic_listview);
        listViewHeightSet(health_adapter, health_listview);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        edit_basic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                edit_basic.setVisibility(edit_basic.INVISIBLE);
                insert.setVisibility(insert.VISIBLE);
                cancel.setVisibility(cancel.VISIBLE);

                basic_listview.setVisibility(basic_listview.GONE);
                health_listview.setVisibility(health_listview.GONE);
                basic_edit.setVisibility(basic_edit.VISIBLE);
                health_edit.setVisibility(health_edit.VISIBLE);

                name.setEnabled(true);
                age.setEnabled(true);
                birth.setEnabled(true);
                sex.setEnabled(true);
                mobile.setEnabled(true);
                emergency.setEnabled(true);
                addr.setEnabled(true);
                detail_addr.setEnabled(true);

                blood_type.setEnabled(true);
                sickness.setEnabled(true);
                hospital.setEnabled(true);
                doctor.setEnabled(true);
                etc.setEnabled(true);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                edit_basic.setVisibility(edit_basic.VISIBLE);
                insert.setVisibility(insert.INVISIBLE);
                cancel.setVisibility(cancel.INVISIBLE);

                basic_listview.setVisibility(basic_listview.VISIBLE);
                health_listview.setVisibility(health_listview.VISIBLE);
                basic_edit.setVisibility(basic_edit.GONE);
                health_edit.setVisibility(health_edit.GONE);

                name.setEnabled(false);
                age.setEnabled(false);
                birth.setEnabled(false);
                sex.setEnabled(false);
                mobile.setEnabled(false);
                emergency.setEnabled(false);
                addr.setEnabled(false);
                detail_addr.setEnabled(false);
                blood_type.setEnabled(false);
                sickness.setEnabled(false);
                hospital.setEnabled(false);
                doctor.setEnabled(false);
                etc.setEnabled(false);
            }
        });

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().getBytes().length <= 0) {
                    Toast.makeText(mContext, "이름은 필수입력란입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    edit_basic.setVisibility(edit_basic.VISIBLE);
                    insert.setVisibility(insert.INVISIBLE);
                    cancel.setVisibility(cancel.INVISIBLE);

                    basic_listview.setVisibility(basic_listview.VISIBLE);
                    health_listview.setVisibility(health_listview.VISIBLE);
                    basic_edit.setVisibility(basic_edit.GONE);
                    health_edit.setVisibility(health_edit.GONE);

                    name.setEnabled(false);
                    age.setEnabled(false);
                    birth.setEnabled(false);
                    sex.setEnabled(false);
                    mobile.setEnabled(false);
                    emergency.setEnabled(false);
                    addr.setEnabled(false);
                    detail_addr.setEnabled(false);
                    blood_type.setEnabled(false);
                    sickness.setEnabled(false);
                    hospital.setEnabled(false);
                    doctor.setEnabled(false);
                    etc.setEnabled(false);

                }
            }
        });

        birth.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (birth.isEnabled() == true) {
                        UserInfoActivity.this.DialogDatePicker();
                    }
                }
                return true;
            }
        });

        sex.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (sex.isEnabled() == true) {
                        UserInfoActivity.this.DialogSexPicker();
                    }
                }
                return true;
            }
        });

        blood_type.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (blood_type.isEnabled() == true) {
                        UserInfoActivity.this.DialogBloodPicker();
                    }
                }
                return true;
            }
        });

        addr.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (addr.isEnabled() == true) {
                        Intent i = new Intent(UserInfoActivity.this, SearchAddressActivity.class);
                        startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);
                    }
                }
                return true;
            }
        });

        edit_mypic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakePhotoAction();
                    }
                };
                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakeAlbumAction();
                    }
                };
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT).setTitle("업로드할 이미지 선택")
                        .setPositiveButton("사진촬영", cameraListener).setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:

                String get_addr = data.getExtras().getString("data");
                int sub = get_addr.indexOf(",");
                if (get_addr != null)
                    addr.setText(get_addr.substring(sub + 1));
                break;

            case PIC_FROM_ALBUM: {
                mImageCaptureUri = data.getData();

                Log.d("SSSSSS", mImageCaptureUri.getPath() + "\n" + data.getData());

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 300);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE);
                break;
            }
            case PIC_FROM_CAMERA: {
                String url = "/tmp_userImg.jpg";
                Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + url));
                Log.d("SSSSSS", "image uri : " + uri);
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(uri, "image/*");

                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 300);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE);
                break;
            }
            case CROP_FROM_IMAGE:

                if (resultCode != RESULT_OK) {
                    return;
                }

                final Bundle extras = data.getExtras();

//                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RemoteHelper/"
//                        + System.currentTimeMillis() + ".jpg";
                String filePath = getFilesDir().getPath() + "/RemoteHelper/userImg.jpg";


                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    iv_UserPhoto.setImageBitmap(photo);

                    storeCropImage(photo, filePath);

                    absoultePath = filePath;
                    break;
                }

                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }
        }
    }

    private void storeCropImage(Bitmap bitmap, final String filePath) {
        String dirPath = getFilesDir().getPath() + "/RemoteHelper/";
        File directory_RemoteHelper = new File(dirPath);

        if (!directory_RemoteHelper.exists())
            directory_RemoteHelper.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setList() {

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

    private void selectItem() {
        User user = UserDao.getInstance().select();
        if (user != null) {
            mBinding.name.setText(user.name);
            mBinding.age.setText(user.age);
            mBinding.birth.setText(user.birth);
            mBinding.sex.setText(user.sex);
            mBinding.mobile.setText(user.mobile);
            mBinding.emergency.setText(user.emergency);
            mBinding.addr.setText(user.address);
            mBinding.detailAddr.setText(user.addressDetail);

//            blood_type.setText(userInfo.getBloodType());
//            sickness.setText(userInfo.getSickness());

            mBinding.hospital.setText(user.hospital);
            mBinding.doctor.setText(user.doctor);
            mBinding.etc.setText(user.etc);

            if (!TextUtils.isEmpty(user.imgPath)) {
                mImageCaptureUri = Uri.fromFile(new File(user.imgPath));
                iv_UserPhoto.setImageURI(mImageCaptureUri);
            }
        }
    }


    public void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String url = "/tmp_userImg.jpg";
        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + url));
        Log.d("SSSSSS", "image uri : " + uri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, PIC_FROM_CAMERA);
    }

    public void doTakeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PIC_FROM_ALBUM);
    }

    private void DialogDatePicker() {
        Calendar c = Calendar.getInstance();
        int cyear = 1991;//c.get(Calendar.YEAR);
        int cmonth = c.get(Calendar.MONTH);
        int cday = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date_selected = String.valueOf(year) + "년" + String.valueOf(monthOfYear + 1) + "월"
                        + String.valueOf(dayOfMonth) + "일";
                birth.setText(date_selected);
            }
        };
        DatePickerDialog alert = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, mDateSetListener, cyear,
                cmonth, cday);
        alert.getDatePicker().setSpinnersShown(true);
        alert.getDatePicker().setCalendarViewShown(false);
        alert.show();
    }

    private void DialogSexPicker() {
        CharSequence info[] = new CharSequence[]{"남자", "여자"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("성별");

        builder.setItems(info, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        sex.setText("남자");
                        break;
                    case 1:
                        sex.setText("여자");
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void DialogBloodPicker() {
        CharSequence info[] = new CharSequence[]{"RH+ A", "RH+ B", "RH+ AB", "RH+ O", "RH- A", "RH- B", "RH- AB",
                "RH- O"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("혈액형");

        builder.setItems(info, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        blood_type.setText("RH+ A");
                        break;
                    case 1:
                        blood_type.setText("RH+ B");
                        break;
                    case 2:
                        blood_type.setText("RH+ AB");
                        break;
                    case 3:
                        blood_type.setText("RH+ O");
                        break;
                    case 4:
                        blood_type.setText("RH- A");
                        break;
                    case 5:
                        blood_type.setText("RH- B");
                        break;
                    case 6:
                        blood_type.setText("RH- AB");
                        break;
                    case 7:
                        blood_type.setText("RH- O");
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
