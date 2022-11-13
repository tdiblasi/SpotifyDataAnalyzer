//  Some code from https://towardsdatascience.com/using-the-spotify-api-with-your-android-application-the-essentials-1a3c1bc36b9e
package com.example.spotifyanalyzer.song;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spotifyanalyzer.UserListenData;
import com.example.spotifyanalyzer.VolleyCallBack;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SongService implements Serializable {
    private ArrayList<Song> songs = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    //private DatabaseReference databaseReference;

//    public Task<Void> addFavoriteSongs(UserListenData usl){
//        return this.databaseReference.push().setValue(usl);
//    }
//
//    public Task<Void> update(String key, HashMap<String, Object> hashMap){
//        return this.databaseReference.child(key).updateChildren(hashMap);
//    }

    public SongService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        //this.databaseReference = db.getReference(UserListenData.class.getSimpleName());

    }

    public ArrayList<Song> getSongs() {
        return songs;
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

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                Log.d("TOKEN", token);
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return songs;
    }

    public ArrayList<Song> getFavoriteTracks(final VolleyCallBack callBack, String timespan, int songCount) {
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

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                Log.d("TOKEN", token);
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }

        };
        queue.add(jsonObjectRequest);
        return songs;
    }

    public ArrayList<Song> getRecommendations(final VolleyCallBack callBack, String[] seedSongs) {
        String endpoint = "https://api.spotify.com/v1/recommendations";
        endpoint = endpoint + "?seed_tracks=";
        boolean first = true;
        for(String song : seedSongs) {
            if(!first) {
                endpoint = endpoint + ",";
            } else {
                first = false;
            }
            endpoint = endpoint + song;
        }
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

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                Log.d("TOKEN", token);
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
        JsonObjectRequest jsonObjectRequest = prepareSongLibraryRequest(payload);
        queue.add(jsonObjectRequest);
    }

    private JsonObjectRequest prepareSongLibraryRequest(JSONObject payload) {
        return new JsonObjectRequest(Request.Method.PUT, "https://api.spotify.com/v1/me/tracks", payload, response -> {
        }, error -> {
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

}
