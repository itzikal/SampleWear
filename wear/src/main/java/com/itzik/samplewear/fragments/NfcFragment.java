package com.itzik.samplewear.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itzik.samplewear.R;
import com.itzik.samplewear.utils.NfcUtil;


public class NfcFragment extends Fragment
{
    NfcUtil mNfcUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View stub = inflater.inflate(R.layout.fragment_nfc, container, false);
        View mainscren = stub.findViewById(R.id.nfc_main);
        if (mNfcUtil == null)
        {
            mNfcUtil = new NfcUtil();
        }
        return stub;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mNfcUtil.isEnabled()) mNfcUtil.pause(this.getActivity());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mNfcUtil.createNfcUtil(this.getActivity());

    }
}
