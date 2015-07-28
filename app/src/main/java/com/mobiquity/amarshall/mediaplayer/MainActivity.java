package com.mobiquity.amarshall.mediaplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.mobiquity.amarshall.mediaplayer.objects.Song;


public class MainActivity extends Activity implements ServiceConnection {

    private MusicService mMusicService;
    private boolean mBound = false;

    //Note: Use Broadcast receivers for the service to talk to the UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_play).setOnClickListener(playButtonListener);
        findViewById(R.id.button_Stop).setOnClickListener(stopButtonListener);
        findViewById(R.id.button_pause).setOnClickListener(pauseButtonListener);
        findViewById(R.id.button_next).setOnClickListener(nextButtonListener);
        findViewById(R.id.button_prev).setOnClickListener(prevButtonListener);
    }



    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i("tag", "onServiceConnected");
        MusicService.MusicServiceBinder binder = (MusicService.MusicServiceBinder) service;
        mMusicService = binder.getService();
        mBound = true;

        mMusicService.play();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i("tag", "onServiceDisconnected");
        mBound = false;

    }

    // Button Listener
    private View.OnClickListener pauseButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("tag", "Pause button pushed.");

            if (mBound) {
                mMusicService.pause();
            }

        }
    };

    private View.OnClickListener playButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("tag", "Play button pushed.");

            //Note: Start service then Bind so music will resume_song after UI is closed
            if (!mBound) {

                Intent startIntent = new Intent(v.getContext(), MusicService.class);
                startService(startIntent);
                bindService(startIntent, (ServiceConnection) v.getContext(), Context.BIND_AUTO_CREATE);

            } else {
                mMusicService.resume_song();
            }
        }
    };

    private View.OnClickListener stopButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("tag", "Stop button pushed.");

            Intent stopIntent = new Intent(v.getContext(), MusicService.class);
            stopService(stopIntent);

            if (mBound) {
                unbindService((ServiceConnection) v.getContext());
                mMusicService = null;
                mBound = false;
            }
        }
    };

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("tag", "Next button pushed.");

            if (mBound) {
//                mMusicService.pause();
            }

        }
    };

    private View.OnClickListener prevButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("tag", "Previous button pushed.");

            if (mBound) {
//                mMusicService.pause();
            }

        }
    };
}
