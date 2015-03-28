package com.itzik.samplewear.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itzik.samplewear.R;

/**
 * Created by Itzik on 3/28/2015.
 */
public class OnLandMainFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View stub = inflater.inflate(R.layout.fragment_on_land_main, container, false);
        return stub;
    }
}
