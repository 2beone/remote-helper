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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;

import net.twobeone.remotehelper.R;

/**
 * Created by Administrator on 2017-04-27.
 */

public class MainActivity extends Activity {

    private Fragment fragment;
    private Fragment homefragment;
    private FragmentManager fm;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        homefragment = new HomeFragment();
        fragment = new HomeRtcFragment();

        fm = getFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.switchfragment, homefragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(!fm.findFragmentByTag("rtcfragment").isRemoving()){
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.replace(R.id.switchfragment, homefragment);
            fragmentTransaction.commit();
        }else{
            super.onBackPressed();
        }
    }
}
