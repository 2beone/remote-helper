package net.twobeone.remotehelper.ui;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.util.FileUtils;
import net.twobeone.remotehelper.util.PermissionUtils;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class FileBoxFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private TextView mTvNoData;

    private static Fragment fragment;
    private static FragmentManager fm;
    private static long backKeyPressedTime = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerViewAdapter = new RecyclerViewAdapter(getContext());

        fragment = new MsgInfoFragment();
        fm = getFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_download_data, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mTvNoData = (TextView) view.findViewById(R.id.msg_text);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerViewAdapter.selectItems();
        mRecyclerView.setVisibility(mRecyclerViewAdapter.getItemCount() == 0 ? View.INVISIBLE : View.VISIBLE);
        mTvNoData.setVisibility(mRecyclerView.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
    }

    public static final class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private final Context mContext;
        private final File mDirectory;
        private List<File> mFiles;

        public RecyclerViewAdapter(Context context) {
            mContext = context;
            mDirectory = FileUtils.makeDirectoryIfNotExists(String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.DONWLOAD_DIRECTORY_NAME));
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final File file = mFiles.get(position);
            holder.ivFileType.setImageDrawable(getFileTypeDrawble(file));
            holder.tvFileName.setText(file.getName());
            holder.tvFileSize.setText(FileUtils.getSizeName(file));
            holder.tvFileDuration.setVisibility(FileUtils.isVideoFile(file) ? View.VISIBLE : View.GONE);
            if (holder.tvFileDuration.getVisibility() == View.VISIBLE) {
                holder.tvFileDuration.setText(String.format("재생 시간 %d초", getMediaDuration(file)));
            }
        }

        public long getMediaDuration(File file) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                retriever.setDataSource(mContext, Uri.fromFile(file));
                return Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (retriever != null) {
                    retriever.release();
                }
            }
            return 0;
        }

        @Override
        public int getItemCount() {
            return mFiles == null ? 0 : mFiles.size();
        }

        public void selectItems() {
            if (PermissionUtils.hasPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                mFiles = new LinkedList<>(Arrays.asList(mDirectory.listFiles()));
                for (int i = mFiles.size() - 1; i >= 0; i--) {
                    File file = mFiles.get(i);
                    if (file.isDirectory() || file.length() == 0) {
                        mFiles.remove(file);
                    }
                }
                notifyDataSetChanged();
            }
        }

        private Drawable getFileTypeDrawble(File file) {
            if ("PDF".equals(FileUtils.getExtension(file))) {
                return ContextCompat.getDrawable(mContext, R.drawable.file_pdf);
            } else if (FileUtils.isVideoFile(file)) {
                return ContextCompat.getDrawable(mContext, R.drawable.file_video);
            } else if (FileUtils.isImageFile(file)) {
                return ContextCompat.getDrawable(mContext, R.drawable.file_picture);
            }
            return ContextCompat.getDrawable(mContext, R.drawable.file_text);
        }

        static final class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivFileType;
            TextView tvFileName;
            TextView tvFileSize;
            TextView tvFileDuration;
            FragmentTransaction fragmentTransaction;

            public ViewHolder(View view) {
                super(view);
                ivFileType = (ImageView) view.findViewById(R.id.iv_file_type);
                tvFileName = (TextView) view.findViewById(R.id.tv_file_name);
                tvFileSize = (TextView) view.findViewById(R.id.tv_file_size);
                tvFileDuration = (TextView) view.findViewById(R.id.tv_file_deration);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                            backKeyPressedTime = System.currentTimeMillis();
                            Bundle args = new Bundle();
                            int sub = tvFileName.getText().toString().indexOf(".");
                            args.putString("name", tvFileName.getText().toString().substring(0, sub));
                            args.putString("extend", tvFileName.getText().toString().substring(sub));
                            fragment.setArguments(args);
                            fragmentTransaction = fm.beginTransaction();
                            fragmentTransaction.replace(R.id.download_data, fragment, "msginfofragment");
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {

                        }
                    }
                });
            }
        }
    }
}
