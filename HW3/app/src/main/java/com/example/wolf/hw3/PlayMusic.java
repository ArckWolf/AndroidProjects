package com.example.wolf.hw3;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class PlayMusic extends Service {
    boolean isPlaying = false;
    MediaPlayer player;
    int counter;

    public PlayMusic() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (player == null || !player.isPlaying()){
            player = MediaPlayer.create(this, R.raw.natural);
            player.setLooping(true); // Set looping
            player.setVolume(100,100);
            counter = getCounter();
        }

        if(isPlaying)
        {
            Toast.makeText(this, "PAUSE.", Toast.LENGTH_LONG).show();
            isPlaying = false;
            player.pause();
        }
        else
        {
            Toast.makeText(this, "START.", Toast.LENGTH_LONG).show();
            isPlaying = true;
            player.start();

            counter++;
            shareCounter();
        }


        return super.onStartCommand(intent, flags, startId);
    }

    public void shareCounter() {
        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.MY_PREFERENCE_FILE),
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("counter", counter);

        editor.commit();
    }

    public int getCounter() {
        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.MY_PREFERENCE_FILE),
                Context.MODE_PRIVATE);

        int count = prefs.getInt("counter",0);
        Log.e("counter", "Counter: " + count);

        return count;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
