package com.example.spotifyanalyzer.artist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

public class Artist implements Serializable {
    private String id;
    private String name;
    private String external_urls_str;
    private String[] genres;
    private String[] images_str;

    public Artist(String id, String name, String[] genres) {
        this.name = name;
        this.id = id;
        this.genres = genres;
    }

    public void setURLs(JSONObject external_urls) {
        this.external_urls_str = external_urls.toString();
    }

    public void setImages(JSONArray images) {
        this.images_str = new String[images.length()];
        for(int i = 0; i < images.length(); i++){
            try {
                this.images_str[i] = images.getJSONObject(i).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject getArtistJSON() {
        HashMap<String, String> map = new HashMap<String, String>();
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

    public String[] getGenres() {
        return this.genres;
    }
}
