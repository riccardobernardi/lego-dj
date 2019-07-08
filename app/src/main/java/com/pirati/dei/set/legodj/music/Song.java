package com.pirati.dei.set.legodj.music;

import java.io.Serializable;

public class Song implements Serializable {
    private String title;
    private String artist;
    private long id;
    private long duration;
    private static final long serialversionUID = 14;

    public Song(String title, String artist, long id, long duration) {
        this.title = title;
        this.artist = artist;
        this.id = id;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public long getId() {
        return id;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object obj) {
        return id == ((Song) obj).id;
    }
}
