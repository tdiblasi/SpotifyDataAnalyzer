//  Some code from https://towardsdatascience.com/using-the-spotify-api-with-your-android-application-the-essentials-1a3c1bc36b9e
package com.example.spotifyanalyzer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.spotifyanalyzer.history.HistoryActivity;
import com.example.spotifyanalyzer.song.Song;
import com.example.spotifyanalyzer.song.SongService;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "Activity: MainActivity";
    private TextView songView;
    private Button addBtn;
    private Button historyBtn;
    private Song song;
    private FrameLayout songFragmentDisplay;

    private SongService songService;
    private ArrayList<Song> recentlyPlayedTracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        final EditText edit_name = findViewById(R.id.edit_name);
//        final EditText edit_password = findViewById(R.id.edit_password);
//        Button btn = findViewById(R.id.add_button);

        Button newBtn = findViewById(R.id.newList);
        Button loadBtn = findViewById(R.id.loadLists);



        newBtn.setOnClickListener(v ->{
            Intent newListData = new Intent(this, UserListenDataActivity.class);
            startActivity(newListData);
        });
//        newBtn.setOnClickListener(v ->{
//            UserCrud user = new UserCrud(edit_name.getText().toString(), edit_password.getText().toString());
//            dao.add(user);
//        });

        Log.d(TAG, "onCreate() called");

        songService = new SongService(getApplicationContext());
        songView = (TextView) findViewById(R.id.song);
        addBtn = (Button) findViewById(R.id.add);
        historyBtn = (Button) findViewById(R.id.history);
        songFragmentDisplay = (FrameLayout) findViewById(R.id.songFragmentFrame);

        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(songFragmentDisplay.getId(), new SongDisplay());
        ft.commit();

        getTracks();

        addBtn.setOnClickListener(addListener);
        historyBtn.setOnClickListener(historyListener);
    }

    private View.OnClickListener addListener = v -> {
        songService.addSongToLibrary(this.song);
        if (recentlyPlayedTracks.size() > 0) {
            recentlyPlayedTracks.remove(0);
        }
        try {
            updateSong();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    private View.OnClickListener historyListener = v -> {
        Intent newHistory = new Intent(this, HistoryActivity.class);
        startActivity(newHistory);
    };


    private void getTracks() {
        songService.getRecentlyPlayedTracks(() -> {
            recentlyPlayedTracks = songService.getSongs();
            try {
                updateSong();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    private void updateSong() throws IOException {
        if (recentlyPlayedTracks.size() > 0) {
            songView.setText(recentlyPlayedTracks.get(0).getName());
            song = recentlyPlayedTracks.get(0);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            SongDisplay songDisplay = new SongDisplay();
            Bundle songData = new Bundle();
            songData.putInt("duration", song.getDuration());
            songData.putString("artist", song.getArtist());
            songData.putString("albumName", song.getAlbumName());
            songDisplay.setArguments(songData);
            ft.replace(songFragmentDisplay.getId(), songDisplay);
            ft.commit();

        }

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