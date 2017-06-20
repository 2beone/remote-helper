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

package net.twobeone.remotehelper.ui;
//
//import android.app.Activity;
//import android.app.Fragment;
//import android.app.FragmentManager;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;

//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.Window;
//
//import net.twobeone.remotehelper.R;
//
///**
// * Created by Administrator on 2017-04-27.
// */
//
//public class MainActivity extends Activity {
//
//    private Fragment fragment;
//    private Fragment homefragment;
//    private FragmentManager fm;
//    private FragmentTransaction fragmentTransaction;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_main);
//
//        homefragment = new HomeFragment();
//        fragment = new HomeRtcFragment();
//
//        fm = getFragmentManager();
//        fragmentTransaction = fm.beginTransaction();
//        fragmentTransaction.add(R.id.switchfragment, homefragment);
//        fragmentTransaction.commit();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if(!fm.findFragmentByTag("rtcfragment").isRemoving()){
//            fragmentTransaction = fm.beginTransaction();
//            fragmentTransaction.remove(fragment);
//            fragmentTransaction.replace(R.id.switchfragment, homefragment);
//            fragmentTransaction.commit();
//        }else{
//            super.onBackPressed();
//        }
//    }
//}

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ViewPager mViewPager;
    private FragmentManager fm;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        RoundImageView imageView = (RoundImageView) nav_hear_view.findViewById(R.id.user_img);
        imageView.setImageResource(R.drawable.user_default);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager(mViewPager);
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
        adapter.addFragment(new Tab2Fragment(), getResources().getString(R.string.rtc_tab1_title));
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        try{
            fm = getSupportFragmentManager();
            if(!fm.findFragmentByTag("rtcfragment").isRemoving()){
                fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.remove(fm.findFragmentByTag("rtcfragment"));
                fragmentTransaction.commit();
            }else{
                super.onBackPressed();
            }
        }catch (Exception e){

        }
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

}