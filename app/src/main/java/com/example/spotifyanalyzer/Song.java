//  Code from https://towardsdatascience.com/using-the-spotify-api-with-your-android-application-the-essentials-1a3c1bc36b9e
package com.example.spotifyanalyzer;

import org.json.JSONObject;

public class Song {
    private String id;
    private String name;
    private String artist;
    private String albumName;
    private int duration_ms;

    public Song(String id, String name, Integer duration_ms) {
        this.name = name;
        this.id = id;
        this.duration_ms = duration_ms;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getDuration() {
        return duration_ms;
    }

    public void setDuration(int duration) {
        this.duration_ms = duration_ms;
    }
}