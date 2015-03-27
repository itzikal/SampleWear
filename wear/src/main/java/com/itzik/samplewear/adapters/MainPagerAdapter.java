package com.itzik.samplewear.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.itzik.samplewear.fragments.ActivateAppOnPhoneFragment;
import com.itzik.samplewear.fragments.DriftAlarmFragment;
import com.itzik.samplewear.fragments.NfcFragment;
import com.itzik.samplewear.fragments.StatisticFragment;


/**
 * Created by Itzik on 3/24/2015.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter
{
    private StatisticFragment mStatisticFragment = new StatisticFragment();
    private NfcFragment mNfcFragment = new NfcFragment();
    private ActivateAppOnPhoneFragment mActivateAppOnPhoneFragment = new ActivateAppOnPhoneFragment();
    private DriftAlarmFragment mDriftAlarmFragment = new DriftAlarmFragment();

    public MainPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return mStatisticFragment;
            case 1:
                return mNfcFragment;
            case 2:
                return mDriftAlarmFragment;
        }
        return mActivateAppOnPhoneFragment;

    }

    @Override
    public int getCount()
    {
        return 4;
    }
}
