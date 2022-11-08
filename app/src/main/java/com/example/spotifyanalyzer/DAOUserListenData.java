package com.example.spotifyanalyzer;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class DAOUserListenData {

    private DatabaseReference databaseReference;

    public DAOUserListenData(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        this.databaseReference = db.getReference(UserListenData.class.getSimpleName());
    }

    public Task<Void> add(UserListenData usl){
        return this.databaseReference.push().setValue(usl);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap){
        return this.databaseReference.child(key).updateChildren(hashMap);
    }
}
