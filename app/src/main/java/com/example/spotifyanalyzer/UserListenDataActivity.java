//  Some code from https://towardsdatascience.com/using-the-spotify-api-with-your-android-application-the-essentials-1a3c1bc36b9e
package com.example.spotifyanalyzer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.util.ArrayList;

public class UserListenDataActivity extends AppCompatActivity {


    private static final String TAG = "Activity: MainActivity";
    private TextView songView;
    private Button addBtn;
    private Song song;
    private FrameLayout songFragmentDisplay;

    private SongService songService;
    private ArrayList<Song> recentlyPlayedTracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_frag_song_display);
//        final EditText edit_name = findViewById(R.id.edit_name);
//        final EditText edit_password = findViewById(R.id.edit_password);
//        Button btn = findViewById(R.id.add_button);

        TextView song1 = findViewById(R.id.song1);
        TextView song2 = findViewById(R.id.song2);
        TextView song3 = findViewById(R.id.song3);
        TextView song4 = findViewById(R.id.song4);
        TextView song5 = findViewById(R.id.song5);

        EditText name = findViewById(R.id.listName);

        Button saveBtn = findViewById(R.id.saveSongs);

        DAOUserListenData dao = new DAOUserListenData();

        saveBtn.setOnClickListener(v ->{
            UserListenData songList = new UserListenData(name.getText().toString());
            dao.add(songList);
        });

//        Log.d(TAG, "onCreate() called");
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        songService = new SongService(getApplicationContext());
//        songView = (TextView) findViewById(R.id.song);
//        addBtn = (Button) findViewById(R.id.add);
//        songFragmentDisplay = (FrameLayout) findViewById(R.id.songFragmentFrame);
//
//        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
//
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(songFragmentDisplay.getId(), new SongDisplay());
//        ft.commit();
//
//        getTracks();
//
//        addBtn.setOnClickListener(addListener);
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