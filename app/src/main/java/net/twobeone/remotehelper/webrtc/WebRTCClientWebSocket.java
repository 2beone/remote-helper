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

package net.twobeone.remotehelper.webrtc;

import android.content.Context;
import android.opengl.EGLContext;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Administrator on 2017-04-18.
 */

public class WebRTCClientWebSocket {

    private final Context mContext;
    public WebSocketClient mWebSocketClient;
    private URI uri;

    private final static int MAX_PEER = 2;
    private boolean[] endPoints = new boolean[MAX_PEER];
    private PeerConnectionFactory factory = null;
    private HashMap<String, WebRTCClientWebSocket.Peer> peers = new HashMap<>();
    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
    private PeerConnectionParameters pcParams;
    private MediaConstraints pcConstraints = new MediaConstraints();
    private MediaStream remoteMS = null;
    private MediaStream localMS = null;
    private AudioSource audioSource = null;
    private VideoSource videoSource = null;
    private WebRTCClientWebSocket.RtcListener mListener;
    private HashMap<String, WebRTCClientWebSocket.Command> commandMap;
    private String people = "";
    private String fileName = "";
    private String filePath = "";
    private VideoCapturer videoCapturer;
    private String camera = "front";
    private String Save_Path;
    private String url = "https://remohelper.com:440/download/";
    private JSONObject payload = null;
    private JSONObject data = null;

    public interface RtcListener {

        void onStatusChanged(String newStatus);

        void onLocalStream(MediaStream localStream);

        void onStartRecording();

        void onClose();
    }

    private interface Command {
        void execute(String peerId, JSONObject payload) throws JSONException;
    }

    private class CreateOfferCommand implements WebRTCClientWebSocket.Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.e("SSSSS", "CreateOfferCommand");
            WebRTCClientWebSocket.Peer peer = peers.get(peerId);
            peer.pc.createOffer(peer, pcConstraints);
        }
    }

    private class CreateAnswerCommand implements WebRTCClientWebSocket.Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.e("SSSSS", "CreateAnswerCommand");
            WebRTCClientWebSocket.Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            peer.pc.setRemoteDescription(peer, sdp);
            peer.pc.createAnswer(peer, pcConstraints);
        }
    }

    private class SetRemoteSDPCommand implements WebRTCClientWebSocket.Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.e("SSSSS", "SetRemoteSDPCommand");
            WebRTCClientWebSocket.Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp").replace("VP9", "VP8")//내폰용
            );
            peer.pc.setRemoteDescription(peer, sdp);
        }
    }

    private class AddIceCandidateCommand implements WebRTCClientWebSocket.Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.e("SSSSS", "AddIceCandidateCommand");
            PeerConnection pc = peers.get(peerId).pc;
            if (pc.getRemoteDescription() != null) {
                IceCandidate candidate = new IceCandidate(
                        payload.getString("sdpMid"),
                        payload.getInt("sdpMLineIndex"),
                        payload.getString("candidate")
                );
                pc.addIceCandidate(candidate);
            }
        }
    }

    public void sendMessage(String to, String type, JSONObject payload) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("name", to);
        message.put("type", type);
        message.put(type, payload);
        if (type.equals("offer")) {
            message.put("latitude", "37.485305");
            message.put("longitude", "127.119737");
            message.put("saviorName", "김진혁");
            message.put("nowAddress", "서울시 송파구 문정동");
            message.put("regId", "ABCDEFGHIJKLMNOP");
        }
        Log.e("SSSSS", to + " ::::: " + type + " ::::: " + payload.toString());
        mWebSocketClient.send(message.toString());
    }

    public WebRTCClientWebSocket(Context context, WebRTCClientWebSocket.RtcListener listener, String host, PeerConnectionParameters params, EGLContext mEGLcontext) {

        Log.e("SSSSS", "WebRTCClientWebSocket Init");
        Save_Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RemoteHelper_download/";
        mContext = context;
        mListener = listener;
        pcParams = params;

        PeerConnectionFactory.initializeAndroidGlobals(mContext, true, true, params.videoCodecHwAcceleration, mEGLcontext);

        Log.e("SSSSS", "initializeAndroidGlobals Init");

        factory = new PeerConnectionFactory();
        Log.e("SSSSS", "PeerConnectionFactory Init");

        mWebSocketClient = null;

        iceServers.add(new PeerConnection.IceServer("stun:stun2.1.google.com:19302"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        iceServers.add(new PeerConnection.IceServer("stun:remohelper.com:3478"));

        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));

        this.commandMap = new HashMap<>();
        commandMap.put("callAnswer", new WebRTCClientWebSocket.CreateOfferCommand());
        commandMap.put("cameraClick", new WebRTCClientWebSocket.CreateOfferCommand());
        commandMap.put("offer", new WebRTCClientWebSocket.CreateAnswerCommand());
        commandMap.put("answer", new WebRTCClientWebSocket.SetRemoteSDPCommand());
        commandMap.put("candidate", new WebRTCClientWebSocket.AddIceCandidateCommand());

        try {
            uri = new URI(host);
            Log.e("SSSSS", "URI OK");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        setCamera();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    mWebSocketClient = new WebSocketClient(uri) {

                        @Override
                        public void onOpen(ServerHandshake handshakedata) {
                            Log.e("SSSSS", "Opened");
                            try {
                                JSONObject message = new JSONObject();
                                message.put("type", "login");
                                message.put("name", "김진혁");
                                mWebSocketClient.send(message.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onMessage(String s) {
                            Log.e("SSSSS", "onMessage " + s);
                            try {
                                data = new JSONObject(s);
                                String type = data.getString("type");
                                payload = null;
                                if (type.equals("offer")) {
                                    payload = data.getJSONObject("offer");
                                } else if (type.equals("answer")) {
                                    payload = data.getJSONObject("answer");
                                } else if (type.equals("leave")) {
                                    JSONObject message = new JSONObject();
                                    message.put("type", "leave");
                                    message.put("name", people);
                                    mWebSocketClient.send(message.toString());
                                    removePeer(people);
                                } else if (type.equals("login")) {
                                    Log.e("SSSSS list", "" + data.getJSONArray("people").length());
                                    if (data.getJSONArray("people").length() > 0) {
                                        people = data.getJSONArray("people").getString(0);
                                        if(people.equals("police")){
                                            people = "";
                                            people = data.getJSONArray("people").getString(1);
                                        }
//                                        people = "chae";//임의 2beone1로만 연결
                                        JSONObject message = new JSONObject();
                                        message.put("type", "call");
                                        message.put("name", people);
                                        message.put("saviorName", "김진혁");
                                        mWebSocketClient.send(message.toString());
                                    } else {
                                        localMS.dispose();
                                        localMS = null;
                                        videoSource.dispose();
                                        videoSource = null;
                                        videoCapturer.dispose();
                                        videoCapturer = null;
                                        audioSource.dispose();
                                        audioSource = null;
                                        startRecording();
                                    }
                                } else if (type.equals("candidate")) {
                                    payload = data.getJSONObject("candidate");
                                } else if (type.equals("callAnswer")) {
                                    people = data.getString("name");
                                } else if (type.equals("cameraClick")) {
                                    Peer peer = peers.get(people);
                                    peer.pc.removeStream(localMS);

                                    localMS.dispose();
                                    localMS = null;
                                    videoSource.dispose();
                                    videoSource = null;
                                    videoCapturer.dispose();
                                    videoCapturer = null;
                                    audioSource.dispose();
                                    audioSource = null;
                                    setCamera();

                                    peer.pc.addStream(localMS);
                                    payload = data.getJSONObject("offer2");
                                } else if (type.equals("file")) {
                                    fileName = data.getString("filename");
                                    filePath = data.getString("filepath");
                                    Log.e("SSSSS", "fileName ::: " + fileName + " filePath ::: " + filePath);

                                    downloadThread(url + filePath, Save_Path + fileName, fileName);
                                } else if (type.equals("police")) {
                                    people = "police";
                                    JSONObject message = new JSONObject();
                                    message.put("type", "call");
                                    message.put("name", people);
                                    message.put("saviorName", "김진혁");
                                    mWebSocketClient.send(message.toString());
                                }
                                // if peer is unknown, try to add him
                                if (!peers.containsKey(people) && !type.equals("leave") && !type.equals("login") && !type.equals("call")
                                        && !type.equals("cameraClick") && !type.equals("file") && !type.equals("police") && !people.equals("")) {
                                    // if MAX_PEER is reach, ignore the call

                                    int endPoint = findEndPoint();
                                    if (endPoint != MAX_PEER) {
                                        Peer peer = addPeer(people, endPoint);
                                        peer.pc.addStream(localMS);
                                        Log.e("SSSSS", "TYPE!!!!!!" + type);
                                        commandMap.get(type).execute(people, payload);
                                    }
                                } else if (!type.equals("leave") && !type.equals("login") && !type.equals("file") && !people.equals("") && !type.equals("police")) {
                                    Log.e("SSSSS", "TYPE!!!!!!" + type);
                                    commandMap.get(type).execute(people, payload);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onClose(int code, String reason, boolean remote) {
                            if (localMS != null) {
                                Peer peer = peers.get(people);

                                peer.pc.removeStream(localMS);

                                localMS.dispose();
                                localMS = null;
                                videoSource.dispose();
                                videoSource = null;
                                videoCapturer.dispose();
                                videoCapturer = null;
                                audioSource.dispose();
                                audioSource = null;
                                peer.pc = null;

                                peers.remove(peer.id);
                                endPoints[peer.endPoint] = false;
                            }
                            Log.e("SSSSS", "onClose");
                        }

                        @Override
                        public void onError(Exception ex) {
                            Log.e("SSSSS", "onError " + ex.toString());
                        }
                    };

                    mWebSocketClient.setSocket(getSSLContext().getSocketFactory().createSocket());
                    mWebSocketClient.connect();

                } catch (Exception e) {
                    Log.e("SSSSS", "Socket Error " + e.toString());
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException {

        TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, new java.security.SecureRandom());
        return sslContext;
    }

    private class Peer implements SdpObserver, PeerConnection.Observer {
        private PeerConnection pc;
        private String id;
        private int endPoint;

        @Override
        public void onCreateSuccess(final SessionDescription sdp) {
            // TODO: modify sdp to use pcParams prefered codecs
            try {
                JSONObject payload = new JSONObject();
                payload.put("type", sdp.type.canonicalForm());
                if (sdp.type.canonicalForm().equals("offer") ||
                        sdp.type.canonicalForm().equals("offer2")) {
                    Log.e("SSSSS", "offer sdp change video codecs");
                    payload.put("sdp", sdp.description.replace("m=video 9 RTP/SAVPF", "m=video 9 RTP/SAVPF 98")
                            .replace("VP8", "VP9")
                            .replace("a=rtpmap:100 VP9", "a=rtcp-rsize\r\n" +
                                    "a=rtpmap:98 VP8/90000\r\n" +
                                    "a=rtcp-fb:98 ccm fir\r\n" +
                                    "a=rtcp-fb:98 nack\r\n" +
                                    "a=rtcp-fb:98 nack pli\r\n" +
                                    "a=rtcp-fb:98 goog-remb\r\n" +
                                    "a=rtcp-fb:98 transport-cc\r\n" +
                                    "a=rtpmap:100 VP9"));
                } else {
                    payload.put("sdp", sdp.description);
                }
                sendMessage(id, sdp.type.canonicalForm(), payload);
                pc.setLocalDescription(WebRTCClientWebSocket.Peer.this, sdp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSetSuccess() {
        }

        @Override
        public void onCreateFailure(String s) {
        }

        @Override
        public void onSetFailure(String s) {
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.e("SSSSS", "onIceConnectionChange ::: " + iceConnectionState.toString());
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
                Log.e("SSSSS", "DISCONNECTED");
                for (WebRTCClientWebSocket.Peer peer : peers.values()) {
                    peer.pc.dispose();
                }
                mListener.onStatusChanged("DISCONNECTED");
            }
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        }

        @Override
        public void onIceCandidate(final IceCandidate candidate) {
            try {
                JSONObject payload = new JSONObject();
                payload.put("candidate", candidate.sdp);
                payload.put("sdpMid", candidate.sdpMid);
                payload.put("sdpMLineIndex", candidate.sdpMLineIndex);
                sendMessage(id, "candidate", payload);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            try {
                remoteMS = mediaStream;
                Log.e("SSSSS", "onAddStream " + mediaStream.label());
            }catch (Exception e){
                Log.e("SSSSS", "onAddStream " + e.toString());
            }
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Log.e("SSSSS", "onRemoveStream " + mediaStream.label());
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
        }

        @Override
        public void onRenegotiationNeeded() {

        }

        public Peer(String id, int endPoint) {
            Log.e("SSSSS", "new Peer: " + id + " " + endPoint);
            this.pc = factory.createPeerConnection(iceServers, pcConstraints, this);
            this.id = id;
            this.endPoint = endPoint;

            pc.addStream(localMS); //, new MediaConstraints()

            mListener.onStatusChanged("CONNECTING");
        }
    }

    private WebRTCClientWebSocket.Peer addPeer(String id, int endPoint) {
        Log.e("SSSSS", "addPeer");
        WebRTCClientWebSocket.Peer peer = new WebRTCClientWebSocket.Peer(id, endPoint);
        peers.put(id, peer);

        endPoints[endPoint] = true;
        return peer;
    }

    public void removePeer(String id) {
        Log.e("SSSSS", "removePeer");
        WebRTCClientWebSocket.Peer peer = peers.get(id);

        peer.pc.removeStream(localMS);

        localMS.dispose();
        localMS = null;
        videoSource.dispose();
        videoSource = null;
        videoCapturer.dispose();
        videoCapturer = null;
        audioSource.dispose();
        audioSource = null;
        peer.pc = null;

        peers.remove(peer.id);
        endPoints[peer.endPoint] = false;
        onDestroy();
    }

    /**
     * Call this method in Activity.onPause()
     */
    public void onPause() {
        if (videoSource != null) videoSource.stop();
    }

    /**
     * Call this method in Activity.onResume()
     */
    public void onResume() {
        if (videoSource != null) videoSource.restart();
    }

    /**
     * Call this method in Activity.onDestroy()
     */
    public void onDestroy() {
        Log.e("SSSSS", "onDestroy");
        mWebSocketClient.close();
        mListener.onClose();
    }

    private int findEndPoint() {
        for (int i = 0; i < MAX_PEER; i++) if (!endPoints[i]) return i;
        return MAX_PEER;
    }

    private void setCamera() {
        if (camera.equals("front")) {
            videoCapturer = getVideoCapturerFront();
            camera = "back";
        } else {
            videoCapturer = getVideoCapturerBack();
            camera = "front";
        }

        localMS = factory.createLocalMediaStream("ARDAMS");
        if (pcParams.videoCallEnabled) {
            MediaConstraints videoConstraints = new MediaConstraints();
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(pcParams.videoHeight)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(pcParams.videoWidth)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(pcParams.videoFps)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(pcParams.videoFps)));

            videoSource = factory.createVideoSource(videoCapturer, videoConstraints);
            localMS.addTrack(factory.createVideoTrack("ARDAMSv0", videoSource));
        }

        MediaConstraints audioConstraints = new MediaConstraints();
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("googEchoCancellation", "false"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("googAutoGainControl", "false"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("googHighpassFilter", "false"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("googNoiseSuppression", "false"));

        audioSource = factory.createAudioSource(audioConstraints);
        localMS.addTrack(factory.createAudioTrack("ARDAMSa0", audioSource));

        mListener.onLocalStream(localMS);
    }

    private VideoCapturer getVideoCapturerFront() {
        String frontCameraDeviceName = VideoCapturerAndroid.getNameOfFrontFacingDevice();//전방
        return VideoCapturerAndroid.create(frontCameraDeviceName);
    }

    private VideoCapturer getVideoCapturerBack() {
        String backCameraDeviceName = VideoCapturerAndroid.getNameOfBackFacingDevice();//후방
        return VideoCapturerAndroid.create(backCameraDeviceName);
    }

    public void clearSocket() {
        try {
            JSONObject message = new JSONObject();
            message.put("type", "leave");
            message.put("name", people);
            mWebSocketClient.send(message.toString());
        } catch (JSONException e) {

        }
    }

    private void downloadThread(String serverPath, String localPath, String filename) {
        final String ServerUrl = serverPath;
        final String FileName = filename;
        final int sub = serverPath.lastIndexOf(".");
        final String FileExtend = serverPath.substring(sub);
        final String LocalPath = localPath + FileExtend;

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
                    for (; ; ) {
                        Read = is.read(tmpByte);
                        if (Read <= 0) {
                            break;
                        }
                        fos.write(tmpByte, 0, Read);
                    }
                    is.close();
                    fos.close();
                    conn.disconnect();

                } catch (MalformedURLException e) {
                    Log.e("ERROR1", e.getMessage());
                } catch (IOException e) {
                    Log.e("ERROR2", e.getMessage());
                }
            }
        }).start();
    }

    private void startRecording() {
        mListener.onStartRecording();
    }

    public void onMute(boolean mutests) {
        if (mutests) {
            remoteMS.audioTracks.getFirst().setEnabled(false);
        } else {
            remoteMS.audioTracks.getFirst().setEnabled(true);
        }
    }

    public void onChangeCamera() {
        WebRTCClientWebSocket.Peer peer = peers.get(people);
        peer.pc.removeStream(localMS);

        localMS.dispose();
        localMS = null;
        videoSource.dispose();
        videoSource = null;
        videoCapturer.dispose();
        videoCapturer = null;
        audioSource.dispose();
        audioSource = null;
        setCamera();

        peer.pc.addStream(localMS);
        try {
            payload = data.getJSONObject("offer2");
        } catch (JSONException e) {

        }
    }

    public void onChangeVoice(boolean voicests) {
        if (voicests) {
            localMS.videoTracks.getFirst().setEnabled(false);
        } else {
            localMS.videoTracks.getFirst().setEnabled(true);
        }
    }

}
