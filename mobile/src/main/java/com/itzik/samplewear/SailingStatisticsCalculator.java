package com.itzik.samplewear;

import android.util.Log;

import com.example.itzik.common.LocationDataSample;

import java.util.ArrayList;

/**
 * Created by Itzik on 1/1/2015.
 */
public class SailingStatisticsCalculator
{
    private static final String LOG_TAG = SailingStatisticsCalculator.class.getSimpleName();
    private LocationDataSample mLastPoint;
    private LocationDataSample mStartingPoint;
    private ArrayList<LocationDataSample> mCurrentLeg;
    private int mCount;
    private double mTotalLength =0;



    public void startNewSailing()
    {
        mStartingPoint = null;
        mLastPoint = null;
        mTotalLength = 0;
        mCurrentLeg = null;
    }

    public void addLocations(ArrayList<LocationDataSample> pointList)
    {
        for (LocationDataSample locationDataSample : pointList)
        {
            addLocation(locationDataSample);
        }
    }

    public void addLocation(LocationDataSample point)
    {
        if(mStartingPoint == null)
        {
            mStartingPoint = point;

        }

        if(mCurrentLeg == null || mLastPoint==null || mLastPoint.getLegIndex()!=point.getLegIndex()){
            mCurrentLeg = new ArrayList<>();
        }
        mCurrentLeg.add(point);
        addStatisticToPoint(point);
    }

    public void legIndexFixed(LocationDataSample locationDataSample){

        if(mCurrentLeg.get(0).getLegIndex() == locationDataSample.getLegIndex()){
            Log.d(LOG_TAG, "legIndexFixed() ignoring since we are updated with leg index "+locationDataSample.getLegIndex());
            return;// nothing to update
        }

        Log.d(LOG_TAG, "legIndexFixed() updating leg data with leg "+locationDataSample.getLegIndex());
        // we will assume that the following indices will be from the same leg
        // find the current location sample from this collection
        int i = 0;
        for (; i < mCurrentLeg.size(); i++) {
            LocationDataSample sample = mCurrentLeg.get(i);
            if(sample.getId() == locationDataSample.getId()){
                break;
            }
        }

        ArrayList<LocationDataSample> legs = new ArrayList<>();
        for (;i<mCurrentLeg.size();i++){
            LocationDataSample point = mCurrentLeg.get(i);
            legs.add(point);
            calculateLegStatisticsOnThepoint(legs, point);
        }
        if(legs.size()==0){
            Log.d(LOG_TAG, "legIndexFixed() but the point is not part of the current leg");
            return;
        }
        mCurrentLeg = legs;

    }

    private void calculateLegStatisticsOnThepoint(ArrayList<LocationDataSample> legs, LocationDataSample point) {
//        point.setLegHeading(LegsCalculation.calculateHeading(legs));
        point.setDistanceFromLeg(point.distanceTo(legs.get(0)));// at least our point is part of the leg
    }

    private void addStatisticToPoint(LocationDataSample point)
    {
        if(mLastPoint == null)
        {
            mCount = 0;
            mLastPoint = point;
        }

        point.setAverageSpeed(((mLastPoint.getAverageSpeed() * mCount) + point.getSpeed()) / (mCount + 1));
        point.setMaxSpeed(mLastPoint.getMaxSpeed() > point.getSpeed() ? mLastPoint.getMaxSpeed() : point.getSpeed());

        double distanceFromPreviousLocation = point.distanceTo(mLastPoint);
        point.setDistanceFromPreviousLocation(distanceFromPreviousLocation);
        mTotalLength += distanceFromPreviousLocation;
        point.setTotalDistance(mTotalLength);
        point.setArialDistanceFormStart(point.distanceTo(mStartingPoint));

        /*
        * Apply the future formula on either of the vectors:
1. For leg: combined (as per leg calculator) vector of current leg
2. For "to origin": substruction of current from origin (origin - current)

Given that the final vector you receive for either 1 or 2 is (X,Y). The AZ (azimuth angle) calculation is:

AZ = MOD(ATAN2(X,Y)*180/PI()+360,360)
        * */

        calculateLegStatisticsOnThepoint(mCurrentLeg, point);
//        point.setHeadingFromStart(LegsCalculation.calculateHeading(mStartingPoint, point));
//
//        if (point.getBearing() == -1)
//        {
//            point.setBearing(LegsCalculation.calculateHeading(mLastPoint, point));
//        }

        Log.d(LOG_TAG, "addStatisticToPoint(), after calculation: " + point.toString());
        mLastPoint = point;
        mCount++;
    }

}
