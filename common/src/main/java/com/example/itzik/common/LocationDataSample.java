package com.example.itzik.common;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Itzik on 12/25/2014.
 */
public class LocationDataSample implements Parcelable
{

    private double mLatitude;
    private double mLongitude;
    private long mTime;
    private String mSailingId;
    private boolean mIsLegStart = false;
    private boolean mIsAutomatedLeg = false;
    private float mSpeed;
    private double mDistanceFromPreviousLocation;
    private double mArialDistanceFormStart;
    private float mAverageSpeed;
    private float mMaxSpeed;
    private float mHeadingFromStart;
    private float mBearing;
    private Location mLocation;
    private int mLegIndex;
    private double mTotalDistance;
    private float mDistanceFromLeg;
    private float mLegHeading;
    private int mId;

    public int getId()
    {
        return mId;
    }

    public void setId(int Id)
    {
        mId = Id;
    }

    public float getDistanceFromLeg()
    {
        return mDistanceFromLeg;
    }

    public void setDistanceFromLeg(float DistanceFromLeg)
    {
        mDistanceFromLeg = DistanceFromLeg;
    }

    public LocationDataSample(Location location)
    {
        this(location.getLatitude(), location.getLongitude());
        mTime  = location.getTime();
        mBearing = location.hasBearing()? location.getBearing() : -1;
        mSpeed = location.getSpeed();
        mLocation = location;
    }

    public LocationDataSample(double latitude, double longitude)
    {
        mLatitude = latitude;
        mLongitude = longitude;
        mLocation = new Location("gps");
        mLocation.setLatitude(latitude);
        mLocation.setLongitude(longitude);
        mBearing = -1;
    }

    public float distanceTo(LocationDataSample that)
    {
        return getLocation().distanceTo(that.getLocation());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeDouble(this.mLatitude);
        dest.writeDouble(this.mLongitude);
    }

    public LocationDataSample(Parcel in)
    {
        this.mLatitude = in.readDouble();
        this.mLongitude = in.readDouble();
    }

    public static final Creator CREATOR = new Creator()
    {
        @Override
        public LocationDataSample createFromParcel(Parcel source)
        {
            return new LocationDataSample(source);
        }

        @Override
        public LocationDataSample[] newArray(int size)
        {
            return new LocationDataSample[size];
        }
    };

    public double getLongitude()
    {
        return mLongitude;
    }

    public double getLatitude()
    {
        return mLatitude;
    }

    public long getTime()
    {
        return mTime;
    }

    public void setTime(long time)
    {
        mTime = time;
    }

    public String getSailingId()
    {
        return mSailingId;
    }

    public void setSailingId(String sailing)
    {
        mSailingId = sailing;
    }

    public boolean isLegStart()
    {
        return mIsLegStart;
    }

    public void setIsLegStart(boolean isLeg)
    {
        mIsLegStart = isLeg;
    }

    public float getSpeed()
    {
        return mSpeed;
    }

    public void setSpeed(float speed)
    {
        mSpeed = speed;
    }

    public double getDistanceFromPreviousLocation()
    {
        return mDistanceFromPreviousLocation;
    }

    public void setDistanceFromPreviousLocation(double distanceFromPreviousLocation)
    {
        mDistanceFromPreviousLocation = distanceFromPreviousLocation;
    }

    public double getArialDistanceFormStart()
    {
        return mArialDistanceFormStart;
    }

    public void setArialDistanceFormStart(double arialDistanceFormStart)
    {
        mArialDistanceFormStart = arialDistanceFormStart;
    }

    public float getAverageSpeed()
    {
        return mAverageSpeed;
    }

    public void setAverageSpeed(float averageSpeed)
    {
        mAverageSpeed = averageSpeed;
    }

    public float getMaxSpeed()
    {
        return mMaxSpeed;
    }

    public void setMaxSpeed(float maxSpeed)
    {
        mMaxSpeed = maxSpeed;
    }

    public void setHeadingFromStart(float headingFromStart)
    {
        mHeadingFromStart = headingFromStart;
    }

    public float getHeadingFromStart()
    {
        return mHeadingFromStart;
    }

    public float getBearing()
    {
        return mBearing;
    }

    public void setBearing(float bearing)
    {
        mBearing = bearing;
    }

    public Location getLocation()
    {
        return mLocation;
    }

    public int getLegIndex()
    {
        return mLegIndex;
    }

    public void setLegIndex(int legIndex)
    {
        mLegIndex = legIndex;
    }

    public void setTotalDistance(double totalDistance)
    {
        mTotalDistance = totalDistance;
    }

    public double getTotalDistance()
    {
        return mTotalDistance;
    }

    @Override
    public String toString()
    {
        return "LocationDataSample{" +
                "mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                ", mTime=" + mTime +
                ", mSailingId='" + mSailingId + '\'' +
                ", mIsLegStart=" + mIsLegStart +
                ", mSpeed=" + mSpeed +
                ", mDistanceFromPreviousLocation=" + mDistanceFromPreviousLocation +
                ", mArialDistanceFormStart=" + mArialDistanceFormStart +
                ", mAverageSpeed=" + mAverageSpeed +
                ", mMaxSpeed=" + mMaxSpeed +
                ", mHeadingFromStart=" + mHeadingFromStart +
                ", mBearing=" + mBearing +
                ", mLocation=" + mLocation +
                ", mLegIndex=" + mLegIndex +
                ", mTotalDistance=" + mTotalDistance +
                '}';
    }

    public void setLegHeading(float legHeading)
    {
        mLegHeading = legHeading;
    }

    public float getLegHeading()
    {
        return mLegHeading;
    }

    public boolean isAutomatedLeg()
    {
        return mIsAutomatedLeg;
    }

    public void setAutomatedLeg(boolean isMark)
    {
        mIsAutomatedLeg = isMark;
    }
}
