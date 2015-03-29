package com.itzik.samplewear;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itzik.common.GoogleApiWrapper;
import com.example.itzik.common.LocationDataSample;
import com.google.android.gms.wearable.MessageEvent;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends ActionBarActivity implements GoogleApiWrapper.OnMessageReceivedListener//GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener
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
    private boolean mResolvingError = false;
    private Thread mThread;
    private int mLocationCount = 0;
    private ArrayList<LocationDataSample> mLocationsList;
    private SailingStatisticsCalculator mSailingStatisticsCalculator = new SailingStatisticsCalculator();
    private LocationDataSample mStartLocation;
    private float mMaxDriftDistance;
    private TextView mSailingStatus;
    private TextView mDriftDistanece;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false); // Maintain state while resolving an error
        mSailingStatus = (TextView) findViewById(R.id.sailing_status);
        mDriftDistanece = (TextView) findViewById(R.id.drift_distance);

        findViewById(R.id.test_alarm_activity).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {GoogleApiWrapper.getInstance().sendMessage("/start_alarm_activity","");
            }
        });
        findViewById(R.id.start_sailing).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startSendingLocations();
            }
        });
        findViewById(R.id.pause_sailing).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pauseSailing();
            }
        });
        findViewById(R.id.stop_sailing).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stopSailing();
            }
        });

        ((SeekBar) findViewById(R.id.compass_mover)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser)
            {
                progress = progresValue;
                GoogleApiWrapper.getInstance().sendMessage("/move_comapss", progresValue + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });

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

                NotificationCompat.Action openWearActivityIntentAction = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "open Activity", p).build();

                Intent mainActivityIntent = new Intent(MainActivity.this, MainActivity.class);
                PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Create the openMainAcivityAction
                NotificationCompat.Action openMainAcivityAction = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Open app, Action Sample", mainActivityPendingIntent).build();

                NotificationCompat.BigPictureStyle bigImageStyle = new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)).setSummaryText("Big image style");
                bigImageStyle.setBigContentTitle("Big image style content title");

                NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
                bigStyle.bigText("this is event description in big style: " + count + "");

                NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
                secondPageStyle.setBigContentTitle("Page 2").bigText("A lot of text...");

                Notification secondPageNotification = new NotificationCompat.Builder(MainActivity.this).setStyle(secondPageStyle).build();

                Notification bigImagePageNotification = new NotificationCompat.Builder(MainActivity.this).setStyle(bigImageStyle).build();

                NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender().setHintHideIcon(false).setBackground(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)).addPage(secondPageNotification).addPage(bigImagePageNotification);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this).setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)).setContentIntent(mainActivityPendingIntent).setContentTitle("Title").extend(wearableExtender)

                        .addAction(openMainAcivityAction).addAction(openWearActivityIntentAction).setStyle(bigStyle).setContentText("Android Wear Notification");

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                notificationManager.notify(notificationId, notificationBuilder.build());
            }
        });
        GoogleApiWrapper.initInstance(this);

        findViewById(R.id.send_message).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GoogleApiWrapper.getInstance().sendMessage(WEAR_MESSAGE_PATH, "message is here");

            }
        });

        findViewById(R.id.send_data).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GoogleApiWrapper.getInstance().putDataRequest();
            }
        });
        mLocationCount = 0;
        mLocationsList = LocationsLoader.getLocationsFromDataBase();
        mSailingStatisticsCalculator.startNewSailing();
    }


    private void stopSailing()
    {
        if (mSendMockingLocationFromFile != null)
        {
            mSendMockingLocationFromFile.interrupt();
        }
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mSailingStatus.setText("Sailing stoped");
            }
        });
        mLocationCount = 0;
        mSailingStatisticsCalculator.startNewSailing();
        GoogleApiWrapper.getInstance().sendMessage("/sailing_stop", "");

    }

    private void pauseSailing()
    {
        if (mSendMockingLocationFromFile != null)
        {
            mSendMockingLocationFromFile.interrupt();
        }
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mSailingStatus.setText("Sailing paused on location: " + mLocationCount + "/" + mLocationsList.size());
            }
        });
    }

    private void startSendingLocations()
    {

        GoogleApiWrapper.getInstance().sendMessage("/sailing_start", "");
        startMockingFromFile();
    }

    private Thread mSendMockingLocationFromFile;

    public void startMockingFromFile()
    {

        if (mLocationsList == null || mLocationsList.size() <= 0)
        {
            Toast.makeText(MainActivity.this, "No Locations Loaded", Toast.LENGTH_LONG).show();
            return;
        }
        mSendMockingLocationFromFile = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(LOG_TAG, "run(), route started");

                long l = Calendar.getInstance().getTime().getTime();
                for (; mLocationCount < mLocationsList.size(); mLocationCount++)
                {
                    if (mSendMockingLocationFromFile.isInterrupted())
                    {
                        break;
                    }
                    LocationDataSample locationDataSample = mLocationsList.get(mLocationCount);

                    if (locationDataSample.getTime() == 0)
                    {
                        locationDataSample.setTime(l);
                        l = l + 1000 * 60;// 1 minute
                    }
                    else
                    {
                        l = locationDataSample.getTime();
                    }


                    onLocationChanged(locationDataSample);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mSailingStatus.setText("Sailing in progress location: " + mLocationCount + "/" + mLocationsList.size());
                        }
                    });
                    Log.d(LOG_TAG, "run(), route ongoing, current thread: " + Thread.currentThread().getId());
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        mSendMockingLocationFromFile.setName("MockingLocationThread");
        mSendMockingLocationFromFile.start();
    }


    private void onLocationChanged(LocationDataSample location)
    {
        mSailingStatisticsCalculator.addLocation(location);

        if (mStartLocation != null && location.distanceTo(mStartLocation) > mMaxDriftDistance)
        {
            Log.d(LOG_TAG, "onLocationChanged(), Alarm distance reacted - activate alarm");
            GoogleApiWrapper.getInstance().sendMessage("/Alarm", "start");
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mDriftDistanece.setText("Alarm started");
                }
            });
            mStartLocation = null;
            //start alarm on clock.
        }
        Gson gson = new Gson();
        String s = gson.toJson(location);
        GoogleApiWrapper.getInstance().sendMessage("/location", s);
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

    @Override
    public void onMassageReceived(final MessageEvent messageEvent)
    {
        Log.d(LOG_TAG, "onMassageReceived(), ");
        if (messageEvent.getPath().equals("/set_alarm"))
        {
            Log.d(LOG_TAG, "onMassageReceived(), set alarm");
            mStartLocation = mLocationsList.get(mLocationCount);
            mMaxDriftDistance = Integer.parseInt(new String(messageEvent.getData()));
            Log.d(LOG_TAG, " set alarm(), location: " + mLocationCount + ", distance: " + mMaxDriftDistance);
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mDriftDistanece.setText("set to " + mMaxDriftDistance);
                }
            });

        }
    }
}
