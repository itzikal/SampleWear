package com.itzik.samplewear.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Created by Itzik on 3/22/2015.
 */
public class CompassUtil implements SensorEventListener
{
    public interface OnCompassDegreeChangedListener
    {
        void onDegreeChanged(float degree);
    }


    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mPrevDegree = 0f;
    private float mCurrentDegree;
    private ArrayList<OnCompassDegreeChangedListener> mListeners = new ArrayList<>();

    public void addListener(OnCompassDegreeChangedListener listener)
    {

        if (mListeners.isEmpty())
        {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
        }
        mListeners.add(listener);
    }

    public void removeListener(OnCompassDegreeChangedListener listener)
    {
        mListeners.remove(listener);
        if(mListeners.isEmpty())
        {
            mSensorManager.unregisterListener(this);
        }
    }

    public void initCompass(Context context)
    {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor == mAccelerometer)
        {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        }
        else if (event.sensor == mMagnetometer)
        {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet)
        {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

            mPrevDegree = -azimuthInDegress;

            if (!(mPrevDegree - mCurrentDegree > -1.5 && mPrevDegree - mCurrentDegree < 1.5))
            {
                setCurrentDegree(mPrevDegree);
            }

        }
    }

    public void setCurrentDegree(float currentDegree)
    {
        mCurrentDegree = currentDegree;
        for (OnCompassDegreeChangedListener listener : mListeners)
        {
            listener.onDegreeChanged(currentDegree);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

}
