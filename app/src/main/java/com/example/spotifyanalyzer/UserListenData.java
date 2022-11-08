package com.example.spotifyanalyzer;

public class UserListenData {

    private String name;

    private double song1;
    private double song2;
    private double song3;
    private double song4;
    private double song5;



    public UserListenData(){}
    public UserListenData(String name){
        this.name = name;

        this.song1 = Math.random() * 1000000;
        this.song2 = Math.random() * 1000000;
        this.song3 = Math.random() * 1000000;
        this.song4 = Math.random() * 1000000;
        this.song5 = Math.random() * 1000000;

    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public double getSong1(){
        return this.song1;
    }
    public double getSong2(){
        return this.song2;
    }
    public double getSong3(){
        return this.song3;
    }
    public double getSong4(){
        return this.song4;
    }
    public double getSong5(){
        return this.song5;
    }
}
