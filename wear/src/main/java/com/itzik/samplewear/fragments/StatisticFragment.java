package com.itzik.samplewear.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.itzik.common.GoogleApiWrapper;
import com.example.itzik.common.LocationDataSample;
import com.google.android.gms.wearable.MessageEvent;
import com.google.gson.Gson;
import com.itzik.samplewear.R;
import com.itzik.samplewear.utils.CompassUtil;
import com.itzik.samplewear.utils.SpeechCommandHandler;
import com.itzik.samplewear.views.DataValuePresenterController;
import com.itzik.samplewear.views.DataValuePresenterView;
import com.itzik.samplewear.views.ViewDataType;

/**
 * Created by Itzik on 3/24/2015.
 */
public class StatisticFragment extends Fragment implements View.OnClickListener, GoogleApiWrapper.OnMessageReceivedListener, CompassUtil.OnCompassDegreeChangedListener
{
    private static final String LOG_TAG = StatisticFragment.class.getSimpleName();
    private static final String MOVE_COMPASS = "/move_comapss";
    private static final int SPEECH_REQUEST_CODE = 0;
    private Button mSpeedButton;
    private Button mDistanceButton;
    private Button mHeadingButton;


    private DataValuePresenterController mDataValuePresenterController;
    //    private TextView mText;
    private ImageView mCompass;
    private CompassUtil mCompassUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View stub = inflater.inflate(R.layout.fragment_statistic, container, false);

        mDataValuePresenterController = new DataValuePresenterController(getActivity(), (DataValuePresenterView) stub.findViewById(R.id.dimention_presenter_view));

        mCompass = (ImageView) stub.findViewById(R.id.compass);
        mSpeedButton = (Button) stub.findViewById(R.id.btn_speed_layout);
        mDistanceButton = (Button) stub.findViewById(R.id.btn_distance_layout);
        mHeadingButton = (Button) stub.findViewById(R.id.btn_heading_layout);
        mSpeedButton.setOnClickListener(this);
        mDistanceButton.setOnClickListener(this);
        mHeadingButton.setOnClickListener(this);
        mSpeedButton.callOnClick();
        stub.findViewById(R.id.voice_command).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                displaySpeechRecognizer();
            }
        });
        mCompassUtil = new CompassUtil();
        mCompassUtil.initCompass(getActivity());

        return stub;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        GoogleApiWrapper.getInstance().addListener(this);
        mCompassUtil.addListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        GoogleApiWrapper.getInstance().removeListener(this);
        mCompassUtil.removeListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_speed_layout:
                mDataValuePresenterController.setSelectedDataType(ViewDataType.Speed);
                break;
            case R.id.btn_distance_layout:
                mDataValuePresenterController.setSelectedDataType(ViewDataType.Distance);
                break;
            case R.id.btn_heading_layout:
                mDataValuePresenterController.setSelectedDataType(ViewDataType.Heading);
                break;
        }
        mSpeedButton.setSelected(false);
        mDistanceButton.setSelected(false);
        mHeadingButton.setSelected(false);
        v.setSelected(true);
        GoogleApiWrapper.getInstance().sendMessage("/itzik", "message");

    }

    @Override
    public void onMassageReceived(final MessageEvent messageEvent)
    {
        Log.d(LOG_TAG, "onMassageReceived(), " + messageEvent.getPath());
        if (messageEvent.getPath().equals(MOVE_COMPASS))
        {
            Log.d(LOG_TAG, "onMassageReceived(), " + messageEvent.getPath() + " moving compass");
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    int degree = Integer.valueOf(new String(messageEvent.getData()));
                    Log.d(LOG_TAG, "run(), degree:" + degree);
                    onDegreeChanged(degree);
                }
            });
        }
        else if (messageEvent.getPath().equals("/location"))
        {
            Gson gson = new Gson();
            String s = new String(messageEvent.getData());
            final LocationDataSample locationDataSample = gson.fromJson(s, LocationDataSample.class);
            Log.d(LOG_TAG, "onMassageReceived(), Location: " + locationDataSample);
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mDataValuePresenterController.updateLocationSample(locationDataSample);
                }
            });

        }

    }

    @Override
    public void onDegreeChanged(float degree)
    {
        Log.d(LOG_TAG, "onDegreeChanged(), start animation");

        mCompass.setPivotX(mCompass.getWidth() / 2);
        mCompass.setPivotY(mCompass.getHeight() / 2);
        mCompass.setRotation(degree);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == this.getActivity().RESULT_OK)
        {
            SpeechCommandHandler  speechCommandHandler= new SpeechCommandHandler();
            speechCommandHandler.handleCommand(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void displaySpeechRecognizer()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }
}
