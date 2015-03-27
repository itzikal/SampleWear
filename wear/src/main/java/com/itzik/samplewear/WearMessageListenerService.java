package com.itzik.samplewear;

import android.content.Intent;
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
            Intent intent = new Intent(this, WearMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            super.onMessageReceived(messageEvent);
        }
    }

}
