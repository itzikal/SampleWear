package com.itzik.samplewear.utils;

import android.util.Log;

import com.example.itzik.common.GoogleApiWrapper;

import java.util.List;

/**
 * Created by Itzik on 3/29/2015.
 */
public class SpeechCommandHandler
{
    private static final String LOG_TAG = SpeechCommandHandler.class.getSimpleName();

    public void handleCommand(List<String> results)
    {

        String spokenText = results.get(0);
        Log.d("spoken text from the activity is : " + LOG_TAG, spokenText);
        String[] text = spokenText.split(" ");
        boolean mDriftInText = false;
        boolean mStop = false;
        boolean mDocking = false;
        boolean mSaying = false;
        int mSayingLocation  = 0;
        boolean mMark = true;


        String mDriftDistance = "";
        int location = 0;
        for (String t : text)
        {
            location++;
            if (isInteger(t, 10))
            {
                mDriftDistance = t;

            }
            else if (t.contains("drift")) mDriftInText = true;

            else if (t.contains("start")) mStop = true;

            else if (t.contains("sailing")) mDocking = true;

            else if (t.contains("saying"))
            {
                mSaying = true;
                mSayingLocation = location;
            }


            else if (t.contains("mark"))
            {
                if (!mSaying) {
                    mMark = true;
                }
            }




        }


        if (mDriftInText && !mDriftDistance.equals(""))
        {
            AlarmUtil.getInstance().setAlarmStatus(true, (mDriftDistance));
            AlarmUtil.getInstance().enableAlarm();

        }
        if (mDocking && mStop)
        {
            AlarmUtil.getInstance().disableAlarm();
        }

        if (mMark && mSaying){
            StringBuilder sb = new StringBuilder();
            for (int i=mSayingLocation;i<text.length;i++) {
                sb.append(text[i] + " ");
            }
            GoogleApiWrapper.getInstance().sendMessage("/mark_text", sb.toString());
        }

    }

    public static boolean isInteger(String s, int radix)
    {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++)
        {
            if (i == 0 && s.charAt(i) == '-')
            {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

}
