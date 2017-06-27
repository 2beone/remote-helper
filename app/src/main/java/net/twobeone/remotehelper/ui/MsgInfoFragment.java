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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2017-06-22.
 */

public class MsgInfoFragment extends Fragment {

    private View view;

    private TextView recv_info;
    private TextView recv_title;
    private ImageView recv_img;
    private VideoView recv_vid;
    private Button recv_back;
    private String name;
    private String name_extend;
    private File file;
    private String strPath = null;
    private BufferedReader bufferedReader = null;
    private Uri fileUri;
    private WebView google_doc;
    private String ServerUrl;
    private PhotoViewAttacher mAttacher;

    private FragmentManager fm;
    private FragmentTransaction fragmentTransaction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_msginfo, container, false);

        recv_info = (TextView) view.findViewById(R.id.recv_info);
        recv_img = (ImageView) view.findViewById(R.id.recv_img);
        recv_vid = (VideoView) view.findViewById(R.id.recv_vid);
        recv_back = (Button) view.findViewById(R.id.recv_back);
        recv_title = (TextView) view.findViewById(R.id.recv_title);
        google_doc = (WebView) view.findViewById(R.id.google_doc);

        WebSettings webSettings = google_doc.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setBuiltInZoomControls(true);

        google_doc.setWebChromeClient(new WebChromeClient());

        recv_img.setScaleType(ImageView.ScaleType.FIT_XY);

        name = getArguments().getString("name");
        name_extend = getArguments().getString("extend");

        recv_title.setText(name);
        recv_info.setText("");

        showDownloadFile();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recv_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                google_doc.destroy();
                getActivity().onBackPressed();
            }
        });
    }

    private void showDownloadFile() {
        FileInputStream fis;
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Constants.DONWLOAD_DIRECTORY_NAME + "/" + name
                + name_extend);

        // 파일 확장자 별로 mime type 지정해 준다.
        if (name_extend.equals(".mp4")) {
            recv_vid.setVisibility(recv_vid.VISIBLE);
            fileUri = Uri.fromFile(file);
            recv_vid.setVideoURI(fileUri);
            recv_vid.requestFocus();
            recv_vid.start();
        } else if (name_extend.equals(".jpg") || name_extend.equals(".jpeg") || name_extend.equals(".JPG")
                || name_extend.equals(".gif") || name_extend.equals(".png") || name_extend.equals(".bmp")) {
            recv_img.setVisibility(recv_img.VISIBLE);

            BitmapFactory.Options bo = new BitmapFactory.Options();
            Bitmap bmp = BitmapFactory.decodeFile(file.getPath(), bo);
            recv_img.setImageBitmap(bmp);
            mAttacher = new PhotoViewAttacher(recv_img);
        } else if (name_extend.equals(".txt")) {
            try {
                fis = new FileInputStream(file);
                bufferedReader = new BufferedReader(new InputStreamReader(fis));
                StringBuffer buf = new StringBuffer();

                while ((strPath = bufferedReader.readLine()) != null) {
                    buf.append(strPath + "\n");
                }

                fis.close();
                recv_info.setVisibility(recv_info.VISIBLE);
                recv_info.setText(buf.toString());
            } catch (Exception e) {
                // TODO: handle exception
            }
        } else if (name_extend.equals(".pdf")) {
            google_doc.setVisibility(google_doc.VISIBLE);
            google_doc.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + file.getAbsolutePath() + "#zoom=page-width");
        }
    }
}
