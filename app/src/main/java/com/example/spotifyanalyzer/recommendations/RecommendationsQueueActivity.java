package com.example.spotifyanalyzer.recommendations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.spotifyanalyzer.SongDisplay;
import com.example.spotifyanalyzer.song.Song;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

import com.example.spotifyanalyzer.R;

public class RecommendationsQueueActivity extends AppCompatActivity {
    private static final String TAG = "RecommsQueueActivity";
    private ArrayList<Song> songs;
    private Button nextSong;
    private int pos = -1;
    private boolean slowedDown = true;

    private FrameLayout songFragmentDisplay;
    private SensorManager sensorMgr;
    private float totalAccel, currentAccel, lastAccel;
    private static final float SHAKE_THRESHOLD = 30;
    private static final float UNSHAKE_THRESHOLD = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations_queue);
        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        Intent intent = getIntent();

        songs = (ArrayList<Song>) intent.getSerializableExtra("queue");
        Log.d(TAG, "onCreate() called");

        sensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(sensorMgr).registerListener(mSensorListener, sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        totalAccel = 10f;
        currentAccel = SensorManager.GRAVITY_EARTH;
        lastAccel = SensorManager.GRAVITY_EARTH;

        nextSong = (Button) findViewById(R.id.nextSong);
        nextSong.setOnClickListener(nextSongListener);
        this.showNextSong();
    }

    public View.OnClickListener nextSongListener = v -> {
        this.showNextSong();
    };

    public void showNextSong() {
        songFragmentDisplay = (FrameLayout) findViewById(R.id.recommendationSongFragmentFrame);
        if(this.pos < songs.size() - 1) {
            Song song = songs.get(++this.pos);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            SongDisplay songDisplay = new SongDisplay();
            Bundle songData = new Bundle();
            songData.putInt("duration", song.getDuration());
            songData.putString("artist", song.getArtist());
            songData.putString("albumName", song.getAlbumName());
            songData.putString("albumCoverUrl", song.getAlbumCover());
            songData.putString("id", song.getId());
            songData.putSerializable("songObj", song);

            songDisplay.setArguments(songData);
            ft.replace(songFragmentDisplay.getId(), songDisplay);
            ft.commit();
            if(this.pos >= songs.size() - 1) {
                nextSong.setText("End of Queue");
                nextSong.setEnabled(false);
            }
        } else {
            Log.e(TAG, "End of queue error");
        }
    };

    public void showLastSong() throws InterruptedException {
        songFragmentDisplay = (FrameLayout) findViewById(R.id.recommendationSongFragmentFrame);
        if(this.pos > 0) {
            Song song = songs.get(--this.pos);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            SongDisplay songDisplay = new SongDisplay();
            Bundle songData = new Bundle();
            songData.putInt("duration", song.getDuration());
            songData.putString("artist", song.getArtist());
            songData.putString("albumName", song.getAlbumName());
            songData.putString("albumCoverUrl", song.getAlbumCover());
            songData.putString("id", song.getId());
            songData.putSerializable("songObj", song);

            songDisplay.setArguments(songData);
            ft.replace(songFragmentDisplay.getId(), songDisplay);
            ft.commit();
            //Log.d("SHAKE", "Undo");

        } else {
            //Log.d("SHAKE", "Beginning of queue");
        }
    };

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            lastAccel = currentAccel;
            currentAccel = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = currentAccel - lastAccel;
            totalAccel = totalAccel * 0.9f + delta;
            if (totalAccel > SHAKE_THRESHOLD && slowedDown) {
                try {
                    showLastSong();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                slowedDown = false;
            } else if (totalAccel < UNSHAKE_THRESHOLD && !slowedDown) {
                slowedDown = true;
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }


    };

    @Override
    protected void onResume() {
        sensorMgr.registerListener(mSensorListener, sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }
    @Override
    protected void onPause() {
        sensorMgr.unregisterListener(mSensorListener);
        super.onPause();
    }
}