package com.itzik.samplewear;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;

/**
 * Created by Itzik on 3/9/2015.
 */
public class TestActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.send_message_to_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendMessageToService();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Intent intent = new Intent(this, WearService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        doUnbindService();
    }

    private Messenger mService;
    private boolean mIsBound = false;


    private void sendMessageToService()
    {
        if(mService != null)
        {
            Message msg = Message.obtain(null, WearService.MSG_MESSAGE_TO_WEAR, "this is the message");
            try
            {
                mService.send(msg);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
    }
    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            mService = new Messenger(service);
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            mService = null;
            mIsBound = false;
        }
    };

    void doUnbindService()
    {
        if (mIsBound)
        {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null)
            {
                try
                {
                    Message msg = Message.obtain(null, WearService.MSG_UNREGISTER_CLIENT);
                    mService.send(msg);
                }
                catch (RemoteException e)
                {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }

            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }
}
