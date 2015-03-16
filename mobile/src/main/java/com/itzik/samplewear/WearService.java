package com.itzik.samplewear;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Itzik on 3/9/2015.
 */
public class WearService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener
{
    private static final String LOG_TAG = WearService.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    private ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_MESSAGE_TO_WEAR = 3;
    private static final int MSG_IMAGE_TO_WEAR = 4;

    private static final String WEAR_MESSAGE_PATH = "/message";
    @Override
    public void onCreate()
    {
        super.onCreate();
        createApiClient();
    }
    private void createApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(LOG_TAG, "onBind(), ");
        return mMessenger.getBinder();
    }
//

    private class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_MESSAGE_TO_WEAR:
                    sendMessageToWear(WEAR_MESSAGE_PATH, (String) msg.obj);
                    break;
                case MSG_IMAGE_TO_WEAR:
                    putDataRequest((Bitmap)msg.obj);
                default:
                    super.handleMessage(msg);
            }
        }
    }
    private void sendMessageToWear(final String path, final String text)
    {
        Log.d(LOG_TAG, "sendMessage(), ");
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(LOG_TAG, "sendMessage - run(), ");
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for (Node node : nodes.getNodes())
                {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, text.getBytes()).await();
                    Log.d(LOG_TAG, "message sent result: " + result.getRequestId());
                }
            }
        }).start();
    }

    //<editor-fold desc="GoogleApiClient.ConnectionCallbacks">
    @Override
    public void onConnected(Bundle connectionHint)
    {
        // Now you can use the Data Layer API
        Log.d(LOG_TAG, "onConnected: " + connectionHint);
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
        if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE)
        {
            // The Android Wear app is not installed
        }
    }
    //</editor-fold>

    //<editor-fold desc="DataApi.DataListener">
    @Override
    public void onDataChanged(DataEventBuffer dataEvents)
    {
//        for (DataEvent event : dataEvents)
//        {
//            if (event.getType() == DataEvent.TYPE_CHANGED)
//            {
//                // DataItem changed
//                DataItem item = event.getDataItem();
//                if (item.getUri().getPath().compareTo("/count") == 0)
//                {
//                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
//                    updateCount(dataMap.getInt(COUNT_KEY));
//                }
//                if (event.getDataItem().getUri().getPath().equals("/image"))
//                {
//                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
//                    Asset profileAsset = dataMapItem.getDataMap().getAsset("profileImage");
//                    Bitmap bitmap = loadBitmapFromAsset(profileAsset);
//                }
//            }
//            else if (event.getType() == DataEvent.TYPE_DELETED)
//            {
//                // DataItem deleted
//            }
//        }
    }

    private void updateCount(int c)
    {
    }
    //</editor-fold>

    //<editor-fold desc="Send data request (example of bitmap)">
    public Bitmap loadBitmapFromAsset(Asset asset)
    {
        if (asset == null)
        {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result = mGoogleApiClient.blockingConnect(3000, TimeUnit.MILLISECONDS);
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

    private void putDataRequest(Bitmap bitmap)
    {
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Asset asset = createAssetFromBitmap(bitmap);

        PutDataMapRequest dataMap = PutDataMapRequest.createWithAutoAppendedId("/image");
        dataMap.getDataMap().putAsset("profileImage", asset);
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);

    }
    //</editor-fold>

    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {

    }
}
