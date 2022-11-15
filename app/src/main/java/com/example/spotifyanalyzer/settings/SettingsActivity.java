package com.example.spotifyanalyzer.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.example.spotifyanalyzer.LoginActivity;
import com.example.spotifyanalyzer.R;
import com.example.spotifyanalyzer.spotifyuser.UserService;

public class SettingsActivity extends AppCompatActivity {
    //private UserService userService;
    //private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button logoutBtn = (Button) findViewById(R.id.logout);
        logoutBtn.setOnClickListener(logoutListener);

        Button deleteBtn = (Button) findViewById(R.id.deleteUserData);
        deleteBtn.setOnClickListener(deleteListener);
    }

    public View.OnClickListener logoutListener = v -> {
        logOut();
    };

    public View.OnClickListener deleteListener = v -> {
        UserService userService = new UserService(getApplicationContext());
        userService.delete();
        logOut();
    };

    public void logOut() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}