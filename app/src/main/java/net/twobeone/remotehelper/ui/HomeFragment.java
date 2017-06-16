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

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import net.twobeone.remotehelper.R;

/**
 * Created by Administrator on 2017-06-16.
 */

public class HomeFragment extends Fragment {

    private View view;
    private ImageButton sos_button;
    private Fragment fragment;
    private FragmentManager fm;
    private FragmentTransaction fragmentTransaction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        sos_button = (ImageButton) getActivity().findViewById(R.id.sos);
        sos_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    fragment = new HomeRtcFragment();
                    fm = getActivity().getFragmentManager();
                    fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.switchfragment, fragment, "rtcfragment");
                    fragmentTransaction.commit();
            }
        });
    }
}
