package com.itzik.samplewear;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;

public class WearMainActivity extends Activity implements GoogleApiWrapper.OnMessageReceivedListener//MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener
{
    private static final String LOG_TAG = WearMainActivity.class.getSimpleName();
    public static final String START_ACTIVITY_PATH = "/start/MainActivity";
    private static final String WEAR_MESSAGE_PATH = "/message";
    private TextView mTextView;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static final String COUNT_KEY = "com.example.key.count";
    private static final long TIMEOUT_MS = 3000;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private Button mButton;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener()
        {
            @Override
            public void onLayoutInflated(WatchViewStub stub)
            {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mButton = (Button) stub.findViewById(R.id.button);
                mButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Log.d(LOG_TAG, "onClick(), sending message");
                        GoogleApiWrapper.getInstance().sendMessage(WEAR_MESSAGE_PATH, "itzik");
                    }
                });
                mImage = (ImageView) stub.findViewById(R.id.image);
            }
        });
        GoogleApiWrapper.initInstance(this);

//        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
//        connect();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        GoogleApiWrapper.getInstance().onResume();
        GoogleApiWrapper.getInstance().addListener(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        GoogleApiWrapper.getInstance().onPause();
        GoogleApiWrapper.getInstance().removeListener(this);
    }

    @Override
    public void onMassageReceived(final MessageEvent messageEvent)
    {
        Log.d(LOG_TAG, "onMessageReceived(), ");
        if (messageEvent.getPath().equals(WEAR_MESSAGE_PATH))
        {
            Log.d(LOG_TAG, "onMessageReceived(), is wear message");
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.d(LOG_TAG, "onMessageReceived(), set text");
                    mTextView.setText(new String(messageEvent.getData()));
                }
            });
        }
    }

    //    private void connect()
//    {
//        Log.d(LOG_TAG, "connect(), ");
//        if (mGoogleApiClient != null && !(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()))
//        {
//            Log.d(LOG_TAG, "connect(), connecting");
//            mGoogleApiClient.connect();
//        }
//    }

//    @Override
//    public void onMessageReceived(final MessageEvent messageEvent)
//    {
//        Log.d(LOG_TAG, "onMessageReceived(), ");
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
//
//    }
//
//    private void sendStartActivityMessage(String nodeId)
//    {
//        Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, START_ACTIVITY_PATH, new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>()
//        {
//            @Override
//            public void onResult(MessageApi.SendMessageResult sendMessageResult)
//            {
//                if (!sendMessageResult.getStatus().isSuccess())
//                {
//                    Log.e(LOG_TAG, "Failed to send message with status code: " + sendMessageResult.getStatus().getStatusCode());
//                }
//            }
//        });
//    }
//
//    private void sendMessage(final String path, final String text)
//    {
//        new Thread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
//                for (Node node : nodes.getNodes())
//                {
//                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, text.getBytes()).await();
//                }
//
//                //                runOnUiThread(new Runnable()
//                //                {
//                //                    @Override
//                //                    public void run()
//                //                    {
//                //
//                //                    }
//                //                });
//            }
//        }).start();
//    }
//
//    @Override
//    protected void onPause()
//    {
//        super.onPause();
//        Log.d(LOG_TAG, "onPause(), ");
//        if (mGoogleApiClient != null)
//        {
//            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
//            Wearable.DataApi.removeListener(mGoogleApiClient, this);
//            if (mGoogleApiClient.isConnected())
//            {
//                mGoogleApiClient.disconnect();
//            }
//        }
//
//    }
//
//    //<editor-fold desc="GoogleApiClient.ConnectionCallbacks">
//    @Override
//    public void onConnected(Bundle connectionHint)
//    {
//        // Now you can use the Data Layer API
//        Log.d(LOG_TAG, "onConnected: adding listener " + connectionHint);
//        Wearable.DataApi.addListener(mGoogleApiClient, this);
//        Wearable.MessageApi.addListener(mGoogleApiClient, this);
//    }
//
//    @Override
//    public void onConnectionSuspended(int cause)
//    {
//        Log.d(LOG_TAG, "onConnectionSuspended: " + cause);
//    }
//    //</editor-fold>
//
//    //<editor-fold desc="GoogleApiClient.OnConnectionFailedListener">
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult)
//    {
//        Log.d(LOG_TAG, "onConnectionFailed(), ");
//        if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE)
//        {
//            // The Android Wear app is not installed
//            mResolvingError = true;
//        }
//
//        if (mResolvingError)
//        {
//            // Already attempting to resolve an error.
//            return;
//        }
//        if (connectionResult.hasResolution())
//        {
//            try
//            {
//                mResolvingError = true;
//                connectionResult.startResolutionForResult(WearMainActivity.this, REQUEST_RESOLVE_ERROR);
//            }
//            catch (IntentSender.SendIntentException e)
//            {
//                // There was an error with the resolution intent. Try again.
//                mGoogleApiClient.connect();
//            }
//        }
//        else
//        {
//            mResolvingError = true;
//        }
//        Log.d(LOG_TAG, "onConnectionFailed: " + connectionResult);
//
//    }
//    //</editor-fold>
//
//    //<editor-fold desc="DataApi.DataListener">
//    @Override
//    public void onDataChanged(DataEventBuffer dataEvents)
//    {
//        Log.d(LOG_TAG, "onDataChanged(), ");
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
//                if (event.getDataItem().getUri().getPath().contains("/image"))
//                {
//                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
//                    Asset profileAsset = dataMapItem.getDataMap().getAsset("profileImage");
//                    final Bitmap bitmap = loadBitmapFromAsset(profileAsset);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run()
//                        {
//                            mImage.setImageBitmap(bitmap);
//                            mTextView.setText("Got Data message");
//                        }
//                    });
//
//                }
//            }
//            else if (event.getType() == DataEvent.TYPE_DELETED)
//            {
//                // DataItem deleted
//            }
//        }
//    }
//
//    private void updateCount(int c)
//    {
//        Log.d(LOG_TAG, "updateCount(), " + c + "");
//    }
//    //</editor-fold>
//
//
//    //<editor-fold desc="Send data request (example of bitmap)">
//    public Bitmap loadBitmapFromAsset(Asset asset)
//    {
//        if (asset == null)
//        {
//            throw new IllegalArgumentException("Asset must be non-null");
//        }
//        ConnectionResult result = mGoogleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
//        if (!result.isSuccess())
//        {
//            return null;
//        }
//        // convert asset into a file descriptor and block until it's ready
//        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset).await().getInputStream();
//
//        //        mGoogleApiClient.disconnect(); was in the example but seems unnecessary.
//
//        if (assetInputStream == null)
//        {
//            Log.w(LOG_TAG, "Requested an unknown Asset.");
//            return null;
//        }
//        // decode the stream into a bitmap
//        return BitmapFactory.decodeStream(assetInputStream);
//    }
//
//    private static Asset createAssetFromBitmap(Bitmap bitmap)
//    {
//        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
//        return Asset.createFromBytes(byteStream.toByteArray());
//    }
//
//    private void putDataRequest()
//    {
//
//        Bitmap bitmap = BitmapFactory.decodeFile("path to file");
//        Asset asset = createAssetFromBitmap(bitmap);
//        PutDataRequest request = PutDataRequest.create("/image");
//        request.putAsset("profileImage", asset);
//        Wearable.DataApi.putDataItem(mGoogleApiClient, request);
//    }
//    //</editor-fold>
}
