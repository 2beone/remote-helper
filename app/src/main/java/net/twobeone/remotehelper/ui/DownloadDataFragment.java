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

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.ui.adapter.MSG_Item;
import net.twobeone.remotehelper.ui.adapter.MSG_Item_Adapter;

import java.io.File;

/**
 * Created by Administrator on 2017-06-20.
 */

public class DownloadDataFragment extends Fragment {

    private TextView mTextView;
    private ListView mListview;
    private MSG_Item_Adapter mAdapter;

    private String path;
    private File[] fileList;
    private File list;
    private int sub = 0;
    private String filename = null;
    private String fileextend = null;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_data, container, false);

        mAdapter = new MSG_Item_Adapter();

        mTextView = (TextView) view.findViewById(R.id.msg_text);
        mListview = (ListView) view.findViewById(R.id.msg_list);
        mListview.setAdapter(mAdapter);

        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RemoteHelper_download/";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }

        list = new File(path);
        fileList = list.listFiles();
        if (fileList.length > 0) {
            for (int i = 0; i < fileList.length; i++) {
                sub = fileList[i].getName().lastIndexOf(".");
                filename = fileList[i].getName().substring(0, sub);
                fileextend = fileList[i].getName().substring(sub);

                if (fileextend.equals(".mp4")) {

                    mAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ico_moving), filename, fileextend);

                } else if (fileextend.equals(".jpg") || fileextend.equals(".jpeg") || fileextend.equals(".JPG")
                        || fileextend.equals(".gif") || fileextend.equals(".png") || fileextend.equals(".bmp")) {

                    mAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ico_picture), filename, fileextend);

                } else {
                    mAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ico_text), filename, fileextend);
                }

            }
        } else {
            mTextView.setVisibility(View.VISIBLE);
            mListview.setVisibility(View.INVISIBLE);
        }

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                MSG_Item item = (MSG_Item) parent.getItemAtPosition(position);
                String msg_name = item.getTitle();
                String msg_extend = item.getExtend();
                Log.e("JH", "::::" + fileextend);

//                Intent intent = new Intent(getActivity().getApplicationContext(), MSG_Info_View.class);
//                intent.putExtra("msg", msg_name);
//                intent.putExtra("extend", msg_extend);
//                startActivity(intent);
            }
        });

        return view;
    }
}
