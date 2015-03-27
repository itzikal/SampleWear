package com.itzik.samplewear.utils;

import com.google.android.gms.wearable.MessageEvent;

/**
 * Created by Itzik on 3/26/2015.
 */
public interface OnMessageReceivedListener
{
    void onMassageReceived(MessageEvent messageEvent);
}
