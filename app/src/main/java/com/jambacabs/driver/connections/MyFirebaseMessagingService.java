package com.jambacabs.driver.connections;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.jambacabs.driver.Chat;
import com.jambacabs.driver.Inbox;
import com.jambacabs.driver.NotificationDetailsActivity;
import com.jambacabs.driver.R;
import com.jambacabs.driver.Splash;
import com.jambacabs.driver.singleton.UserSession;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    /**
     * onMessageReceived
     * Dharma Teja
     * 12-10-2018
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            try {
                Map<String, String> params = remoteMessage.getData();
                JSONObject object = new JSONObject(params);
                String message = object.optString("text");
                String screen = object.optString("type");
                String title = object.optString("title");
                Context context = getApplicationContext();
                ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                assert am != null;
                ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
                assert foregroundTaskInfo.topActivity != null;
                String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
                if (foregroundTaskPackageName.equals(context.getPackageName())) {
                    sendDataToController(object, screen);
                } else {
                    createPushNotification(message, title, object, screen);
                }
            } catch (Exception e) {
               Log.v("parseError", "ParseError"+e.getLocalizedMessage());
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendDataToController(JSONObject object, String screen) {

        String user_id = new UserSession(this).getUserId();
        if (!user_id.equals("")) {
            checkNotificationSound(object.optString("text"));

            if (!screen.equals("admin_info") && !screen.equals("message") && !screen.equals("fleet_request"))
            {
                new UserSession(this).removeChatDetails();
                if (!screen.equals("ride_info"))
                {
                    new UserSession(this).setTagFrom("push");
                    new UserSession(this).setScreen(screen);
                    new UserSession(this).setPushParams(object.toString());
                }else
                {
                    new UserSession(this).setNotificationReceivedTime(new Date().getTime());
                }
                Intent intent = new Intent(this, Splash.class);
                intent.putExtra("screen", screen);
                intent.putExtra("tagFrom", "push");
                intent.putExtra("parameters", object.toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else
            {
                Intent intent;
                if (screen.equals("message"))
                {
                    intent = new Intent(this, Chat.class);
                    intent.putExtra("parameters", object.toString());
                }else if (screen.equals("admin_info"))
                {
                    new UserSession(this).removeChatDetails();
                    intent = new Intent(this, Inbox.class);
                }else
                {
                    new UserSession(this).removeChatDetails();
                    intent = new Intent(this, NotificationDetailsActivity.class);
                    intent.putExtra("notification_data", object.toString());
                    intent.putExtra("tagFrom", "push");
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else {
            String message = object.optString("text");
            String title = object.optString("title");
            createPushNotification(message, title, object, screen);
        }
    }

    public void checkNotificationSound(String message) {
        if (message.equalsIgnoreCase("Ride request")) {
            try {
                Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.notification_sound);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), uri);
                r.play();
            } catch (Exception e) {
               Log.v("parseError", "ParseError"+e.getLocalizedMessage());
            }
        }
    }

    /**
     * onNewToken
     * Dharma Teja
     * 12-10-2018
     */

    @Override
    public void onNewToken(@NotNull String s) {
        super.onNewToken(s);
        new UserSession(this).setToken(s);
    }

    /**
     * onNewToken
     * Dharma Teja
     * 12-10-2018
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createPushNotification(String message, String title, JSONObject dictResponse, String screen) {

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL_ID = "jambocabs_driver_version_1";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        int notification_id = (int) (Math.random() * 100) + (int) System.currentTimeMillis();

        String user_id = new UserSession(this).getUserId();
        Intent intent;

        if (!user_id.equals("")) {
            if (!screen.equals("admin_info") && !screen.equals("message") && !screen.equals("fleet_request"))
            {
                new UserSession(this).removeChatDetails();
                if (!screen.equals("ride_info"))
                {
                    new UserSession(this).setTagFrom("push");
                    new UserSession(this).setScreen(screen);
                    new UserSession(this).setPushParams(dictResponse.toString());
                }else
                {
                    new UserSession(this).setNotificationReceivedTime(new Date().getTime());
                }
//                intent = new Intent(this, Dashboard.class);
                intent = new Intent(this, Splash.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Long.toString(System.currentTimeMillis()));
                intent.putExtra("screen", screen);
                intent.putExtra("tagFrom", "push");
                intent.putExtra("parameters", dictResponse.toString());
            }else
            {
                if (screen.equals("message"))
                {
                    intent = new Intent(this, Chat.class);
                    intent.putExtra("parameters", dictResponse.toString());
                    new UserSession(this).setChatDetails(dictResponse.toString());
                }else if (screen.equals("admin_info"))
                {
                    new UserSession(this).removeChatDetails();
                    intent = new Intent(this, Inbox.class);
                }else
                {
                    new UserSession(this).removeChatDetails();
                    intent = new Intent(this, NotificationDetailsActivity.class);
                    intent.putExtra("notification_data", dictResponse.toString());
                    intent.putExtra("tagFrom", "push");
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Long.toString(System.currentTimeMillis()));
            }
        }else
        {
            new UserSession(this).removeChatDetails();
            intent = new Intent(this, Splash.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Long.toString(System.currentTimeMillis()));
        }
        checkNotificationSound(message);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, getResources().getString(R.string.app_name), importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }


        PendingIntent contentIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_app_icon))
                .setContentTitle(getResources().getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(title))
                .setContentText(message);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(uri);
        if (screen.equals("ride_info"))
        {
            mBuilder.setTimeoutAfter(18000);
        }
        mBuilder.setChannelId(CHANNEL_ID);

        if (notificationManager != null) {
            notificationManager.notify(notification_id, mBuilder.build());
        }

    }
}
