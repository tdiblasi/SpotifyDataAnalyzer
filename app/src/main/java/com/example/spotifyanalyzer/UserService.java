//  Code from https://towardsdatascience.com/using-the-spotify-api-with-your-android-application-the-essentials-1a3c1bc36b9e
package com.example.spotifyanalyzer;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserService {

    private static final String ENDPOINT = "https://api.spotify.com/v1/me";
    private SharedPreferences msharedPreferences;
    private RequestQueue mqueue;
    private SpotifyUser user;
    private DatabaseReference databaseReference;

    public UserService(RequestQueue queue, SharedPreferences sharedPreferences) {
        mqueue = queue;
        msharedPreferences = sharedPreferences;
    }

    public SpotifyUser getUser() {
        return user;
    }

    public void get(final VolleyCallBack callBack) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(ENDPOINT, null, response -> {
            Gson gson = new Gson();
            user = gson.fromJson(response.toString(), SpotifyUser.class);
            this.add(user);
            callBack.onSuccess();
        }, error -> get(() -> {

        })) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = msharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        mqueue.add(jsonObjectRequest);
    }

    public Task<Void> add(SpotifyUser user){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(user.id).setValue(user.display_name);
        return this.databaseReference.push().setValue(user);
    }


}
