package com.example.spotifyanalyzer;

public class UserCrud {

    private String name;

    private String password;

    public UserCrud(){}
    public UserCrud(String name, String password){
        this.name = name;

        this.password = password;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPassword(){ return this.password; }

    public void setPassword(String password){ this.password = password;}
}
