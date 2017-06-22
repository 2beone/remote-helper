package net.twobeone.remotehelper.ui;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.ui.adapter.MSG_Item;
import net.twobeone.remotehelper.ui.adapter.MSG_Item_Adapter;
import net.twobeone.remotehelper.util.StringUtils;

import java.io.File;
import java.util.Arrays;

public class DownloadDataFragment extends Fragment {

    private ListView mListview;
    private MSG_Item_Adapter mAdapter;

    private static final String[] VIDEO_EXTENSIONS = {"MP4"};
    private static final String[] IMAGE_EXTENSIONS = {"JPG", "GIF", "PNG", "BMP"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MSG_Item_Adapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_data, container, false);

        for (File file : makeDirectoryIfNotExists(String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.DONWLOAD_DIRECTORY_NAME)).listFiles()) {
            String extension = getExtension(file);
            if (!StringUtils.isNullOrEmpty(extension)) {
                mAdapter.addItem(ContextCompat.getDrawable(getActivity(), getIcon(extension)), file.getName(), extension);
            }
        }

        mListview = view.findViewById(R.id.msg_list);
        mListview.setAdapter(mAdapter);
        mListview.setVisibility(mAdapter.getCount() != 0 ? View.VISIBLE : View.INVISIBLE);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                MSG_Item item = (MSG_Item) parent.getItemAtPosition(position);
                String msg_name = item.getTitle();
                String msg_extend = item.getExtend();

//                Intent intent = new Intent(getActivity().getApplicationContext(), MSG_Info_View.class);
//                intent.putExtra("msg", msg_name);
//                intent.putExtra("extend", msg_extend);
//                startActivity(intent);
            }
        });

        view.findViewById(R.id.msg_text).setVisibility(mListview.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private int getIcon(String extension) {
        if (Arrays.asList(VIDEO_EXTENSIONS).contains(extension)) {
            return R.drawable.ico_moving;
        } else if (Arrays.asList(IMAGE_EXTENSIONS).contains(extension)) {
            return R.drawable.ico_picture;
        }
        return R.drawable.ico_text;
    }

    private String getExtension(File file) {
        String fileName = file.getName();
        if (file.isDirectory() || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
    }

    private File makeDirectoryIfNotExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
