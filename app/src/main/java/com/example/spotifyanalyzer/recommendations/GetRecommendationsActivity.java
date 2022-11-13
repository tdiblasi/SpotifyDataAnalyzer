package com.example.spotifyanalyzer.recommendations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.spotifyanalyzer.R;
import com.example.spotifyanalyzer.artist.Artist;
import com.example.spotifyanalyzer.history.HistoryActivity;
import com.example.spotifyanalyzer.history.ViewHistoryActivity;
import com.example.spotifyanalyzer.song.Song;
import com.example.spotifyanalyzer.song.SongService;

import java.util.ArrayList;

public class GetRecommendationsActivity extends AppCompatActivity {
    private final String TAG = "GetRecommsActivity";
    private final String[] SAMPLE_SONGS = {
            "2QbGvQssb0VLLS4x5NOmyJ",
            "1q8DwZtQen5fvyB7cKbShC",
            "2HbKqm4o0w5wEeEFXm2sD4",
            "564oa00vY05d1uYnTEAAmE",
            "1GmIFHYT3N2V3G0MoYhEil"
    };
    private Button queryButton;
    private SongService songService;
    private ArrayList<Song> recommendedTracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_recommendations);

        songService = new SongService(getApplicationContext());
        queryButton = (Button) findViewById(R.id.recommendationsQuery);
        queryButton.setOnClickListener(recommendationsQueryListener);
    }

    private View.OnClickListener recommendationsQueryListener = v -> {
        getRecommendations();
    };

    private void getRecommendations() {
        Intent newIntent = new Intent(GetRecommendationsActivity.this, RecommendationsQueueActivity.class);
        songService.getRecommendations(() -> {
            recommendedTracks = songService.getSongs();
            for(Song s : recommendedTracks) {
                Log.d("RECOMMEND", s.getName());
            }
            newIntent.putExtra("queue",recommendedTracks);
            startActivity(newIntent);


        }, SAMPLE_SONGS);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}