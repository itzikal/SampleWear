package com.itzik.samplewear.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.itzik.samplewear.fragments.HistoryFragment;
import com.itzik.samplewear.fragments.NfcFragment;
import com.itzik.samplewear.fragments.OnLandMainFragment;

import java.util.ArrayList;

/**
 * Created by Itzik on 3/28/2015.
 */
public class OnLandPagerAdapter extends FragmentPagerAdapter
{

    private ArrayList<Fragment> mHistoryFragments = new ArrayList<>();
    public OnLandPagerAdapter(FragmentManager fm)
    {
        super(fm);
        mHistoryFragments.add(new OnLandMainFragment());
        mHistoryFragments.add(new NfcFragment());
        mHistoryFragments.add(new HistoryFragment());
        mHistoryFragments.add(new HistoryFragment());
        mHistoryFragments.add(new HistoryFragment());
    }

    @Override
    public Fragment getItem(int position)
    {
        return mHistoryFragments.get(position);
    }

    @Override
    public int getCount()
    {
      return mHistoryFragments.size();
    }


}
