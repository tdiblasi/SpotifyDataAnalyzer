package com.example.spotifyanalyzer.artist;

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
import com.example.spotifyanalyzer.song.Song;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ArtistService {
    private ArrayList<Artist> artists = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    private DatabaseReference databaseReference;

    public ArtistService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);

    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public ArrayList<Artist> getFavoriteArtists(final VolleyCallBack callBack, String timespan, int songCount) {
        String endpoint = "https://api.spotify.com/v1/me/top/artists";
        endpoint = endpoint + "?limit=" + songCount + "&time_range=" + timespan;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    JSONArray jsonArray = response.optJSONArray("items");
                    for (int n = 0; n < jsonArray.length(); n++) {
                        try {
                            JSONObject object = jsonArray.getJSONObject(n);
                            //object = object.optJSONObject("track");
                            Artist artist = gson.fromJson(object.toString(), Artist.class);
                            artist.setArtist(object.getJSONArray("artists").getJSONObject(0).getString("name"));
                            artist.setAlbumName(object.getJSONObject("album").getString("name"));
                            boolean exists = false;
                            for(Artist s: artists) {
                                if (s.getId().equals(artist.getId())) {
                                    exists = true;
                                    break;
                                }
                            }
                            if(!exists) {
                                artists.add(artist);
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


//            @Override
//            protected Map<String, String> getParams()
//            {
//                Map<String, String>  params = new HashMap<String, String>();
//                params.put("limit", "" + 1);
//                params.put("time_range", timespan);
//
//                return params;
//            }

        };
        queue.add(jsonObjectRequest);
        return artists;
    }

}

