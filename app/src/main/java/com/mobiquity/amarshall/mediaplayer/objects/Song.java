package com.mobiquity.amarshall.mediaplayer.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

/**
 * Created by amarshall on 7/28/15.
 */
public class Song {

    private String mTrackName;
    private String mArtist;
    private String mAlbum;
    private String mGenre;
    private String mAudioResource;
    private String mDuration;
    private Bitmap mAlbumArt;

    public Song(Context _context, int _raw_id) {

        mAudioResource = "android.resource://" + "com.mobiquity.amarshall.mediaplayer" + "/" + _raw_id;

        Uri uri = Uri.parse(mAudioResource);
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(_context, uri);

        mTrackName = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        mArtist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        mAlbum = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        mGenre = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
        mDuration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        byte[] art = metadataRetriever.getEmbeddedPicture();

        if (art != null) {
            mAlbumArt = BitmapFactory.decodeByteArray(art, 0, art.length);
        } else {
            //TODO: Get default image with Glide
        }


    }

    public void view_song_in_log() {

        Log.d("song", "Name: " + mTrackName);
        Log.d("song", "Artist: " + mArtist);
        Log.d("song", "Album: " + mAlbum);
        Log.d("song", "Genre: " + mGenre);
        Log.d("song", "Duration: " + mDuration);
        Log.d("song", "Resource: " + mAudioResource);
        Log.d("song", "Album Art: " + mAlbumArt);

    }

    public String getTrackName() {
        return mTrackName;
    }

    public void setTrackName(String mTrackName) {
        this.mTrackName = mTrackName;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public String getGenre() {
        return mGenre;
    }

    public void setGenre(String mGenre) {
        this.mGenre = mGenre;
    }

    public String getAudioResource() {
        return mAudioResource;
    }

    public void setAudioResource(String mAudioResource) {
        this.mAudioResource = mAudioResource;
    }


    public Bitmap getmAlbumArt() {
        return mAlbumArt;
    }

    public String getmDuration() {
        return mDuration;
    }


}
