package net.twobeone.remotehelper.ui;

//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.twobeone.remotehelper.R;

/**
 * Created by Administrator on 2017-06-16.
 */

public class HomeFragment extends Fragment {

    private View view;
    private Button sos_button;
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
        sos_button = (Button) view.findViewById(R.id.btn_call);
        sos_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    fragment = new HomeRtcFragment();
                    fm = getFragmentManager();
                    fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.home_fragment, fragment, "rtcfragment");
                    fragmentTransaction.commit();
            }
        });
    }
}
