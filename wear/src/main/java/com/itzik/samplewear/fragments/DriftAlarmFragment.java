package com.itzik.samplewear.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.itzik.common.GoogleApiWrapper;
import com.itzik.samplewear.R;


/**
 * Created by Itzik on 3/24/2015.
 */
public class DriftAlarmFragment extends Fragment
{
    private TextView mValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View stub = inflater.inflate(R.layout.fragment_drift_alarm, container, false);
        mValue = (TextView) stub.findViewById(R.id.radius_value);
        SeekBar seekbar = ((SeekBar) stub.findViewById(R.id.drift_seek_bar));
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
                mValue.setText(Integer.toString(progress + 100));
            }
        });

        stub.findViewById(R.id.set_radius).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                GoogleApiWrapper.getInstance().sendMessage("/set_alarm", mValue.getText().toString());
            }
        });

        return stub;
    }
}
