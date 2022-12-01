package com.example.spotifyanalyzer.artist;

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
import com.example.spotifyanalyzer.song.Song;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ArtistService implements Serializable {
    private static final String TAG = "ArtistService";
    private ArrayList<Artist> artists = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private Context context;

    public ArtistService(Context c) {
        context = c;
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);

    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public ArrayList<Artist> findFavoriteArtists(final VolleyCallBack callBack, String timespan, int songCount) {
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
                            artist.setURLs(object.getJSONObject("external_urls"));
                            artist.setImages(object.getJSONArray("images"));
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
                    Toast errorToast = Toast.makeText(context,
                            "Failed to retrieve favorite artists",
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



    public Task<Void> uploadFavoriteArtists(ArrayList<Artist> artists) {
        List<String> artistIds = new ArrayList<String>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference user = db.collection("Users").document(sharedPreferences.getString("userid",""));
        user.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            user.update("artists", null)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            for(Artist artist : artists) {
                                                user.update("artists", FieldValue.arrayUnion(artist.getId()));

                                            }
                                        }
                                    });
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        return null;
    }

}

