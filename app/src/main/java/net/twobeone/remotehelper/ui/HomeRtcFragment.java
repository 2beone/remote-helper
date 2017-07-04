package net.twobeone.remotehelper.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.util.GPSInfo;
import net.twobeone.remotehelper.util.WebRTCParams;
import net.twobeone.remotehelper.util.WebRTCSocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.MediaStream;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeRtcFragment extends BaseFragment implements WebRTCSocket.RtcListener, SurfaceHolder.Callback {

    private static final String VIDEO_CODEC_VP8 = "VP8";
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String AUDIO_CODEC_OPUS = "opus";
    // Local preview screen position before call is connected.
    private static final int LOCAL_X_CONNECTING = 0;
    private static final int LOCAL_Y_CONNECTING = 0;
    private static final int LOCAL_WIDTH_CONNECTING = 100;
    private static final int LOCAL_HEIGHT_CONNECTING = 100;
    private VideoRendererGui.ScalingType scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;
    private GLSurfaceView vsv = null;
    private VideoRenderer.Callbacks localRender = null;
    private String mSocketAddress;
    private WebRTCSocket clientWebSocket = null;
    private View view;

    private String Save_Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RemoteHelper_download/";

    private Camera cam = null;
    private MediaRecorder mediaRecorder = null;
    private boolean recording = false;
    private String save_name = System.currentTimeMillis() + "";
    private SurfaceHolder sh;
    private SurfaceView sv;

    private ImageButton mute_button;
    private ImageButton change_camera;
    private ImageButton change_voice;
    private ImageButton change_video;
    private ImageButton hangup;
    private ImageView voice_img;
    private boolean mutests = false;
    private Button sos_button;
    private Runnable runnable = null;

    private static Fragment fragment;
    private static FragmentManager fm;
    private FragmentTransaction fragmentTransaction;

    private String iceStatus = "";

    private GPSInfo gps;
    private double latitude;
    private double longitude;
    private String userName = "";

    private String ServerUrl = "";
    private String FileName = "";
    private int sub;
    private String FileExtend = "";
    private String LocalPath = "";
    private MediaPlayer music;
    private String mDeviceID;
    private ViewGroup viewGroupParent;

    private void setting() {
        cam = Camera.open(1);
        cam.setDisplayOrientation(90);
        sh = sv.getHolder();
        sh.addCallback(this);
        sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeviceID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rtc, container, false);

        viewGroupParent = container;

        getActivity().getWindow().addFlags(
                LayoutParams.FLAG_KEEP_SCREEN_ON
                        | LayoutParams.FLAG_DISMISS_KEYGUARD
                        | LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | LayoutParams.FLAG_TURN_SCREEN_ON);
        mSocketAddress = "wss://remohelper.com:9090";
        Log.e("SSSSS", "onCreateview");

        sos_button = (Button) getActivity().findViewById(R.id.btn_call);
        sos_button.setVisibility(sos_button.INVISIBLE);

        vsv = (GLSurfaceView) view.findViewById(R.id.glview_call);
        vsv.setPreserveEGLContextOnPause(true);
        vsv.setKeepScreenOn(true);
        sv = (SurfaceView) view.findViewById(R.id.preview);

        music = MediaPlayer.create(getContext(), R.raw.test);
        music.setLooping(false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        userName = prefs.getString(Constants.PREF_USER_NAME, null);

        VideoRendererGui.setView(vsv, runnable = new Runnable() {
            @Override
            public void run() {
                init();
            }
        });

        localRender = VideoRendererGui.create(
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mute_button = (ImageButton) view.findViewById(R.id.mute_button);
        change_camera = (ImageButton) view.findViewById(R.id.change_camera);
        change_voice = (ImageButton) view.findViewById(R.id.change_voice);
        change_video = (ImageButton) view.findViewById(R.id.change_video);
        hangup = (ImageButton) view.findViewById(R.id.hangup);
        voice_img = (ImageView) view.findViewById(R.id.voice_img);

        mute_button.setOnClickListener(onClickListener);
        change_camera.setOnClickListener(onClickListener);
        change_voice.setOnClickListener(onClickListener);
        change_video.setOnClickListener(onClickListener);
        hangup.setOnClickListener(onClickListener);

        gps = new GPSInfo(getContext());
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            // GPS 를 사용할수 없으므로 좌표 0
            latitude = 0;
            longitude = 0;
        }

        if (getArguments().getString("isMute").equals("false")) {
            mutests = false;
            mute_button.setBackgroundResource(R.drawable.btn_mute_on);
        } else {
            mutests = true;
            mute_button.setBackgroundResource(R.drawable.btn_mute_off);
        }
    }

    ImageButton.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.mute_button:
                    if (mutests) {
                        mutests = false;
                        mute_button.setBackgroundResource(R.drawable.btn_mute_on);
                    } else {
                        mutests = true;
                        mute_button.setBackgroundResource(R.drawable.btn_mute_off);
                    }
                    clientWebSocket.onMute(mutests);
                    break;
                case R.id.change_camera:
                    clientWebSocket.onChangeCamera();
                    break;
                case R.id.change_voice:
                    clientWebSocket.onChangeVoice(true);
                    vsv.setVisibility(vsv.INVISIBLE);
                    voice_img.setVisibility(voice_img.VISIBLE);
                    change_voice.setVisibility(change_voice.INVISIBLE);
                    change_video.setVisibility(change_video.VISIBLE);
                    break;
                case R.id.change_video:
                    clientWebSocket.onChangeVoice(false);
                    vsv.setVisibility(vsv.VISIBLE);
                    voice_img.setVisibility(voice_img.INVISIBLE);
                    change_voice.setVisibility(change_voice.VISIBLE);
                    change_video.setVisibility(change_video.INVISIBLE);
                    break;
                case R.id.hangup:
                    getActivity().onBackPressed();
                    break;
            }
        }
    };

    private void init() {
        Point displaySize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(displaySize);
        WebRTCParams params = new WebRTCParams(
                true, false, 640, 360, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        clientWebSocket = new WebRTCSocket(getActivity(), this, mSocketAddress, params, VideoRendererGui.getEGLContext(),
                getAddress(getContext(), latitude, longitude), latitude, longitude, userName, prefs.getString(Constants.PROPERTY_REG_ID, ""), mDeviceID);
    }

    @Override
    public void onPause() {
        if (clientWebSocket != null) {
            clientWebSocket.onPause();
        }
        vsv.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        if (clientWebSocket != null) {
            clientWebSocket.onResume();
        }
        vsv.onResume();
        super.onResume();
    }

    @Override
    public void onDetach() {
        Log.e("SSSSS", "Here6");
        localRender = null;
        VideoRendererGui.remove(localRender);
        sos_button.setVisibility(sos_button.VISIBLE);
        if (!clientWebSocket.mWebSocketClient.isClosed()) {
            Log.e("SSSSS", "clientWebSocket disconnect");
            clientWebSocket.clearSocket();
            clientWebSocket.mWebSocketClient.close();
        }
        clientWebSocket = null;
        handler.removeCallbacks(runnable);

        if (recording) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                recording = false;
                cam.stopPreview();
                cam.release();
            } catch (Exception e) {
                Log.e("JH", "CAM " + e.toString());
            }
            File file = new File(Save_Path + save_name + ".mp4");
            file.delete();
        }
        super.onDetach();
    }

    @Override
    public void onStatusChanged(final String newStatus) {
        if (newStatus.equals("CONNECTING")) {
            handler.sendEmptyMessage(3);
        }
        iceStatus = newStatus;
        handler.sendEmptyMessage(2);
    }

    @Override
    public void onClose() {
        handler.sendEmptyMessage(4);
    }

    @Override
    public void onLocalStream(MediaStream localStream) {
        localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType, false);
    }

    @Override
    public void onStartRecording() {
        VideoRendererGui.remove(localRender);
        handler.sendEmptyMessage(0);
        if (!recording) {
            setting();
            File dir = new File(Save_Path);

            if (!dir.exists()) {
                dir.mkdir();
            }
            try {
                mediaRecorder = new MediaRecorder();
                cam.lock();
                cam.unlock();
                refreshCamera(cam);
                mediaRecorder.setCamera(cam);
                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);// H264
                mediaRecorder.setOrientationHint(270);
                mediaRecorder.setOutputFile(Save_Path + save_name + ".mp4");
                mediaRecorder.setPreviewDisplay(sh.getSurface());
                mediaRecorder.prepare();
                mediaRecorder.start();
                recording = true;
                handler.sendEmptyMessage(1);
            } catch (final Exception ex) {
                Log.e("JH", ex.toString());
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                recording = false;
                try {
                    cam.stopPreview();
                    cam.release();
                } catch (Exception e) {

                }
                getActivity().onBackPressed();
            }
        }
    }

    @Override
    public void downloadThread(String serverPath, String localPath, String filename) {
        ServerUrl = serverPath;
        FileName = filename;
        sub = serverPath.lastIndexOf(".");
        FileExtend = serverPath.substring(sub);
        LocalPath = localPath + FileExtend;

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Log.e("SSSSS", "filedownload");

                URL fileurl;
                int Read;
                try {
                    fileurl = new URL(ServerUrl);
                    HttpURLConnection conn = (HttpURLConnection) fileurl.openConnection();
                    byte[] tmpByte = new byte[1024];
                    InputStream is = conn.getInputStream();
                    File file = new File(LocalPath);

                    if (!file.exists())
                        file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    for ( ; ; ) {
                        Read = is.read(tmpByte);
                        if (Read <= 0) {
                            break;
                        }
                        fos.write(tmpByte, 0, Read);
                    }
                    is.close();
                    fos.close();
                    conn.disconnect();

                    handler.sendEmptyMessage(5);
                } catch (MalformedURLException e) {
                    Log.e("ERROR1", e.getMessage());
                } catch (IOException e) {
                    Log.e("ERROR2", e.getMessage());
                }
            }
        }).start();
    }

    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    mute_button.setVisibility(mute_button.INVISIBLE);
                    change_voice.setVisibility(change_voice.INVISIBLE);
                    change_camera.setVisibility(change_camera.INVISIBLE);
                    hangup.setVisibility(hangup.INVISIBLE);
                    sv.setVisibility(sv.VISIBLE);
                    vsv.setVisibility(vsv.INVISIBLE);
                    break;
                case 1:
                    new CountDownTimer(5000, 500) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            if (recording) {
                                try {
                                    mediaRecorder.stop();
                                    mediaRecorder.release();
                                    mediaRecorder = null;
                                    recording = false;
                                    cam.stopPreview();
                                    cam.release();
                                } catch (Exception e) {
                                    Log.e("JH", "CAM " + e.toString());
                                }

                                // 서버로 녹화한 영상 전송
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        fileUpload();
                                        File file = new File(Save_Path + save_name + ".mp4");
                                        file.delete();
                                    }
                                }).start();
                                onClose();
                            }
                        }
                    }.start();
                    break;
                case 2:
                    Toast.makeText(getActivity().getApplicationContext(), iceStatus, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    try {
                        new CountDownTimer(2000, 500) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                try {
                                    clientWebSocket.onMute(mutests);
                                } catch (Exception e) {

                                }
                            }
                        }.start();
                    } catch (Exception e) {
                        Log.e("SSSSS", e.toString());
                    }
                    break;
                case 4:
                    getActivity().onBackPressed();
                    break;
                case 5:
                    DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            fragment = new MsgInfoFragment();
                            fm = getFragmentManager();
                            Bundle args = new Bundle();
                            args.putString("name", FileName);
                            args.putString("extend", FileExtend);
                            fragment.setArguments(args);
                            fragmentTransaction = fm.beginTransaction();
                            fragmentTransaction.replace(R.id.rtc_fragment, fragment, "msginfofragment");
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    };
                    DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    };

                    new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_LIGHT)
                            .setTitle("안전도우미가 전송한 메시지가 도착하였습니다.").setPositiveButton("확인", okListener)
                            .setNegativeButton("취소", cancelListener).show();
                    break;
                case 6:
                    vsv.setVisibility(vsv.INVISIBLE);
                    voice_img.setVisibility(voice_img.VISIBLE);
                    mute_button.setVisibility(mute_button.INVISIBLE);
                    change_voice.setVisibility(change_voice.INVISIBLE);
                    change_camera.setVisibility(change_camera.INVISIBLE);
                    hangup.setVisibility(hangup.INVISIBLE);

                    try {
                        music.prepare();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    music.seekTo(0);
                    music.start();

                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    if (music.isPlaying()) {
                                        music.stop();

                                        sos_button.setVisibility(sos_button.VISIBLE);
                                        viewGroupParent.removeView(view);
                                        fm = getFragmentManager();
                                        fragmentTransaction = fm.beginTransaction();
                                        fragmentTransaction.remove(fm.findFragmentByTag("rtcfragment"));
                                        fragmentTransaction.commitAllowingStateLoss();
                                    }
                                }
                            });
                        }
                    };
                    timer.schedule(timerTask, music.getDuration() - 500);
                    break;
            }
        }
    };

    public void fileUpload() {

        String pathToOurFile = save_name + ".mp4";
        String urlServer = "https://remohelper.com:440/m/websocket/getValue.do";


        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            MediaType media_type_video = MediaType.parse("video/mp4");
            File file = new File(Save_Path + pathToOurFile);
            OkHttpClient client = new OkHttpClient();

            FormBody.Builder builder = new FormBody.Builder();

            JSONObject obj = new JSONObject();
            try {
                obj.put("fileName", pathToOurFile);
                obj.put("date", save_name);
                obj.put("userName", userName);
                obj.put("getRegId", prefs.getString(Constants.PROPERTY_REG_ID, ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", pathToOurFile, RequestBody.create(media_type_video, file))
                    .addFormDataPart("sariodId", mDeviceID/* regid */).addFormDataPart("type", "S")
                    .addFormDataPart("param", obj.toString()).build();

            builder.build();
            Request request = new Request.Builder().url(urlServer).post(requestBody).build();
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if (cam == null) {
                cam.setPreviewDisplay(surfaceHolder);
                cam.startPreview();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        refreshCamera(cam);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    public void refreshCamera(Camera camera) {
        if (sh.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            cam.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            Log.e("SSSSS", "Preview?!? " + e.toString());
        }
        setCamera(camera);
        try {
            cam.setPreviewDisplay(sh);
            cam.startPreview();
        } catch (Exception e) {
            Log.e("SSSSS", "Preview?? " + e.toString());
        }
    }

    public void setCamera(Camera camera) {
        // method to set a camera instance
        cam = camera;
    }

    public static String getAddress(Context mContext, double lat, double lng) {
        String nowAddress = "현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;
        try {
            if (lat == 0 && lng == 0) {
            } else {
                if (geocoder != null) {
                    address = geocoder.getFromLocation(lat, lng, 1);
                    // 세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로 한좌표에 대해 두개이상의 이름이 존재할수있기에 리턴 최대값 설정

                    if (address != null && address.size() > 0) {
                        // 주소 받아오기
                        String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                        int sub = currentLocationAddress.indexOf(" ");// 대한민국 제거
                        nowAddress = currentLocationAddress.substring(sub + 1);
                    }
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        return nowAddress;
    }

    @Override
    public void onLeave() {
        handler.sendEmptyMessage(6);
    }
}