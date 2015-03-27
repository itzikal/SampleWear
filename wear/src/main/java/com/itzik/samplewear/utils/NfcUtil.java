package com.itzik.samplewear.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;

/**
 * Created by Oren on 3/25/15.
 */
public class NfcUtil
{
    NfcAdapter mNfcAdapter;

    public void createNfcUtil(Context context)
    {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);

        Intent intent = new Intent(context, context.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try
        {
            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
                                       You should specify only the ones that you need. */
        }
        catch (IntentFilter.MalformedMimeTypeException e)
        {
            throw new RuntimeException("fail", e);
        }
        IntentFilter[] intentFiltersArray = new IntentFilter[]{ndef,};
        String[][] techListsArray = new String[][]{new String[]{NfcF.class.getName()}};

        //        mNfcAdapter.enableForegroundDispatch((Activity)context,pendingIntent,intentFiltersArray,techListsArray);

    }

    public void pause(Activity activity)
    {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled())
            mNfcAdapter.disableForegroundDispatch(activity);
    }

    public boolean isEnabled()
    {
        if (mNfcAdapter != null) return mNfcAdapter.isEnabled();
        else return false;
    }

}
