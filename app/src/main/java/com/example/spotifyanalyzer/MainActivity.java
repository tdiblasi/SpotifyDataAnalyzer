//  Some code from https://towardsdatascience.com/using-the-spotify-api-with-your-android-application-the-essentials-1a3c1bc36b9e
package com.example.spotifyanalyzer;

import androidx.annotation.NonNull;
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

import com.example.spotifyanalyzer.artist.Artist;
import com.example.spotifyanalyzer.artist.ArtistService;
import com.example.spotifyanalyzer.history.HistoryActivity;
import com.example.spotifyanalyzer.history.ViewHistoryActivity;
import com.example.spotifyanalyzer.recommendations.GetRecommendationsActivity;
import com.example.spotifyanalyzer.recommendations.RecommendationsQueueActivity;
import com.example.spotifyanalyzer.settings.SettingsActivity;
import com.example.spotifyanalyzer.song.Song;
import com.example.spotifyanalyzer.song.SongService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "Activity: MainActivity";
    private TextView songView;
    private Button addBtn;
    private Button historyBtn, showHistoryButton, settingsButton;
    private Button recommendationsBtn;
    private Song song;
    private FrameLayout songFragmentDisplay;

    private SongService songService;
    private ArtistService artistService;
    private ArrayList<Song> recentlyPlayedTracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        final EditText edit_name = findViewById(R.id.edit_name);
//        final EditText edit_password = findViewById(R.id.edit_password);
//        Button btn = findViewById(R.id.add_button);

//        Button newBtn = findViewById(R.id.newList);
//        Button loadBtn = findViewById(R.id.loadLists);
        recommendationsBtn = (Button) findViewById(R.id.recommendations);
        settingsButton = (Button) findViewById(R.id.settingsButton);
//
//
//
//        newBtn.setOnClickListener(v ->{
//            Intent newListData = new Intent(this, UserListenDataActivity.class);
//            startActivity(newListData);
//        });
//        newBtn.setOnClickListener(v ->{
//            UserCrud user = new UserCrud(edit_name.getText().toString(), edit_password.getText().toString());
//            dao.add(user);
//        });

        Log.d(TAG, "onCreate() called");

        songService = new SongService(getApplicationContext());
        artistService = new ArtistService(getApplicationContext());
        songView = (TextView) findViewById(R.id.song);
        addBtn = (Button) findViewById(R.id.add);
        historyBtn = (Button) findViewById(R.id.history);
        showHistoryButton = (Button) findViewById(R.id.showHistory);
        songFragmentDisplay = (FrameLayout) findViewById(R.id.songFragmentFrame);

        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);


//        getTracks();

//        addBtn.setOnClickListener(addListener);
        settingsButton.setOnClickListener(settingsListener);
        historyBtn.setOnClickListener(historyListener);
        showHistoryButton.setOnClickListener(showHistoryListener);
        recommendationsBtn.setOnClickListener(recommendationsListener);

//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//
//        // Create a new user with a first and last name
//        Map<String, Object> user = new HashMap<>();
//        user.put("first", "Tom");
//        user.put("last", "D");
//        user.put("born", 2000);
//
//// Add a new document with a generated ID
//        db.collection("Users")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
//
//        db.collection("Users")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
    }

//    private View.OnClickListener addListener = v -> {
//        songService.addSongToLibrary(this.song);
//        if (recentlyPlayedTracks.size() > 0) {
//            recentlyPlayedTracks.remove(0);
//        }
//        try {
//            updateSong();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    };

    private View.OnClickListener settingsListener = v -> {
        Intent newSettings = new Intent(this, SettingsActivity.class);
        startActivity(newSettings);
    };

    private View.OnClickListener historyListener = v -> {
        Intent newHistory = new Intent(this, HistoryActivity.class);
        startActivity(newHistory);
    };

    private View.OnClickListener showHistoryListener = v -> {
        getFavorites();
    };


    private View.OnClickListener recommendationsListener = v -> {
        Intent newRecommendations = new Intent(this, GetRecommendationsActivity.class);
        startActivity(newRecommendations);
    };

    private void getFavorites() {
        Intent newIntent = new Intent(this, ViewHistoryActivity.class);
        String time_range;
        switch("No Limit") {
            case "No Limit":
                time_range = "long_term";
                break;

            case "Last 6 Months":
                time_range = "medium_term";
                break;

            default:
                time_range = "short_term";
        }
        artistService.findFavoriteArtists(() -> {
            ArrayList<Artist> favoriteArtists = artistService.getArtists();
            newIntent.putExtra("artistService",favoriteArtists);
            songService.findFavoriteTracks(() -> {
                ArrayList<Song> favoriteTracks = songService.getSongs();
                newIntent.putExtra("songService",favoriteTracks);
                startActivity(newIntent);

            }, time_range, 50);

        }, time_range, 50);

    }


//    private void getTracks() {
//        songService.getRecentlyPlayedTracks(() -> {
//            recentlyPlayedTracks = songService.getSongs();
//            try {
//                updateSong();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        });
//    }

//    private void updateSong() throws IOException {
//        if (recentlyPlayedTracks.size() > 0) {
//            songView.setText(recentlyPlayedTracks.get(0).getName());
//            song = recentlyPlayedTracks.get(0);
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            SongDisplay songDisplay = new SongDisplay();
//            Bundle songData = new Bundle();
//            songData.putInt("duration", song.getDuration());
//            songData.putString("artist", song.getArtist());
//            songData.putString("albumName", song.getAlbumName());
//            songDisplay.setArguments(songData);
//            ft.replace(songFragmentDisplay.getId(), songDisplay);
//            ft.commit();
//
//        }
//
//    }

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