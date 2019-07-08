package com.pirati.dei.set.legodj.music;

import java.util.ArrayList;

public interface Player {

    void pause();

    void play();

    default void play(Playlist p, Song s){
        play(p,p.getAll().indexOf(s));
    }

    default void play(Playlist p){
        play(p,0);
    }

    void play(Playlist p, int pos);

    void nextSong();

    void prevSong();

    void nextPlaylist();

    void stop();

    void destroy();

    Song getCurrentSong();

    Playlist getCurrentPlaylist();

    boolean isPlaying();

    int getCurrentPosition();

    void seek(int pos);

    ArrayList<Song> getAll();

    ArrayList<Playlist> getPlaylist(); //copia non originale

    void addOnChangeListener(OnChangeListener list);

    void removeOnChangeListener(OnChangeListener list);

}
