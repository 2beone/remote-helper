package net.twobeone.remotehelper.util;

/**
 * Created by Administrator on 2017-06-30.
 */

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.ui.MainActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class GCMIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    // web server 에서 받을 extra key (web server 와 동일해야 함)
    static final String TITLE_EXTRA_KEY = "TITLE";
    static final String MSG_EXTRA_KEY = "MSG";
    static final String TYPE_EXTRA_CODE = "TYPE_CODE";
    // web server 에서 받을 extras key

    private static PowerManager.WakeLock sCpuWakeLock;

    private int sub = 0;
    private Intent intent;
    private NotificationCompat.Builder mBuilder = null;

    public GCMIntentService() {
        super("");
    }

    public GCMIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                // 메시지를 받은 후 작업 시작

                // Post notification of received message.
                sendNotification(extras);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    // 상태바에 공지
    private void sendNotification(Bundle extras) {
        // 혹시 모를 사용가능한 코드
        String typeCode = extras.getString(TYPE_EXTRA_CODE);
        String regid = getRegistrationId(getApplicationContext()); // 기존에 발급받은 등록 아이디를 가져온다
        Log.e("SSSSS",URLDecoder.decode(extras.getString(MSG_EXTRA_KEY)));

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (URLDecoder.decode(extras.getString(MSG_EXTRA_KEY), "UTF-8").contains("&")) {

                sub = URLDecoder.decode(extras.getString(MSG_EXTRA_KEY), "UTF-8").indexOf("&");

                intent = new Intent(this, MainActivity.class);

                mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(URLDecoder.decode(extras.getString(TITLE_EXTRA_KEY), "UTF-8"))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(URLDecoder.decode(extras.getString(MSG_EXTRA_KEY), "UTF-8").substring(0, sub)))
                        .setContentText(URLDecoder.decode(extras.getString(MSG_EXTRA_KEY), "UTF-8").substring(0, sub));
            } else {
                intent = new Intent(this, MainActivity.class);

                mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(URLDecoder.decode(extras.getString(TITLE_EXTRA_KEY), "UTF-8"))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(URLDecoder.decode(extras.getString(MSG_EXTRA_KEY), "UTF-8")))
                        .setContentText(URLDecoder.decode(extras.getString(MSG_EXTRA_KEY), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setVibrate(new long[]{0, 500}); // 진동 효과 (퍼미션 필요)
        mBuilder.setAutoCancel(true); // 클릭하면 삭제
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        acquireCpuWakeLock(this);
        releaseCpuLock();
    }

    static void acquireCpuWakeLock(Context context) {

        if (sCpuWakeLock != null) {
            return;
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "RemoteHelper");

        sCpuWakeLock.acquire();
    }

    static void releaseCpuLock() {

        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }

    private String getRegistrationId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // 등록 아이디를 SharedPreferences에서 가져온다.
        String registrationId = prefs.getString(Constants.PROPERTY_REG_ID, ""); // 저장해둔 등록
        // 아이디가 없으면 빈 문자열을 반환한다.
        if (registrationId.isEmpty()) {
            return "";
        }

        return registrationId;
    }

}