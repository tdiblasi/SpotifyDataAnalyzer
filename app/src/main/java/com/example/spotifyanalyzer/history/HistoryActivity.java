package com.example.spotifyanalyzer.history;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.example.spotifyanalyzer.R;
import com.example.spotifyanalyzer.artist.Artist;
import com.example.spotifyanalyzer.artist.ArtistService;
import com.example.spotifyanalyzer.song.Song;
import com.example.spotifyanalyzer.song.SongService;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private Spinner timeSpan;
    private NumberPicker songCount;
    private Button queryButton;

    private SongService songService;
    private ArrayList<Song> favoriteTracks;
    private ArtistService artistService;
    private ArrayList<Artist> favoriteArtists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        songService = new SongService(getApplicationContext());
        artistService = new ArtistService(getApplicationContext());
        timeSpan = (Spinner) findViewById(R.id.timespan);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> timespanAdapter = ArrayAdapter.createFromResource(this,
                R.array.timespans_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        timespanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        timeSpan.setAdapter(timespanAdapter);

        //timeSpan.
        songCount = (NumberPicker)  findViewById(R.id.songCount);
        songCount.setMinValue(1);
        songCount.setMaxValue(50);

        queryButton = (Button) findViewById(R.id.getSongs);
        queryButton.setOnClickListener(historyQueryListener);

    }

    private View.OnClickListener historyQueryListener = v -> {
        getFavorites();
    };

    private void getFavorites() {
        String time_range;
        switch(timeSpan.getSelectedItem().toString()) {
            case "No Limit":
                time_range = "long_term";
                break;

            case "Last 6 Months":
                time_range = "medium_term";
                break;

            default:
                time_range = "short_term";
        }
        artistService.getFavoriteArtists(() -> {
            favoriteArtists = artistService.getArtists();
            for(Artist a : favoriteArtists) {
                Log.d("SONG", a.getId());
            }

        }, time_range, songCount.getValue());
        songService.getFavoriteTracks(() -> {
            favoriteTracks = songService.getSongs();
            for(Song s : favoriteTracks) {
                Log.d("SONG", s.getId());
            }

        }, time_range, songCount.getValue());
    }
}