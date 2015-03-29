package com.itzik.samplewear.utils;

import com.example.itzik.common.GoogleApiWrapper;

/**
 * Created by Oren on 3/28/15.
 */
public class AlarmUtil {

    private boolean mIsOn;
    private String mRadiusDistance;
    public static AlarmUtil mAlarmUtil;

    private AlarmUtil(){
        mIsOn = false;
        mRadiusDistance = "0";
    }

    public static AlarmUtil getInstance(){
        if (mAlarmUtil == null)
            mAlarmUtil = new AlarmUtil();
        return mAlarmUtil;
    }

    public void setAlarmStatus(boolean state, String radius){
        mIsOn = state;
        mRadiusDistance = radius;

    }

    public boolean isOn() {
        return mIsOn;
    }

    public void setIsOn(boolean mIsOn) {
        this.mIsOn = mIsOn;
    }

    public String getRadiusDistance() {
        return mRadiusDistance;
    }

    public void setRadiusDistance(String distance) {
        this.mRadiusDistance = distance;
    }

    public void enableAlarm()
    {
        mIsOn = true;
        GoogleApiWrapper.getInstance().sendMessage("/set_alarm", mRadiusDistance);
    }

    public void disableAlarm()
    {
        mIsOn = false;
        GoogleApiWrapper.getInstance().sendMessage("/disable_alarm", "");
    }
}
