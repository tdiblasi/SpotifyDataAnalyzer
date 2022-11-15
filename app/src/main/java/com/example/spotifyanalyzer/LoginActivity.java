//  Code from https://towardsdatascience.com/using-the-spotify-api-with-your-android-application-the-essentials-1a3c1bc36b9e
package com.example.spotifyanalyzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;

import com.android.volley.*;
import com.android.volley.toolbox.Volley;
import com.example.spotifyanalyzer.spotifyuser.SpotifyUser;
import com.example.spotifyanalyzer.spotifyuser.UserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.spotify.sdk.android.auth.*;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Activity: LoginActivity";
    private static final String CLIENT_ID = "8ab9226475db4ffca0159c755076590f";
    private static final String REDIRECT_URI = "com.example.spotifytestv4://callback";
    private static final int REQUEST_CODE = 1337;
    private static final String SCOPES = "user-read-recently-played,user-library-read,user-library-modify,user-read-email,user-read-private,user-top-read";

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;

    private Button loginBtn;

    private RequestQueue queue;

    private void waitForUserInfo() {
        UserService userService = new UserService(queue, msharedPreferences);
        userService.get(() -> {
            SpotifyUser user = userService.getUser();
            editor = getSharedPreferences("SPOTIFY", 0).edit();
            editor.putString("userid", user.id);
            Log.d("STARTING", "GOT USER INFORMATION");
            // We use commit instead of apply because we need the information stored immediately
            editor.commit();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create a new user with a first and last name
            Map<String, Object> thisUser = new HashMap<>();
            thisUser.put("id", user.id);
            thisUser.put("name", user.display_name);
            thisUser.put("email", user.email);
            thisUser.put("songs", null);
            thisUser.put("artists", null);
            thisUser.put("genres", null);
            thisUser.put("recommendations", null);


            db.collection("Users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                boolean found = false;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getData().get("id").equals(user.id)) {
                                        found = true;
                                        break;
                                    }
                                }
                                if(found == false) {
                                    uploadUser(user, thisUser, db);
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });


            startMainActivity();
        });
    }

    private void startMainActivity() {
        Intent newintent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(newintent);
    }

    private void uploadUser(SpotifyUser user, Map<String, Object> userData, FirebaseFirestore db) {
                    db.collection("Users").document(user.id)
                    .set(userData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
    }

    private void authenticateSpotify() {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthorizationRequest request = builder.build();
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    Log.d("STARTING", "GOT AUTH TOKEN");
                    editor.apply();
                    waitForUserInfo();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.login_activity);

        loginBtn = (Button) findViewById(R.id.Login);
        loginBtn.setOnClickListener(v ->{
            authenticateSpotify();
            msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
            queue = Volley.newRequestQueue(this);
        });



    }

    @Override
    public void onResume() {
        super.onResume();
        if(msharedPreferences != null && msharedPreferences.contains("token")) {
            Log.d(TAG, msharedPreferences.getString("token", "null"));
        }
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


