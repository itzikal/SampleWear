package com.itzik.samplewear.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.itzik.samplewear.R;

/**
 * Created by Itzik on 3/28/2015.
 */
public class DriftAlarmActivity extends Activity
{
    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drift_alarm);
        View viewById = findViewById(R.id.alarm_frame);
        Animation anim = new AlphaAnimation(0, 255);
        anim.setDuration(400);
        anim.setRepeatMode(Animation.INFINITE);
        viewById.setAnimation(anim);
        anim.start();
        mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mVibrator.vibrate(10000);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mVibrator.cancel();
    }
}
