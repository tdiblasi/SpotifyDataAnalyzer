package com.example.spotifyanalyzer.history;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.spotifyanalyzer.R;
import com.example.spotifyanalyzer.artist.Artist;
import com.example.spotifyanalyzer.artist.ArtistService;
import com.example.spotifyanalyzer.song.Song;
import com.example.spotifyanalyzer.song.SongService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ViewHistoryActivity extends AppCompatActivity {
    private TextView songsList, artistsList, genresList;
    private ArrayList<Song> songs;
    private ArrayList<Artist> artists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        Intent intent = getIntent();

        songs = (ArrayList<Song>) intent.getSerializableExtra("songService");
        artists = (ArrayList<Artist>) intent.getSerializableExtra("artistService");

        songsList = (TextView) findViewById(R.id.topSongs);
        artistsList = (TextView) findViewById(R.id.topArtists);
        genresList = (TextView) findViewById(R.id.topGenres);
        songsList.setText(getSongs());
        artistsList.setText(getArtists());
        genresList.setText(getGenres());
    }

    private String getSongs() {
        String list = "";
        int listLength = Math.min(songs.size(), 5);
        for(int i = 0; i < listLength; i++) {
            if(i > 0) {
                list = list + "\n";
            }
            list = list + (i+1) + ".) " + songs.get(i).getName();
        }
        return list;
    }

    private String getArtists() {
        String list = "";
        int listLength = Math.min(artists.size(), 5);
        for(int i = 0; i < listLength; i++) {
            if(i > 0) {
                list = list + "\n";
            }
            list = list + (i+1) + ".) " + artists.get(i).getName();
        }
        return list;
    }

    private String getGenres() {
        HashMap<String, Integer> genres = new HashMap<String, Integer>();
        for(Artist artist : artists) {
            for(String genre : artist.getGenres()) {
                if(genres.containsKey(genre)) {
                    genres.put(genre, genres.get(genre) + 1);
                } else {
                    genres.put(genre, 1);
                }
            }
        }

        Map<String, Integer> sortedGenres = sortByValue(genres);

        String list = "";
        int i = 0;
        for(Map.Entry<String, Integer> entry : sortedGenres.entrySet()) {
            if(i == 5) {
                break;
            }
            if(i > 0) {
                list = list + "\n";
            }
            list = list + (i+1) + ".) " + entry.getKey();
            i++;
        }
        return list;
    }

    private static Map<String, Integer> sortByValue(HashMap<String, Integer> map)
    {
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());
        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
        {
            @Override
            public int compare(Map.Entry<String, Integer> stringIntegerEntry, Map.Entry<String, Integer> t1) {
                return stringIntegerEntry.getValue().compareTo(t1.getValue());
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}