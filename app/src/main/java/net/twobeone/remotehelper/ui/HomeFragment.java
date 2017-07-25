package net.twobeone.remotehelper.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.databinding.FragmentHomeBinding;
import net.twobeone.remotehelper.util.LocationUtils;
import net.twobeone.remotehelper.util.UserUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {

    private View view;
    private Button sos_button;
    private Fragment fragment;
    private FragmentManager fm;
    private FragmentTransaction fragmentTransaction;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private FragmentHomeBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerViewAdapter = new RecyclerViewAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        view = mBinding.getRoot();

        sos_button = (Button) view.findViewById(R.id.btn_call);
        sos_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 사용자를 체크합니다.
                if (!UserUtils.isRegisted(getActivity())) {
                    Toast.makeText(getContext(), "먼저 사용자 정보를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

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

        if (!"false".equals(getArguments().getString("helper_id"))) {
            sos_button.setEnabled(false);
            Handler h = new Handler();
            h.postDelayed(new splashhandler(), 3000);

            Bundle args = new Bundle();
            args.putString("isMute", "false");
            args.putString("helper_id", getArguments().getString("helper_id"));
            fragment = new HomeRtcFragment();
            fm = getFragmentManager();
            fragment.setArguments(args);
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.home_fragment, fragment, "rtcfragment");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerViewAdapter.selectItems();
    }

    class splashhandler implements Runnable {
        public void run() {
            sos_button.setEnabled(true);
        }
    }

    public static final class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private final Context mContext;
        private List<ChatMessage> mItems;

        public RecyclerViewAdapter(Context context) {
            mContext = context;
            mItems = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ChatMessage item = mItems.get(position);
            holder.tvMessageContent.setText(item.message);
            holder.intent = item.intent;
        }

        @Override
        public int getItemCount() {
            return mItems == null ? 0 : mItems.size();
        }

        public void selectItems() {
            mItems.clear();

            // 첫인사
            mItems.add(new ChatMessage("안녕하세요? 원격안전도우미입니다."));

            // 상담원연결
            mItems.add(new ChatMessage("저의 도움이 필요하시면 아래의 '상담원 연결' 버튼을 클릭해 주세요."));

            // 위치서비스
            if (!LocationUtils.isLocationEnabled(mContext)) {
                ChatMessage item = new ChatMessage("위치서비스를 '사용'으로 설정하시면 더욱 다양한 서비스를 이용하실 수 있습니다. 여기를 클릭해 주세요.");
                item.intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mItems.add(item);
            } else {
                mItems.add(new ChatMessage("위치서비스(안전지대 등)를 이용하실 수 있습니다."));
            }

            notifyDataSetChanged();
        }

        static final class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvMessageContent;
            Intent intent;

            public ViewHolder(View itemView) {
                super(itemView);
                tvMessageContent = (TextView) itemView.findViewById(R.id.tv_message_content);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (intent != null) {
                            v.getContext().startActivity(intent);
                        }
                    }
                });
            }
        }

        private class ChatMessage {

            String message;
            Intent intent;

            ChatMessage(String message) {
                this.message = message;
            }
        }
    }
}
