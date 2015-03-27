package com.itzik.samplewear.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.wearable.view.WatchViewStub;

import com.itzik.samplewear.R;
import com.itzik.samplewear.adapters.MainPagerAdapter;
import com.itzik.samplewear.utils.GoogleApiWrapper;


public class WearMainActivity extends FragmentActivity
{
    private static final String LOG_TAG = WearMainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_main);
        GoogleApiWrapper.initInstance(this);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener()
        {
            @Override
            public void onLayoutInflated(WatchViewStub stub)
            {
                ViewPager pager = (ViewPager) stub.findViewById(R.id.app_pager);
                MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
                pager.setAdapter(adapter);
            }
        });

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        GoogleApiWrapper.getInstance().onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        GoogleApiWrapper.getInstance().onResume();
    }
}
