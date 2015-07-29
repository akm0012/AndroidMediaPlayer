package com.mobiquity.amarshall.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.mobiquity.amarshall.mediaplayer.objects.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by amarshall on 7/28/15.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    MusicServiceBinder mBinder;

    // Media Player stuff
    private MediaPlayer mPlayer;
    private boolean mIsPrepared = false;

    private Song mCurrentSong;
    private int mCurrentSongIndex;
    private ArrayList<Song> mSongQueue;

    private boolean mIsShuffled = true;
    private boolean mIsTrackRepeating = false;

    private ArrayList<Integer> mRandomList;

    public class MusicServiceBinder extends Binder {

        MusicService mMusicService;

        public MusicServiceBinder(MusicService _context) {
            mMusicService = _context;
        }

        public MusicService getService() {
            return MusicService.this;
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.i("tag", "MusicService onBind()");

        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.i("tag", "MusicService onCreate()");
        super.onCreate();

        mBinder = new MusicServiceBinder(this);

        if (mPlayer == null) {
            mPlayer = new MediaPlayer(); // Idle
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);

            // Create the Song Queue
            mSongQueue = new ArrayList<>();
            mCurrentSongIndex = 0;
            mCurrentSong = null;

            mRandomList = new ArrayList<>();

            // Remove code eventually, should assume the mSongQueue and mCurrentSongIndex are correct
            Song newSong = new Song(this, R.raw.feet_tall);
            Song newSong2 = new Song(this, R.raw.something_elated);
            Song newSong3 = new Song(this, R.raw.hey_jude);
            add_song_to_queue(newSong);
            add_song_to_queue(newSong2);
            add_song_to_queue(newSong3);
            mCurrentSong = newSong;
            //-----

//            Song cheap = new Song(this, R.raw.something_elated);
//            play(cheap);
//
//            cheap.view_song_in_log();
        }
    }

    // Media Player onPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {

        mIsPrepared = true;
        if (mPlayer != null) {
            mPlayer.seekTo(Integer.parseInt(mCurrentSong.getmDuration()) - 10000);
            mPlayer.start();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("tag", "MusicService onStartCommand()");

//        String audio_resource = "android.resource://" + getPackageName() + "/" + R.raw.feet_tall;
//        Song song = new Song(this, R.raw.feet_tall);
//
//        song.view_song_in_log();
//
//        try {
//            mPlayer.setDataSource(this, Uri.parse(audio_resource)); // Initialized
//        } catch (IOException e) {
//            Log.e("tag", "Error loading audio source");
//            mPlayer.release();
//            mPlayer = null;
//            e.printStackTrace();
//        }
//
//
//        if (mPlayer != null && !mIsPrepared) {
//            mPlayer.prepareAsync(); // Calls onPrepared
//        } else if (mPlayer != null) {
//            mPlayer.start();
//        }

//        return super.onStartCommand(intent, flags, startId);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("tag", "MusicService onDestroy()");

        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("tag", "MusicService onUnbind()");

        return super.onUnbind(intent);
    }

    public void seek_to(int _msec) {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.seekTo(_msec);
        }
    }

    public void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    public void resume_song() {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            if (mIsPrepared) {
                mPlayer.start();
            }
        }
    }

    public void skip_to_next_song() {
        //TODO: If user has repeat song on they may not want to keep repeating when skip is pressed
        onCompletion(mPlayer);
    }

    public void add_song_to_queue(Song _song) {
        mSongQueue.add(_song);
    }

    public void play() {

//        Log.i("tag", "Resource: " + mSongQueue.get(mCurrentSongIndex).getAudioResource());
        Log.i("tag", "Playing song: " + mSongQueue.get(mCurrentSongIndex).getTrackName());

        try {
            mPlayer.setDataSource(this, Uri.parse(mSongQueue.get(mCurrentSongIndex).getAudioResource())); // Initialized
        } catch (IOException e) {
            Log.e("tag", "Error loading audio source");
            mPlayer.release();
            mPlayer = null;
            e.printStackTrace();
        }

        if (mPlayer != null && !mIsPrepared) {
            mPlayer.prepareAsync(); // Calls onPrepared
        } else if (mPlayer != null) {
            mPlayer.seekTo(Integer.parseInt(mCurrentSong.getmDuration()) - 1000);
            mPlayer.start();
        }
    }

    public void reset_random() {
        mRandomList = new ArrayList<>();
    }

    /**
     * @return Boolean indicating if there is a song that has not yet been played.
     */
    private boolean get_shuffled_song() {

        // We have played through all the songs in the Queue
        if (mRandomList.size() == mSongQueue.size()) {

            Log.i("tag", "We have played through all the songs in the queue.");

            mPlayer.stop();
            mPlayer.reset();
            mIsPrepared = false;

            return false;
        }

        int random_index = -1;

        do {

            Random rand = new Random();
            rand.setSeed(System.currentTimeMillis());
            random_index = rand.nextInt(mSongQueue.size());
            Log.d("tag", "In Loop Index: " + random_index);

        } while (mRandomList.contains(random_index));

        mRandomList.add(random_index);

        mCurrentSongIndex = random_index;
        mCurrentSong = mSongQueue.get(mCurrentSongIndex);


        Log.i("tag", "Random song Index picked: " + mCurrentSongIndex);
        Log.i("tag", "Random song picked: " + mCurrentSong.getTrackName());

        return true;

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i("tag", "Song completion");

        if (mIsTrackRepeating) {

            //Note: Left blank. Do not increment the current track index

        } else if (mIsShuffled) {

            if (!get_shuffled_song()) {
                return;
            }


        } else {
            mCurrentSongIndex++;

            if (mCurrentSongIndex < mSongQueue.size()) {
                mCurrentSong = mSongQueue.get(mCurrentSongIndex);
                Log.i("tag", "Current Song Index: " + mCurrentSongIndex);
                mCurrentSong.view_song_in_log();
            } else {

                // Reset the Queue Index
                mCurrentSongIndex = 0;
                mCurrentSong = mSongQueue.get(mCurrentSongIndex);

                mPlayer.stop();
                mPlayer.reset();
                mIsPrepared = false;
//Question: Why is onCompletion being called  after I am done with the play list and then press Play again?
                return; // Do not play the next song
            }
        }

        mPlayer.stop();
        mPlayer.reset();
        mIsPrepared = false;

        play();
    }

}

