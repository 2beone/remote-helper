package net.twobeone.remotehelper;

public final class Constants {

    /**
     * 디바이스 검사 웹페이지
     */
    public static final String TROUBLES_HTTP_URI = "https://test.webrtc.org";

    public static final String HTTP_URI_REQ_INSERT = "https://remohelper.com:440/m/reqInsertSaviorInfo.ajax";
    public static final String HTTP_URI_WEBSOCKET = "wss://remohelper.com:9090";
    public static final String HTTP_URI_VIDEO_UPLOAD = "https://remohelper.com:440/m/websocket/getValue.do";
    public static final String HTTP_URI_FILE_DOWNLOAD = "https://remohelper.com:440/download/";

    /**
     * Chat API URI
     */
    public static final String HTTP_URI_CHAT = "http://210.205.92.24:8220/chatbot/api/";

    /**
     * 다운로드 디렉토리명
     */
    public static final String DONWLOAD_DIRECTORY_NAME = "RemoteHelper_download";

    /**
     * 에셋
     */
    public static final String ASSETS_MANUALS_SAMPLES_DIRECTORY_PATH = "manuals/samples";

    /**
     * 데이터베이스
     */
    public static final String DATABASE_FILE_NAME = "default.sqlite";
    public static final int DATABASE_VERSION_CODE = 1;

    /**
     * 네이버맵
     */
    public static final String NAVER_MAP_CLIENT_ID = "vaaipy79LqtPJRueO9eJ";
    public static final String NAVER_MAP_CLIENT_SECRET = "y6UNfzsFFK";

    /**
     * 설정
     */
    public static final String PREF_LOCATION_ENABLED = "location_enabled";
    public static final String PREF_USER_NAME = "user_name";
    public static final String PREF_USER_AGE = "user_age";
    public static final String PREF_USER_MOBILE = "user_mobile";
    public static final String PREF_USER_EMERGENCY_CONTACT = "user_emergency_contact";
    public static final String PREF_USER_BLOOD_TYPE = "user_blood_type";
    public static final String PREF_USER_ETC = "user_etc";
    public static final String PREF_FILE_RECEIVE_DT = "file_receive_dt";

    /**
     * GCM
     */
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appver";
    public static final String SENDER_ID = "186620067699";
}
