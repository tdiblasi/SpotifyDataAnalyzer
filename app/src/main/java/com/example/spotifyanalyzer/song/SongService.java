//  Some code from https://towardsdatascience.com/using-the-spotify-api-with-your-android-application-the-essentials-1a3c1bc36b9e
package com.example.spotifyanalyzer.song;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spotifyanalyzer.UserListenData;
import com.example.spotifyanalyzer.VolleyCallBack;
import com.example.spotifyanalyzer.artist.Artist;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SongService implements Serializable {
    private ArrayList<Song> songs = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private String favoriteSongIds;
    private Context context;
    private static final String TAG = "SongService";

    //private DatabaseReference databaseReference;

//    public Task<Void> addFavoriteSongs(UserListenData usl){
//        return this.databaseReference.push().setValue(usl);
//    }
//
//    public Task<Void> update(String key, HashMap<String, Object> hashMap){
//        return this.databaseReference.child(key).updateChildren(hashMap);
//    }

    public SongService(Context c) {
        context = c;
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        //this.databaseReference = db.getReference(UserListenData.class.getSimpleName());

    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public String getFavoriteSongIds() {
        return favoriteSongIds;
    }

    public ArrayList<Song> getRecentlyPlayedTracks(final VolleyCallBack callBack) {
        String endpoint = "https://api.spotify.com/v1/me/player/recently-played";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    JSONArray jsonArray = response.optJSONArray("items");
                    for (int n = 0; n < jsonArray.length(); n++) {
                        try {
                            JSONObject object = jsonArray.getJSONObject(n);
                            object = object.optJSONObject("track");
                            Song song = gson.fromJson(object.toString(), Song.class);
                            song.setArtist(object.getJSONArray("artists").getJSONObject(0).getString("name"));
                            song.setAlbumCoverUrl(object.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url"));
                            song.setAlbumName(object.getJSONObject("album").getString("name"));
                            boolean exists = false;
                            for(Song s: songs) {
                                if (s.getId().equals(song.getId())) {
                                    exists = true;
                                    break;
                                }
                            }
                            if(!exists) {
                                songs.add(song);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callBack.onSuccess();
                }, error -> {
                    Log.v("Song ERROR", "Error retrieving song list");
                    Toast errorToast = Toast.makeText(context,
                            "Failed to retrieve recently played songs",
                            Toast.LENGTH_SHORT);
                    errorToast.show();

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return songs;
    }

    public ArrayList<Song> findFavoriteTracks(final VolleyCallBack callBack, String timespan, int songCount) {
        String endpoint = "https://api.spotify.com/v1/me/top/tracks";
        endpoint = endpoint + "?limit=" + songCount + "&time_range=" + timespan;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    JSONArray jsonArray = response.optJSONArray("items");
                    for (int n = 0; n < jsonArray.length(); n++) {
                        try {
                            JSONObject object = jsonArray.getJSONObject(n);
                            //object = object.optJSONObject("track");
                            Song song = gson.fromJson(object.toString(), Song.class);
                            song.setArtist(object.getJSONArray("artists").getJSONObject(0).getString("name"));
                            song.setAlbumName(object.getJSONObject("album").getString("name"));
                            song.setAlbumCoverUrl(object.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url"));
                            boolean exists = false;
                            for(Song s: songs) {
                                if (s.getId().equals(song.getId())) {
                                    exists = true;
                                    break;
                                }
                            }
                            if(!exists) {
                                songs.add(song);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callBack.onSuccess();
                }, error -> {
                    Log.v("Song ERROR", "Error retrieving song list");
                    Toast errorToast = Toast.makeText(context,
                            "Failed to retrieve favorite songs",
                            Toast.LENGTH_SHORT);
                    errorToast.show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }

        };
        queue.add(jsonObjectRequest);
        return songs;
    }

    public ArrayList<Song> getRecommendations(final VolleyCallBack callBack, String seedSongs) {
        String endpoint = "https://api.spotify.com/v1/recommendations";
        endpoint = endpoint + "?seed_tracks=" + seedSongs;
//        boolean first = true;
//        for(String song : seedSongs) {
//            if(!first) {
//                endpoint = endpoint + ",";
//            } else {
//                first = false;
//            }
//            endpoint = endpoint + song;
//        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    JSONArray jsonArray = response.optJSONArray("tracks");
                    for (int n = 0; n < jsonArray.length(); n++) {
                        try {
                            JSONObject object = jsonArray.getJSONObject(n);
                            Song song = gson.fromJson(object.toString(), Song.class);
                            song.setArtist(object.getJSONArray("artists").getJSONObject(0).getString("name"));
                            song.setAlbumName(object.getJSONObject("album").getString("name"));
                            song.setAlbumCoverUrl(object.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url"));

                            boolean exists = false;
                            for(Song s: songs) {
                                if (s.getId().equals(song.getId())) {
                                    exists = true;
                                    break;
                                }
                            }
                            if(!exists) {
                                songs.add(song);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callBack.onSuccess();
                }, error -> {
                    Log.v("Song ERROR", "Error retrieving song list");
                    Toast errorToast = Toast.makeText(context,
                            "Failed to retrieve recommendations",
                            Toast.LENGTH_SHORT);
                    errorToast.show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return songs;
    }


    public void addSongToLibrary(Song song) {
        JSONObject payload = preparePutPayload(song);
        JsonObjectRequest jsonObjectRequest = prepareSongLibraryRequest(payload, song.getId());
        queue.add(jsonObjectRequest);
    }

    private JsonObjectRequest prepareSongLibraryRequest(JSONObject payload, String songId) {
        return new JsonObjectRequest(Request.Method.PUT, "https://api.spotify.com/v1/me/tracks?ids=" + songId, payload, response -> {
            Log.e("LIBRARY", "SUCCESS");
        }, error -> {
            Log.e("LIBRARY", "FAIL");
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
    }

    private JSONObject preparePutPayload(Song song) {
        JSONArray idarray = new JSONArray();
        idarray.put(song.getId());
        JSONObject ids = new JSONObject();
        try {
            ids.put("ids", idarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public Task<Void> uploadFavoriteSongs(ArrayList<Song> songs) {
        List<String> songIds = new ArrayList<String>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference user = db.collection("Users").document(sharedPreferences.getString("userid",""));
        user.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            user.update("songs", null)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            ArrayList<String> existingIds = (ArrayList<String>) (task.getResult().get("songs"));
                                            for(Song s : songs) {
                                                user.update("songs", FieldValue.arrayUnion(s.getId()));

                                            }
                                        }
                                    });
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            Toast errorToast = Toast.makeText(context,
                                    "Error uploading favorite songs to database",
                                    Toast.LENGTH_SHORT);
                            errorToast.show();

                        }
                    }
                });
        return null;
    }



    public Task<DocumentSnapshot> get5FavoriteSongs() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference user = db.collection("Users").document(sharedPreferences.getString("userid",""));
        return user.get();
    }



}
