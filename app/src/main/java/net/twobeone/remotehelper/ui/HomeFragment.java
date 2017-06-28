package net.twobeone.remotehelper.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.twobeone.remotehelper.R;

public class HomeFragment extends BaseFragment {

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        sos_button = (Button) view.findViewById(R.id.btn_call);
        sos_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //중복 클릭 방지
                        sos_button.setEnabled(false);
                        Handler h = new Handler();
                        h.postDelayed(new splashhandler(), 3000);

                        Bundle args = new Bundle();
                        args.putString("isMute", "false");
                        fragment = new HomeRtcFragment();
                        fm = getFragmentManager();
                        fragment.setArguments(args);
                        fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.replace(R.id.home_fragment, fragment, "rtcfragment");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        dialog.dismiss();
                    }
                };
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("안전도우미와 연결하시겠습니까?").setNegativeButton("취소", cancelListener)
                        .setPositiveButton("확인", okListener).show();
            }
        });

        sos_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //중복 클릭 방지
                sos_button.setEnabled(false);
                Handler h = new Handler();
                h.postDelayed(new splashhandler(), 3000);

                Bundle args = new Bundle();
                args.putString("isMute", "true");
                fragment = new HomeRtcFragment();
                fm = getFragmentManager();
                fragment.setArguments(args);
                fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.home_fragment, fragment, "rtcfragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                return false;
            }
        });
    }

    class splashhandler implements Runnable {
        public void run() {
            sos_button.setEnabled(true);
        }
    }
}
