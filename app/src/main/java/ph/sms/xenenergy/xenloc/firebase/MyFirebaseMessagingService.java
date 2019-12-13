package ph.sms.xenenergy.xenloc.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ph.sms.xenenergy.xenloc.MainActivity;
import ph.sms.xenenergy.xenloc.R;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "1";
    @Override
    public void onMessageReceived(RemoteMessage message) {
        sendMyNotification(message.getNotification().getBody());

    }


    private void sendMyNotification(String message) {

        try {
            mContext = getApplicationContext();
            Intent resultIntent = new Intent(mContext, MainActivity.class);
            resultIntent.putExtra("News", "fill");
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent resultPendingIntent = PendingIntent.getBroadcast(mContext,
                    0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder = new NotificationCompat.Builder(mContext);
            mBuilder.setSmallIcon(R.drawable.fb_icon);
            mBuilder.setContentTitle(message)
                    .setContentText(message)
                    .setAutoCancel(false)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(resultPendingIntent);


            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);

                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert mNotificationManager != null;
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
            mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}