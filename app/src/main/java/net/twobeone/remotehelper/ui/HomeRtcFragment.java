package net.twobeone.remotehelper.ui;

//import android.app.Fragment;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.webrtc.PeerConnectionParameters;
import net.twobeone.remotehelper.webrtc.WebRTCClientWebSocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.MediaStream;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeRtcFragment extends Fragment implements WebRTCClientWebSocket.RtcListener, SurfaceHolder.Callback {

    private final static int VIDEO_CALL_SENT = 666;
    private static final String VIDEO_CODEC_VP8 = "VP8";
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String AUDIO_CODEC_OPUS = "opus";
    // Local preview screen position before call is connected.
    private static final int LOCAL_X_CONNECTING = 0;
    private static final int LOCAL_Y_CONNECTING = 0;
    private static final int LOCAL_WIDTH_CONNECTING = 100;
    private static final int LOCAL_HEIGHT_CONNECTING = 100;
    // Local preview screen position after call is connected.
    private static final int LOCAL_X_CONNECTED = 72;
    private static final int LOCAL_Y_CONNECTED = 72;
    private static final int LOCAL_WIDTH_CONNECTED = 25;
    private static final int LOCAL_HEIGHT_CONNECTED = 25;
    // Remote video screen position
    private static final int REMOTE_X = 0;
    private static final int REMOTE_Y = 0;
    private static final int REMOTE_WIDTH = 100;
    private static final int REMOTE_HEIGHT = 100;
    private VideoRendererGui.ScalingType scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;
    private GLSurfaceView vsv = null;
    private VideoRenderer.Callbacks localRender = null;
    //    private VideoRenderer.Callbacks remoteRender;
    private String mSocketAddress;
    private String callerId;
    private WebRTCClientWebSocket clientWebSocket = null;
    private View view;
    private Runnable runnable;

    private String Save_Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RemoteHelper_download/";

    private Camera cam;
    private MediaRecorder mediaRecorder;
    private boolean recording = false;
    private String save_name = System.currentTimeMillis() + "";
    private SurfaceHolder sh;
    private SurfaceView sv;

    private void setting() {
        cam = Camera.open(1);
        cam.setDisplayOrientation(90);
//        sv = (SurfaceView) getActivity().findViewById(R.id.preview);
//        sv.setVisibility(sv.VISIBLE);
        sh = vsv.getHolder();
        sh.addCallback(this);
        sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rtc, container, false);

        getActivity().getWindow().addFlags(
//                LayoutParams.FLAG_FULLSCREEN
                        LayoutParams.FLAG_KEEP_SCREEN_ON
                        | LayoutParams.FLAG_DISMISS_KEYGUARD
                        | LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | LayoutParams.FLAG_TURN_SCREEN_ON);
        mSocketAddress = "wss://remohelper.com:9090";
        Log.e("SSSSS", "onCreateview");

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("SSSSS", "onAttach");
    }

    @Override
    public void onStart() {
        super.onStart();
        vsv = (GLSurfaceView) view.findViewById(R.id.glview_call);
        vsv.setPreserveEGLContextOnPause(true);
        vsv.setKeepScreenOn(true);

        VideoRendererGui.setView(vsv, new Runnable() {
            @Override
            public void run() {
                init();
            }
        });

        localRender = VideoRendererGui.create(
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType, true);
        Log.e("SSSSS", "onStart");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("SSSSS", "onCreate");
    }

    private void init() {
        Point displaySize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                true, false, 640, 360, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true);

        clientWebSocket = new WebRTCClientWebSocket(getActivity(), this, mSocketAddress, params, VideoRendererGui.getEGLContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        vsv.onPause();
        if (clientWebSocket != null) {
            clientWebSocket.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        vsv.onResume();
        Log.e("SSSSS", "onResume");
        if (clientWebSocket != null) {
            clientWebSocket.onResume();
        }
    }

    @Override
    public void onDestroy() {
        Log.e("SSSSS", "Here5");
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDetach() {
        Log.e("SSSSS", "Here6");
        VideoRendererGui.remove(localRender);
        if (!clientWebSocket.mWebSocketClient.isClosed()) {
            Log.e("SSSSS", "clientWebSocket disconnect");
            clientWebSocket.clearSocket();
            clientWebSocket.mWebSocketClient.close();
        }
        clientWebSocket = null;
        super.onDetach();
    }

    @Override
    public void onStatusChanged(final String newStatus) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getActivity().getApplicationContext(), newStatus, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public void onClose() {
//        getActivity().finish();
        getActivity().onBackPressed();
    }

    @Override
    public void onLocalStream(MediaStream localStream) {
        localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType);
    }

    @Override
    public void onStartRecording() {
//        VideoRendererGui.remove(localRender);
        if (!recording) {
            setting();
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {

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
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);// H264
                        mediaRecorder.setOrientationHint(270);
                        mediaRecorder.setOutputFile(Save_Path + save_name + ".mp4");
                        mediaRecorder.setPreviewDisplay(sh.getSurface());
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        recording = true;
                        handler.sendEmptyMessage(0);
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
                        return;
                    }
//                }
//            });
        }
    }

    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                new CountDownTimer(5000, 500) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onFinish() {
                        // TODO Auto-generated method stub
                        if (recording) {
                            mediaRecorder.stop();
                            mediaRecorder.release();
                            mediaRecorder = null;
                            recording = false;
                            try {
                                cam.stopPreview();
                                cam.release();
                            } catch (Exception e) {

                            }

                            // 서버로 녹화한 영상 전송
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    fileUpload();
                                }
                            }).start();
                            getActivity().onBackPressed();
                        }
                    }
                }.start();
            }
        }
    };

    public void fileUpload() {

        String pathToOurFile = save_name + ".mp4";
        String urlServer = "https://remohelper.com:440/m/websocket/getValue.do";

        String device_ID = Settings.Secure.getString(getActivity().getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        try {
            MediaType media_type_video = MediaType.parse("video/mp4");
            File file = new File(Save_Path + pathToOurFile);
            OkHttpClient client = new OkHttpClient();

            FormBody.Builder builder = new FormBody.Builder();

            JSONObject obj = new JSONObject();
            try {
                obj.put("fileName", pathToOurFile);
                obj.put("date", save_name);
                obj.put("userName", "김진혁");
                obj.put("getRegId", "123456789");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", pathToOurFile, RequestBody.create(media_type_video, file))
                    .addFormDataPart("sariodId", device_ID/* regid */).addFormDataPart("type", "S")
                    .addFormDataPart("param", obj.toString()).build();
            // .addFormDataPart("device_id", /* regid */ device_ID).build();

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
        }
        setCamera(camera);
        try {
            cam.setPreviewDisplay(sh);
            cam.startPreview();
        } catch (Exception e) {
            Log.e("SSSSS",e.toString());
        }
    }

    public void setCamera(Camera camera) {
        // method to set a camera instance
        cam = camera;
    }
}