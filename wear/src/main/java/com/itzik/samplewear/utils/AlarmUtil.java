package com.itzik.samplewear.utils;

/**
 * Created by Oren on 3/28/15.
 */
public class AlarmUtil {

    private boolean mIsOn;
    private Integer mSeekLocation;
    public static AlarmUtil mAlarmUtil;

    private AlarmUtil(){
        mIsOn = false;
        mSeekLocation = 0;
    }

    public static AlarmUtil getInstance(){
        if (mAlarmUtil == null)
            mAlarmUtil = new AlarmUtil();
        return mAlarmUtil;
    }

    public void setAlarmStaus(boolean state,Integer location){
        mIsOn = state;
        mSeekLocation = location;

    }

    public boolean ismIsOn() {
        return mIsOn;
    }

    public void setmIsOn(boolean mIsOn) {
        this.mIsOn = mIsOn;
    }

    public Integer getmSeekLocation() {
        return mSeekLocation;
    }

    public void setmSeekLocation(Integer mSeekLocation) {
        this.mSeekLocation = mSeekLocation;
    }
}
