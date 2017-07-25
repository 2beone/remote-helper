package net.twobeone.remotehelper.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.R;

import java.io.IOException;

public final class MainActivity extends BaseActivity {

    public static final String REDIRECT = "redirect";

    public interface Redirect {
        int CALL = 1;
        int FILE_BOX = 2;
        int HELP = 3;
        int SETTINGS = 4;
        int SAFETY = 5;
    }

    private static String regid;
    private GoogleCloudMessaging gcm;
    private Context context;

    private DrawerLayout mDrawerLayout;
    private ViewPager mViewPager;
    private TextView mUserName;
    private String helper_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            helper_id = getIntent().getExtras().getString("helper_id");
        } catch (Exception e) {
            helper_id = "false";
        }

        if(getIntent().getIntExtra(REDIRECT, 0) == Redirect.CALL){

            helper_id = "redirect";
        }

        checkPermissions();

        context = getApplicationContext();

        //GCM ID 생성
        gcm = GoogleCloudMessaging.getInstance(context); // GoogleCloudMessaging
        // 클래스의 인스턴스를 생성한다
        regid = getRegistrationId(context); // 기존에 발급받은 등록 아이디를 가져온다
        if (regid.equals("")) {
            registerInBackground();
        }

        // 툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 액션바
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // 네비게이션
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new MainNavigationListener(this));

        View navHeaderView = navigationView.getHeaderView(0);
        navHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserInfoActivity.class));
            }
        });
        mUserName = (TextView) navHeaderView.findViewById(R.id.userage_txt);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager(mViewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // 리다이렉트
        switch (getIntent().getIntExtra(REDIRECT, 0)) {
            case Redirect.FILE_BOX:
                // tabLayout.getTabAt(1).select();
                mViewPager.setCurrentItem(1);
                break;
            case Redirect.SETTINGS:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case Redirect.HELP:
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
                break;
            case Redirect.SAFETY:
                startActivity(new Intent(MainActivity.this, MapActivity.class));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mDrawerLayout.closeDrawer(GravityCompat.START, false);
        selectUserInfo();
    }

    private void selectUserInfo() {
        mUserName.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREF_USER_NAME, ""));
        if (TextUtils.isEmpty(mUserName.getText())) {
            mUserName.setText("여기를 클릭 후 성명을 입력해 주세요");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupViewPager(ViewPager viewPager) {
        if(helper_id == null)
            helper_id = "false";
        android.support.v4.app.Fragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("helper_id", helper_id);
        homeFragment.setArguments(args);

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mainPagerAdapter.addFragment(homeFragment, getResources().getString(R.string.main_tab1_title));
        mainPagerAdapter.addFragment(new FileBoxFragment(), getResources().getString(R.string.rtc_tab1_title));
        viewPager.setAdapter(mainPagerAdapter);
    }

    public void checkPermissions() {

        int APIVersion = Build.VERSION.SDK_INT;
        if (APIVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(this, PermissionActivity.class);
                startActivity(intent);
            }
        }
    }

    private String getRegistrationId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this); // 이전에 저장해둔 등록 아이디를 SharedPreferences에서 가져온다.
        String registrationId = prefs.getString(Constants.PROPERTY_REG_ID, ""); // 저장해둔 등록 아이디가 없으면 빈 문자열을 반환한다.
        if (registrationId.isEmpty()) {
            return "";
        }

        // 앱이 업데이트 되었는지 확인하고, 업데이트 되었다면 기존 등록 아이디를 제거한다.
        // 새로운 버전에서도 기존 등록 아이디가 정상적으로 동작하는지를 보장할 수 없기 때문이다.
        int registeredVersion = prefs.getInt(Constants.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        Log.e("SSSSS", "" + currentVersion + "  " + registeredVersion);
        if (registeredVersion != currentVersion) { // 이전에 등록 아이디를 저장한 앱의 버전과 현재 버전을 비교해 버전이 변경되었으면 빈 문자열을 반환한다.
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    // reg id 발급
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(Constants.SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    Log.e("SSSSS", msg);

                    // 등록 아이디를 저장해 등록 아이디를 매번 받지 않도록 한다.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.e("SSSSS", msg);
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }

        }.execute(null, null, null);
    }

    // SharedPreferences에 발급받은 등록 아이디를 저장해 등록 아이디를 여러 번 받지 않도록 하는 데 사용
    private void storeRegistrationId(Context context, String regid) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PROPERTY_REG_ID, regid);
        editor.putInt(Constants.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}