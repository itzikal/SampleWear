package com.itzik.samplewear.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itzik.samplewear.R;


public class ActivateAppOnPhoneFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View stub = inflater.inflate(R.layout.fragment_activate_app, container, false);
        return stub;
    }
}
