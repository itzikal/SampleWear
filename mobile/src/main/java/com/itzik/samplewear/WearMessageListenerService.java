package com.itzik.samplewear;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Itzik on 3/8/2015.
 */
public class WearMessageListenerService extends WearableListenerService
{
    private static final String LOG_TAG = WearMessageListenerService.class.getSimpleName();
    private static final String START_ACTIVITY = "/start_activity";




    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {
        Log.d(LOG_TAG, "onMessageReceived(), ");
        if (messageEvent.getPath().equalsIgnoreCase(START_ACTIVITY))
        {
            Log.d(LOG_TAG, "onMessageReceived(), starting activity");
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


        else
        {
            super.onMessageReceived(messageEvent);
        }

        Intent openWearActivityIntent = new Intent("com.myCompany.myApp.ACTION.openWearActivity");
        PendingIntent p = PendingIntent.getActivity(getApplicationContext(), 0, openWearActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action openWearActivityIntentAction =new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "open Activity", p).build();

        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the openMainAcivityAction
        NotificationCompat.Action openMainAcivityAction =new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Open app, Action Sample", mainActivityPendingIntent)
                .build();

        NotificationCompat.BigPictureStyle bigImageStyle = new NotificationCompat.BigPictureStyle()
                .bigPicture(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)).setSummaryText("Big image style");
        bigImageStyle.setBigContentTitle("Big image style content title");

        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText("this is event description in big style: " + "");

        NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle("Page 2")
                .bigText("A lot of text...");

        Notification secondPageNotification =new NotificationCompat.Builder(getApplicationContext())
                .setStyle(secondPageStyle)
                .build();

        Notification bigImagePageNotification =new NotificationCompat.Builder(getApplicationContext())
                .setStyle(bigImageStyle)
                .build();

        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender()
                .setHintHideIcon(false)
                .setBackground(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .addPage(secondPageNotification)
                .addPage(bigImagePageNotification);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(mainActivityPendingIntent)
                .setContentTitle("Title")
                .extend(wearableExtender)

                .addAction(openMainAcivityAction)
                .addAction(openWearActivityIntentAction)
                .setStyle(bigStyle)
                .setContentText("Android Wear Notification");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, notificationBuilder.build());
    }
}
