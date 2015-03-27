package com.itzik.samplewear.views;

import android.content.Context;

import com.example.itzik.common.LocationDataSample;
import com.itzik.samplewear.R;

import java.text.DecimalFormat;

/**
 * Created by Itzik on 1/5/2015.
 */
public class DataValuePresenterController
{
    private static final String DISTANCE_UNITS = "nm";
    private static final int DISTANCE_PRECISION = 1;
    private static final String VELOCITY_UNITS = "kn";
    private static final int VELOCITY_PRECISION = 1;
    private static final String HEADING_UNITS = "\u00b0";
    private static final int HEADING_PRECISION = 0;
    public static final double METER_TO_NAUTICAL_MILE = 0.539956804 / 1000;

    private final DataValue mAerialDistract;
    private final DataValue mTotalDistance;
    private final DataValue mLegDistance;
    private final DataValue mAverageVelocity;
    private final DataValue mCurrentVelocity;
    private final DataValue mMaxVelocity;
    private final DataValue mLegHeading;
    private final DataValue mCurrentHeading;
    private final DataValue mHeadingToOrigin;

    private Context mContext;
    private DataValuePresenterView mView;
    private ViewDataType mCurrentDataType;
    private DataValue mCurrentDataValue;
    private int mCurrentDataValueIndex;

    private DataValue[] mDistanceDataSet;
    private DataValue[] mHeadingDataSet;
    private DataValue[] mVelocityDataSet;
    private DataValue[] mSelectedDateSet;

    public DataValuePresenterController(Context context, DataValuePresenterView view)
    {
        mContext = context;
        mView = view;
        //        mView.setOnClickListener(new View.OnClickListener()
        //        {
        //            @Override
        //            public void onClick(View v)
        //            {
        //                switchToNextValue();
        //            }
        //        });
        //
        //        setDataSetVisibility(View.VISIBLE);
        //        setTitle(mContext.getString(R.string.sailing));
        mLegDistance = new DataValue(DISTANCE_UNITS, mContext.getString(R.string.leg), DISTANCE_PRECISION);
        mAerialDistract = new DataValue(DISTANCE_UNITS, mContext.getString(R.string.aerial), DISTANCE_PRECISION);
        mTotalDistance = new DataValue(DISTANCE_UNITS, mContext.getString(R.string.total_path), DISTANCE_PRECISION);
        mDistanceDataSet = new DataValue[]{mLegDistance, mTotalDistance, mAerialDistract};

        mAverageVelocity = new DataValue(VELOCITY_UNITS, mContext.getString(R.string.average), VELOCITY_PRECISION);
        mCurrentVelocity = new DataValue(VELOCITY_UNITS, mContext.getString(R.string.current), VELOCITY_PRECISION);
        mMaxVelocity = new DataValue(VELOCITY_UNITS, mContext.getString(R.string.max), VELOCITY_PRECISION);
        mVelocityDataSet = new DataValue[]{mAverageVelocity, mCurrentVelocity, mMaxVelocity};

        mLegHeading = new DataValue(HEADING_UNITS, mContext.getString(R.string.leg), HEADING_PRECISION);
        mCurrentHeading = new DataValue(HEADING_UNITS, mContext.getString(R.string.current), HEADING_PRECISION);
        mHeadingToOrigin = new DataValue(HEADING_UNITS, mContext.getString(R.string.to_origin), HEADING_PRECISION);
        mHeadingDataSet = new DataValue[]{mLegHeading, mCurrentHeading, mHeadingToOrigin};
    }


    public void updateLocationSample(LocationDataSample dataSample)
    {
        mTotalDistance.setValue((float) (dataSample.getTotalDistance() * METER_TO_NAUTICAL_MILE));
        mAerialDistract.setValue((float) (dataSample.getArialDistanceFormStart() * METER_TO_NAUTICAL_MILE));
        mLegDistance.setValue((float) (dataSample.getDistanceFromLeg() * METER_TO_NAUTICAL_MILE));

        mAverageVelocity.setValue(dataSample.getAverageSpeed());
        mCurrentVelocity.setValue(dataSample.getSpeed());
        mMaxVelocity.setValue(dataSample.getMaxSpeed());

        mCurrentHeading.setValue(dataSample.getBearing());
        mHeadingToOrigin.setValue(dataSample.getHeadingFromStart());
        mLegHeading.setValue((float) dataSample.getLegHeading());

        mView.applyDataValues(mCurrentDataValue);
    }

    public void clearCalculations()
    {
        mTotalDistance.reset();
        mAerialDistract.reset();
        mLegDistance.reset();

        mAverageVelocity.reset();
        mCurrentVelocity.reset();
        mMaxVelocity.reset();

        mCurrentHeading.reset();
        mHeadingToOrigin.reset();
        mLegHeading.reset();

        mView.applyDataValues(mCurrentDataValue);
    }

    public void updateLegInformation(LocationDataSample leg)
    {
        mView.applyDataValues(mCurrentDataValue);
    }

    public void setTitle(String title)
    {
        mView.setTitle(title);
    }

    public void setSelectedDataType(ViewDataType type)
    {
        mCurrentDataType = type;
        switch (mCurrentDataType)
        {
            case Heading:
                mSelectedDateSet = mHeadingDataSet;
                break;
            case Speed:
                mSelectedDateSet = mVelocityDataSet;
                break;
            case Distance:
                mSelectedDateSet = mDistanceDataSet;
                break;
            default:
                return;
        }
        setCenter(1);
        mView.setTitle(type.name());
        mView.applyDataSetValues(mSelectedDateSet);
    }

    private void setCenter(int index)
    {
        mCurrentDataValue = mSelectedDateSet[index];
        mCurrentDataValueIndex = index;
        mView.applyDataValues(mCurrentDataValue);
    }

    private void switchToNextValue()
    {
        int nextData = (mCurrentDataValueIndex + 1) % 3;
        setCenter(nextData);
    }

    public void setDataSetVisibility(int visibility)
    {
        mView.setVisibility(visibility);
        mView.setTitleVisibility(visibility);
    }


    public class DataValue
    {
        private final DecimalFormat mDecimalFormat;
        private final String mUnits;
        private final String mAux;
        private float mValue;

        public String getConvertedValue()
        {
            return mDecimalFormat.format(mValue);
        }
        //        public float getValue()
        //        {
        //            return mValue;
        //        }

        public String getUnits()
        {
            return mUnits;
        }

        public String getAux()
        {
            return mAux;
        }


        public DataValue(String mainUnits, String aux, int precision)
        {
            mUnits = mainUnits;
            mAux = aux;
            mDecimalFormat = new DecimalFormat();
            mDecimalFormat.setMaximumFractionDigits(precision);
        }

        public void setValue(float value)
        {
            mValue = value;
        }

        public void reset()
        {
            mValue = 0;
        }
    }
}
