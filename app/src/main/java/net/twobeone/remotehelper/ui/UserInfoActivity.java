package net.twobeone.remotehelper.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.databinding.ActivityUserInfoBinding;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserInfoActivity extends BaseActivity {

    private ActivityUserInfoBinding mBinding;

    private Animator.AnimatorListener mCollapseListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mBinding.contentOptional.setVisibility(View.GONE);
            hideSoftInputFromWindow();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_info);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.etUserMobile.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mBinding.etEmergencyContact.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // 선택정보
        mBinding.tvOptional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.contentOptional.getVisibility() == View.VISIBLE) {
                    collapse(mBinding.contentOptional);
                } else {
                    expand(mBinding.contentOptional);
                }
            }
        });

        // 저장버튼
        mBinding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    saveData();
                    hideSoftInputFromWindow();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            userInfoUpload();
                        }
                    }).start();
                }
            }
        });
    }

    private boolean isValid() {
        String userName = mBinding.etUserName.getText().toString().trim();
        mBinding.etUserName.setText(userName);
        if (userName.matches("")) {
            Toast.makeText(this, "성명을 입력해 주세요.", Toast.LENGTH_LONG).show();
            mBinding.etUserName.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void hideSoftInputFromWindow() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mBinding.getRoot().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void expand(View view) {
        view.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);
        slideAnimator(view, 0, view.getMeasuredHeight()).start();
    }

    private void collapse(final View view) {
        int finalHeight = view.getHeight();
        ValueAnimator animator = slideAnimator(view, finalHeight, 0);
        animator.addListener(mCollapseListener);
        animator.start();
    }

    private ValueAnimator slideAnimator(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void selectData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mBinding.etUserName.setText(prefs.getString(Constants.PREF_USER_NAME, ""));
        mBinding.etUserAge.setText(prefs.getString(Constants.PREF_USER_AGE, ""));
        mBinding.etUserMobile.setText(prefs.getString(Constants.PREF_USER_MOBILE, ""));
        mBinding.etEmergencyContact.setText(prefs.getString(Constants.PREF_USER_EMERGENCY_CONTACT, ""));
        mBinding.etBloodType.setText(prefs.getString(Constants.PREF_USER_BLOOD_TYPE, ""));
        mBinding.etEtc.setText(prefs.getString(Constants.PREF_USER_ETC, ""));
    }

    private void saveData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PREF_USER_NAME, mBinding.etUserName.getText().toString().trim());
        editor.putString(Constants.PREF_USER_AGE, mBinding.etUserAge.getText().toString().trim());
        editor.putString(Constants.PREF_USER_MOBILE, mBinding.etUserMobile.getText().toString().trim());
        editor.putString(Constants.PREF_USER_EMERGENCY_CONTACT, mBinding.etEmergencyContact.getText().toString().trim());
        editor.putString(Constants.PREF_USER_BLOOD_TYPE, mBinding.etBloodType.getText().toString().trim());
        editor.putString(Constants.PREF_USER_ETC, mBinding.etEtc.getText().toString().trim());
        editor.commit();
        Toast.makeText(this, "정상적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public void userInfoUpload() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String urlServer = "https://remohelper.com:440/m/reqInsertSaviorInfo.ajax";

        String device_ID = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        try {
            OkHttpClient client = new OkHttpClient();

            FormBody.Builder builder = new FormBody.Builder();

            RequestBody requestBody = builder
                    .add("deviceId", device_ID)
                    .add("saviorId", device_ID)
                    .add("saviorName", prefs.getString(Constants.PREF_USER_NAME, ""))
                    .add("age", prefs.getString(Constants.PREF_USER_AGE, ""))
                    .add("birth", "")
                    .add("sex", "")
                    .add("mobileNumber", prefs.getString(Constants.PREF_USER_MOBILE, ""))
                    .add("emergencyNumber", prefs.getString(Constants.PREF_USER_EMERGENCY_CONTACT, ""))
                    .add("address", "")
                    .add("bloodgroups", prefs.getString(Constants.PREF_USER_BLOOD_TYPE, ""))
                    .add("sickness", "")
                    .add("hospital", "")
                    .add("doctor", "")
                    .add("etc", prefs.getString(Constants.PREF_USER_ETC, ""))
                    .add("regId", prefs.getString(Constants.PROPERTY_REG_ID, "")).build();

            Request request = new Request.Builder().url(urlServer).post(requestBody).build();
            Response response = client.newCall(request).execute();

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("Error Http connect ::: ", ex.toString());
        }
    }
}
