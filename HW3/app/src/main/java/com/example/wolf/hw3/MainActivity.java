package com.example.wolf.hw3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.txtV_counter);
        textView.setText("Count: " + getCounter());
    }

    public void clickMusic(View view) {
        Intent intent = new Intent(this, PlayMusic.class);
        startService(intent);

        if(isPlaying)
        {
            textView.setText("Count: " + getCounter());
            isPlaying = false;
        }
        else
        {
            int count = getCounter()+1;
            textView.setText("Count: " + count);
            isPlaying = true;
        }
    }

    public int getCounter() {
        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.MY_PREFERENCE_FILE),
                Context.MODE_PRIVATE);

        int count = prefs.getInt("counter",0);
        Log.e("counter", "Counter: " + count);

        return count;
    }
}
