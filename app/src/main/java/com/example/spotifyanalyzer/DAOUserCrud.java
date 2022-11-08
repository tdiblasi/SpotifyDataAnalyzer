package com.example.spotifyanalyzer;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DAOUserCrud {

    private DatabaseReference databaseReference;

    public DAOUserCrud(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        this.databaseReference = db.getReference(UserCrud.class.getSimpleName());
    }

    public Task<Void> add(UserCrud usc){
        return this.databaseReference.push().setValue(usc);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap){
        return this.databaseReference.child(key).updateChildren(hashMap);
    }
}
