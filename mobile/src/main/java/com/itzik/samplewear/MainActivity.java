    package com.itzik.samplewear;

import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
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
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener
{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String WEAR_MESSAGE_PATH = "/message";
    private static final String START_ACTIVITY = "/start_activity";
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static final String COUNT_KEY = "com.example.key.count";
    private static final long TIMEOUT_MS = 3000;
    int count = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false); // Maintain state while resolving an error

        Button wearButton = (Button) findViewById(R.id.wearButton);
        wearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int notificationId = 001;
                count++;

                Intent openWearActivityIntent = new Intent("com.myCompany.myApp.ACTION.openWearActivity");
                PendingIntent p = PendingIntent.getActivity(MainActivity.this, 0, openWearActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Action openWearActivityIntentAction =new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "open Activity", p).build();

                Intent mainActivityIntent = new Intent(MainActivity.this, MainActivity.class);
                PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Create the openMainAcivityAction
                NotificationCompat.Action openMainAcivityAction =new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Open app, Action Sample", mainActivityPendingIntent)
                                .build();

                NotificationCompat.BigPictureStyle bigImageStyle = new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)).setSummaryText("Big image style");
                bigImageStyle.setBigContentTitle("Big image style content title");

                NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
                bigStyle.bigText("this is event description in big style: " + count +"");

                NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
                secondPageStyle.setBigContentTitle("Page 2")
                        .bigText("A lot of text...");

                Notification secondPageNotification =new NotificationCompat.Builder(MainActivity.this)
                                .setStyle(secondPageStyle)
                                .build();

                Notification bigImagePageNotification =new NotificationCompat.Builder(MainActivity.this)
                        .setStyle(bigImageStyle)
                        .build();

                NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender()
                        .setHintHideIcon(false)
                        .setBackground(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .addPage(secondPageNotification)
                        .addPage(bigImagePageNotification);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentIntent(mainActivityPendingIntent)
                        .setContentTitle("Title")
                        .extend(wearableExtender)

                        .addAction(openMainAcivityAction)
                        .addAction(openWearActivityIntentAction)
                        .setStyle(bigStyle)
                        .setContentText("Android Wear Notification");

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                notificationManager.notify(notificationId, notificationBuilder.build());
            }
        });


        createApiClient();

        findViewById(R.id.send_message).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendMessage(WEAR_MESSAGE_PATH, "message is here");

            }
        });

        findViewById(R.id.send_data).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                putDataRequest();
            }
        });
    }

    private void createApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
    }

    private void sendMessage(final String path, final String text)
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
        sendMessage(START_ACTIVITY, "");
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
            mResolvingError = true;
        }

        if (mResolvingError)
        {
            // Already attempting to resolve an error.
            return;
        }
        if (connectionResult.hasResolution())
        {
            try
            {
                mResolvingError = true;
                connectionResult.startResolutionForResult(MainActivity.this, REQUEST_RESOLVE_ERROR);
            }
            catch (IntentSender.SendIntentException e)
            {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        }
        else
        {
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
        Log.d(LOG_TAG, "onConnectionFailed: " + connectionResult);

    }
    //</editor-fold>

    //<editor-fold desc="DataApi.DataListener">
    @Override
    public void onDataChanged(DataEventBuffer dataEvents)
    {
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
                if (event.getDataItem().getUri().getPath().equals("/image"))
                {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Asset profileAsset = dataMapItem.getDataMap().getAsset("profileImage");
                    Bitmap bitmap = loadBitmapFromAsset(profileAsset);
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
    }
    //</editor-fold>

    //<editor-fold desc="Error Dialog for Api Connection failures">
    private void showErrorDialog(int errorCode)
    {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed()
    {
        mResolvingError = false;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {
        Log.d(LOG_TAG, "onMessageReceived(), ");
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment
    {
        public ErrorDialogFragment()
        {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog)
        {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
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

    private void putDataRequest()
    {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Asset asset = createAssetFromBitmap(bitmap);

        PutDataMapRequest dataMap = PutDataMapRequest.createWithAutoAppendedId("/image");
        dataMap.getDataMap().putAsset("profileImage", asset);
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);

    }
    //</editor-fold>

    private Messenger mService;
    private boolean mIsBound = false;
    @Override
    protected void onStart()
    {
        super.onStart();
        if (!mResolvingError)
        {
            mGoogleApiClient.connect();
        }
    }


    @Override
    protected void onStop()
    {
        super.onStop();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);//Maintain state while resolving an error
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_RESOLVE_ERROR) //Once the user completes the resolution provided by startResolutionForResult()
            // {
            mResolvingError = false;
        if (resultCode == RESULT_OK)
        {
            // Make sure the app is not already connected or attempting to connect
            if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected())
            {
                mGoogleApiClient.connect();
            }
        }
    }

    //    public static final String START_ACTIVITY_PATH = "/start/MainActivity";
    //        private void sendStartActivityMessage(String nodeId)
    //        {
    //            Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, START_ACTIVITY_PATH, new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>()
    //                    {
    //                        @Override
    //                        public void onResult(MessageApi.SendMessageResult sendMessageResult)
    //                        {
    //                            if (!sendMessageResult.getStatus().isSuccess())
    //                            {
    //                                Log.e(LOG_TAG, "Failed to send message with status code: " + sendMessageResult.getStatus().getStatusCode());
    //                            }
    //                        }
    //                    });
    //        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
