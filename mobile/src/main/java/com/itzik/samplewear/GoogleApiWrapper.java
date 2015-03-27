package com.itzik.samplewear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Itzik on 3/26/2015.
 */
public class GoogleApiWrapper implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener
{
    private static final String LOG_TAG = GoogleApiWrapper.class.getSimpleName();
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String COUNT_KEY = "com.example.key.count";
    private static final long TIMEOUT_MS = 3000;
    private static GoogleApiWrapper msInstance;
    private final GoogleApiClient mGoogleApiClient;
    private final Context mContext;
    private boolean mResolvingError;

    public interface OnMessageReceivedListener
    {
        void onMassageReceived(MessageEvent messageEvent);
    }

    
    private ArrayList<OnMessageReceivedListener> mMassageRecivedListeners = new ArrayList<>();

    public void addListener(OnMessageReceivedListener listener)
    {
        mMassageRecivedListeners.add(listener);
    }
    public void removeListener(OnMessageReceivedListener listener)
    {
        mMassageRecivedListeners.remove(listener);
    }

    public static void initInstance(Context context)
    {
        if(msInstance == null)
        {
            msInstance = new GoogleApiWrapper(context);
        }
    }

    public static GoogleApiWrapper getInstance()
    {
        return msInstance;
    }
    private GoogleApiWrapper(Context context)
    {
        mContext = context;
        mGoogleApiClient = new GoogleApiClient.Builder(mContext).addApi(Wearable.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        connect();
    }

    private void connect()
    {
        Log.d(LOG_TAG, "connect(), ");
        if (mGoogleApiClient != null && !(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()))
        {
            Log.d(LOG_TAG, "connect(), connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent)
    {
        Log.d(LOG_TAG, "onMessageReceived(), path: "+ messageEvent.getPath());
        for (OnMessageReceivedListener massageRecivedListener : mMassageRecivedListeners)
        {
            massageRecivedListener.onMassageReceived(messageEvent);
        }


        //        if (messageEvent.getPath().equals(WEAR_MESSAGE_PATH))
        //        {
        //            Log.d(LOG_TAG, "onMessageReceived(), is wear message");
        //            runOnUiThread(new Runnable()
        //            {
        //                @Override
        //                public void run()
        //                {
        //                    Log.d(LOG_TAG, "onMessageReceived(), set text");
        //                    mTextView.setText(new String(messageEvent.getData()));
        //                }
        //            });
        //
        //        }

    }

    public void sendMessage(final String path, final String text)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for (Node node : nodes.getNodes())
                {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, text.getBytes()).await();
                }
            }
        }).start();
    }

    public void onPause()
    {
        Log.d(LOG_TAG, "onPause(), ");
        if (mGoogleApiClient != null)
        {
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())
            {
                mGoogleApiClient.disconnect();
            }
        }

    }

    public void onResume()
    {
        connect();
    }

    //<editor-fold desc="GoogleApiClient.ConnectionCallbacks">
    @Override
    public void onConnected(Bundle connectionHint)
    {
        // Now you can use the Data Layer API
        Log.d(LOG_TAG, "onConnected: adding listener " + connectionHint);
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        Log.d(LOG_TAG, "onConnectionSuspended: " + cause);
    }
    //</editor-fold>

    //<editor-fold desc="GoogleApiClient.OnConnectionFailedListener">
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.d(LOG_TAG, "onConnectionFailed(), ");
        if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE)
        {
            // The Android Wear app is not installed
            mResolvingError = true;
        }

        if (mResolvingError)
        {
            // Already attempting to resolve an error.
            return;
        }
        if (connectionResult.hasResolution())
        {
//            try
//            {
//                mResolvingError = true;
////                connectionResult.startResolutionForResult(mContext, REQUEST_RESOLVE_ERROR);
//            }
//            catch (IntentSender.SendIntentException e)
//            {
//                // There was an error with the resolution intent. Try again.
//                mGoogleApiClient.connect();
//            }
        }
        else
        {
            mResolvingError = true;
        }
        Log.d(LOG_TAG, "onConnectionFailed: " + connectionResult);

    }
    //</editor-fold>

    //<editor-fold desc="DataApi.DataListener">
    @Override
    public void onDataChanged(DataEventBuffer dataEvents)
    {
        Log.d(LOG_TAG, "onDataChanged(), ");
        for (DataEvent event : dataEvents)
        {
            if (event.getType() == DataEvent.TYPE_CHANGED)
            {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/count") == 0)
                {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateCount(dataMap.getInt(COUNT_KEY));
                }
                if (event.getDataItem().getUri().getPath().contains("/image"))
                {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Asset profileAsset = dataMapItem.getDataMap().getAsset("profileImage");
                    final Bitmap bitmap = loadBitmapFromAsset(profileAsset);


                }
            }
            else if (event.getType() == DataEvent.TYPE_DELETED)
            {
                // DataItem deleted
            }
        }
    }

    private void updateCount(int c)
    {
        Log.d(LOG_TAG, "updateCount(), " + c + "");
    }
    //</editor-fold>

    //<editor-fold desc="Send data request (example of bitmap)">
    public Bitmap loadBitmapFromAsset(Asset asset)
    {
        if (asset == null)
        {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result = mGoogleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!result.isSuccess())
        {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset).await().getInputStream();

        //        mGoogleApiClient.disconnect(); was in the example but seems unnecessary.

        if (assetInputStream == null)
        {
            Log.w(LOG_TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap)
    {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    public void putDataRequest()
    {

        Bitmap bitmap = BitmapFactory.decodeFile("path to file");
        Asset asset = createAssetFromBitmap(bitmap);
        PutDataRequest request = PutDataRequest.create("/image");
        request.putAsset("profileImage", asset);
        Wearable.DataApi.putDataItem(mGoogleApiClient, request);
    }
    //</editor-fold>

}
