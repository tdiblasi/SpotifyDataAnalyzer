package com.example.spotifyanalyzer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.spotifyanalyzer.song.Song;
import com.example.spotifyanalyzer.song.SongService;

import org.w3c.dom.Text;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongDisplay#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongDisplay extends Fragment {
    private static final String GET_TRACK_PREFIX = "https://open.spotify.com/track/";
    private static final String TAG = "Fragments: SongDisplay";
//    private int duration;
//    private String songName, artist, albumName, albumCoverUrl, id;
    private TextView durationColumn, artistColumn, albumColumn, titleView;
    private ImageView albumCover;
    private Song currentSong  = null;
    private SongService songService;
    private Button addButton, listenButton;

    public SongDisplay() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SongDisplay.
     */
    // TODO: Rename and change types and number of parameters
    public static SongDisplay newInstance(String param1, String param2) {
        SongDisplay fragment = new SongDisplay();
        Bundle args = new Bundle();
        //args.putInt("duration", duration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            duration = getArguments().getInt("duration");
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        if (getArguments() != null) {
            currentSong = (Song) getArguments().getSerializable("songObj");
        }
        songService = new SongService(getActivity().getApplicationContext());
        return inflater.inflate(R.layout.fragment_song_display, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated() called");
        super.onViewCreated(view, savedInstanceState);

        durationColumn = (TextView) view.findViewById(R.id.duration);
        artistColumn = (TextView) view.findViewById(R.id.artist);
        albumColumn = (TextView) view.findViewById(R.id.albumName);
        albumCover = (ImageView) view.findViewById(R.id.albumCover);
        addButton = (Button) view.findViewById(R.id.recomAddBtn);
        listenButton = (Button) view.findViewById(R.id.ListenBtn);
        titleView = (TextView) view.findViewById(R.id.recomSongTitle);


        changeSong(currentSong);

        listenButton.setOnClickListener(songListener);
        addButton.setOnClickListener(addListener);
    }

    public View.OnClickListener songListener = v -> {
        openLink(GET_TRACK_PREFIX + currentSong.getId());
    };

    public void openLink(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private View.OnClickListener addListener = v -> {
        songService.addSongToLibrary(this.currentSong);
    };

    public void changeSong(Song song) {
        currentSong = song;
        Glide.with(this)
                .load(currentSong.getAlbumCover())
                .override(840, 840)
                .into(albumCover);
        titleView.setText(currentSong.getName());
        int duration = currentSong.getDuration()/1000;
        durationColumn.setText((duration/60) + ":" + String.format("%02d",duration % 60));
        artistColumn.setText(currentSong.getArtist());
        albumColumn.setText(currentSong.getAlbumName());
    }
}