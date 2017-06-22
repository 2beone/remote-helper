package net.twobeone.remotehelper.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.db.model.UserInfo;
import net.twobeone.remotehelper.widget.RoundImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;
    private ViewPager mViewPager;
    private FragmentManager fm;
    private FragmentTransaction fragmentTransaction;

    private TextView mUserName;
    private RoundImageView mUserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        View nav_hear_view = navigationView.getHeaderView(0);
        mUserName = nav_hear_view.findViewById(R.id.userage_txt);
        mUserImage = nav_hear_view.findViewById(R.id.user_img);
        mUserImage.setImageResource(R.drawable.user_default);
        Realm.init(this);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager(mViewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Button userBtn = nav_hear_view.findViewById(R.id.btn_user);
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                startActivity(intent);

                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mDrawerLayout.closeDrawer(GravityCompat.START, false);
        Realm realm = Realm.getDefaultInstance();
        UserInfo userInfo = realm.where(UserInfo.class).findFirst();
        if (userInfo != null) {
            if (userInfo.getImgPath() != null) {
                mUserImage.setImageResource(R.drawable.user_default);
                File img = new File(userInfo.getImgPath());
                Uri uri = Uri.fromFile(img);
                mUserImage.setImageURI(uri);
            }
            if (userInfo.getName() != null) {
                mUserName.setText(userInfo.getName());
            }
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
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), getResources().getString(R.string.main_tab1_title));
        adapter.addFragment(new DownloadDataFragment(), getResources().getString(R.string.rtc_tab1_title));
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_safezone:
                                startActivity(new Intent(MainActivity.this, SafetyZoneActivity.class));
                                break;
                            case R.id.nav_settings:
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {
//        try {
//            fm = getSupportFragmentManager();
//            fragmentTransaction = fm.beginTransaction();
//
//            if(fm.equals("")){
//                super.onBackPressed();
//            }
//
//            if (!fm.findFragmentByTag("rtcfragment").isRemoving()) {
//                fragmentTransaction.remove(fm.findFragmentByTag("rtcfragment"));
//                fragmentTransaction.commit();
//            } else if (!fm.findFragmentByTag("msginfofragment").isRemoving()){
//                fragmentTransaction.remove(fm.findFragmentByTag("msginfofragment"));
//                fragmentTransaction.commit();
//            }
//
//        } catch (Exception e) {
//
//        }
    }

    static class Adapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
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

                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.GET_ACCOUNTS },
                        100);

            }

        }
    }

    @SuppressLint("Override")
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onResume();

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.GET_ACCOUNTS},
                            100);

                }
                return;
            }
            default: {

                return;
            }
        }
    }
}