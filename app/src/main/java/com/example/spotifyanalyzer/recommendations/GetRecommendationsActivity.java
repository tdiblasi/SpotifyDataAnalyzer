package com.example.spotifyanalyzer.recommendations;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class GetRecommendationsActivity extends AppCompatActivity {
    private final String TAG = "GetRecommsActivity";
    private final String[] SAMPLE_SONGS = {
//            "2QbGvQssb0VLLS4x5NOmyJ",
//            "1q8DwZtQen5fvyB7cKbShC",
//            "2HbKqm4o0w5wEeEFXm2sD4",
//            "564oa00vY05d1uYnTEAAmE",
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
        try {
            getRecommendations();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    private void getRecommendations() throws ExecutionException, InterruptedException {
        Intent newIntent = new Intent(GetRecommendationsActivity.this, RecommendationsQueueActivity.class);

        songService.get5FavoriteSongs()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> existingIds = (ArrayList<String>) (task.getResult().get("songs"));
                            String favoriteSongIds = "";
                            for(int i = 0; i < Math.min(existingIds.size(), 5); i++) {
                                if(i > 0) {
                                    favoriteSongIds = favoriteSongIds + ",";
                                }
                                favoriteSongIds = favoriteSongIds + existingIds.get(i);
                            }
                            songService.getRecommendations(() -> {
                                recommendedTracks = songService.getSongs();
                                newIntent.putExtra("queue",recommendedTracks);
                                startActivity(newIntent);


                            }, favoriteSongIds);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });




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