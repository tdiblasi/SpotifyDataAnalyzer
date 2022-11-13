package com.example.spotifyanalyzer.artist;

import org.json.JSONObject;

import java.util.HashMap;

public class Artist {
    private String id;
    private String name;
    private String artist;
    private String albumName;
    private int duration_ms;

    public Artist(String id, String name, Integer duration_ms) {
        this.name = name;
        this.id = id;
        this.duration_ms = duration_ms;
    }

    public JSONObject getArtistJSON() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", this.id);
        map.put("name", this.name);
        map.put("artist", this.artist);
        map.put("albumName", this.albumName);
        map.put("duration_ms", ""+this.duration_ms);
        return new JSONObject(map);
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
