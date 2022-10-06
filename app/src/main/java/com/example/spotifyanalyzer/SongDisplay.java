package com.example.spotifyanalyzer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongDisplay#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongDisplay extends Fragment {
    private static final String TAG = "Fragments: SongDisplay";
    private int duration;
    private String artist, albumName;
    private TextView durationColumn, artistColumn, albumColumn;

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
        if (getArguments() != null) {
            duration = getArguments().getInt("duration");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        if (getArguments() != null) {
            duration = getArguments().getInt("duration")/1000;
            artist = getArguments().getString("artist");
            albumName = getArguments().getString("albumName");
        }
        return inflater.inflate(R.layout.fragment_song_display, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated() called");
        super.onViewCreated(view, savedInstanceState);
        durationColumn = (TextView) view.findViewById(R.id.duration);
        artistColumn = (TextView) view.findViewById(R.id.artist);
        albumColumn = (TextView) view.findViewById(R.id.albumName);
        durationColumn.setText("Duration:\n"+(duration/60) + ":" + (duration % 60));
        artistColumn.setText("Artist:\n"+artist);
        albumColumn.setText("Album Title:\n"+albumName);
    }
}