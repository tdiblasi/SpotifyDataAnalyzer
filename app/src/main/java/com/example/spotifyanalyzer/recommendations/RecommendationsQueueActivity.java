package com.example.spotifyanalyzer.recommendations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.spotifyanalyzer.SongDisplay;
import com.example.spotifyanalyzer.song.Song;

import java.lang.reflect.Array;
import java.util.ArrayList;
import com.example.spotifyanalyzer.R;

public class RecommendationsQueueActivity extends AppCompatActivity {
    private static final String TAG = "RecommsQueueActivity";
    private ArrayList<Song> songs;
    private Button nextSong;
    private int pos = 0;

    private FrameLayout songFragmentDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations_queue);
        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        Intent intent = getIntent();

        songs = (ArrayList<Song>) intent.getSerializableExtra("queue");
        Log.d(TAG, "onCreate() called");

        nextSong = (Button) findViewById(R.id.nextSong);
        nextSong.setOnClickListener(nextSongListener);
        this.showNextSong();
    }

    public View.OnClickListener nextSongListener = v -> {
        this.showNextSong();
    };

    public void showNextSong() {
        songFragmentDisplay = (FrameLayout) findViewById(R.id.recommendationSongFragmentFrame);
        if(this.pos < songs.size()) {
            Song song = songs.get(this.pos++);
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
            if(this.pos >= songs.size()) {
                nextSong.setEnabled(false);
            }
        } else {
            Log.e(TAG, "End of queue error");
        }
    };
}