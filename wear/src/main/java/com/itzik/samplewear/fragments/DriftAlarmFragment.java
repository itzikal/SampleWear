package com.itzik.samplewear.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.itzik.samplewear.R;
import com.itzik.samplewear.utils.AlarmUtil;


/**
 * Created by Itzik on 3/24/2015.
 */
public class DriftAlarmFragment extends Fragment
{
    private TextView mValue;
    private TextView mState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View stub = inflater.inflate(R.layout.fragment_drift_alarm, container, false);
        mValue = (TextView) stub.findViewById(R.id.radius_value);
        mState = (TextView) stub.findViewById(R.id.alarm_status);
        SeekBar seekbar = ((SeekBar) stub.findViewById(R.id.drift_seek_bar));

        mValue.setText(AlarmUtil.getInstance().getRadiusDistance());
        mState.setText((AlarmUtil.getInstance().isOn()? "Enabled": "Disabled"));

        seekbar.setProgress(Integer.parseInt(AlarmUtil.getInstance().getRadiusDistance()));

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                String distance = Integer.toString(progress + 100);
                mValue.setText(distance);
                AlarmUtil.getInstance().setRadiusDistance(distance);
            }
        });

        stub.findViewById(R.id.set_radius).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlarmUtil.getInstance().enableAlarm();
                mState.setText((AlarmUtil.getInstance().isOn() ? "Enabled" : "Disabled"));
            }
        });

        stub.findViewById(R.id.disable_alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AlarmUtil.getInstance().disableAlarm();
                mState.setText((AlarmUtil.getInstance().isOn() ? "Enabled" : "Disabled"));
            }
        });

        return stub;
    }
}
