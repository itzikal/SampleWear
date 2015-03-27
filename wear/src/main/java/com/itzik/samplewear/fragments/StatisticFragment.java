package com.itzik.samplewear.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageEvent;
import com.itzik.samplewear.R;
import com.itzik.samplewear.utils.GoogleApiWrapper;
import com.itzik.samplewear.utils.OnMessageReceivedListener;
import com.itzik.samplewear.views.DataValuePresenterController;

/**
 * Created by Itzik on 3/24/2015.
 */
public class StatisticFragment extends Fragment implements View.OnClickListener, OnMessageReceivedListener
{
    private Button mSpeedButton;
    private Button mDistanceButton;
    private Button mHeadingButton;
    private DataValuePresenterController mDataValuePresenterController;
    private TextView mText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View stub = inflater.inflate(R.layout.fragment_statistic, container, false);

//        mDataValuePresenterController = new DataValuePresenterController(getActivity(), (DataValuePresenterView) stub.findViewById(R.id.dimention_presenter_view));

//        mDataValuePresenterController.setDataSetVisibility(View.VISIBLE);

        mSpeedButton = (Button)stub.findViewById(R.id.btn_speed_layout);
        mDistanceButton = (Button)stub.findViewById(R.id.btn_distance_layout);
        mHeadingButton = (Button)stub.findViewById(R.id.btn_heading_layout);
        mSpeedButton.setOnClickListener(this);
        mDistanceButton.setOnClickListener(this);
        mHeadingButton.setOnClickListener(this);
        mText = (TextView)stub.findViewById(R.id.texting);
//        mSpeedButton.callOnClick();

        return stub;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        GoogleApiWrapper.getInstance().addListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        GoogleApiWrapper.getInstance().removeListener(this);
    }

    @Override
    public void onClick(View v)
    {
//        switch (v.getId())
//        {
//            case R.id.btn_speed_layout:
//                mDataValuePresenterController.setSelectedDataType(ViewDataType.Speed);
//                break;
//            case R.id.btn_distance_layout:
//                mDataValuePresenterController.setSelectedDataType(ViewDataType.Distance);
//                break;
//            case R.id.btn_heading_layout:
//                mDataValuePresenterController.setSelectedDataType(ViewDataType.Heading);
//                break;
//        }
//        mSpeedButton.setSelected(false);
//        mDistanceButton.setSelected(false);
//        mHeadingButton.setSelected(false);
//        v.setSelected(true);
        GoogleApiWrapper.getInstance().sendMessage("/itzik", "message");

    }

    @Override
    public void onMassageReceived(final MessageEvent messageEvent)
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mText.setText("Message received " +messageEvent.getPath());
            }
        });

    }
}
