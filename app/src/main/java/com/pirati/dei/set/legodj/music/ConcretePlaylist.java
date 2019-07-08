package com.pirati.dei.set.legodj.music;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;


public class ConcretePlaylist implements Playlist {

    private SharedPreferences sharedPref;
    private ArrayList<Song> songs;
    private String name;
    private Color c;
    private int id;


    ConcretePlaylist(int id, SharedPreferences sharedPref) {
        songs = new ArrayList<>();
        this.sharedPref=sharedPref;
        this.id=id;
        c=Color.getColor(id);
        name=sharedPref.getString(c.toString().toUpperCase(Locale.ITALY), c.toString());
    }

    public boolean add(Song s,boolean save){
        if(songs.contains(s)){
            return false;
        }
        songs.add(s);
        if(save){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(MusicLoader.generateKey(id,s.getId()),true);
            editor.apply();
        }
        return true;
    }

    @Override
    public boolean add(Song s) {
        return add(s,true);
    }

    @Override
    public boolean add(ArrayList<Song> s) {
        boolean ris=true;
        for(int i=0;i<s.size();i++){
            ris = ris && add(s.get(i));
        }
        return ris;
    }

    @Override
    public boolean del(Song s) {
        songs.remove(s);
        SharedPreferences.Editor editor = sharedPref.edit();
       //editor.remove(String.format("%d-%d",id,s.getId()).toUpperCase(Locale.ITALY));
        editor.remove(MusicLoader.generateKey(id,s.getId()));
        editor.apply();
        return true;
    }

    @Override
    public int size() {
        return songs.size();
    }

    @Override
    public Song get(int pos) throws IndexOutOfBoundsException{
        return songs.get(pos);
    }

    @Override
    public ArrayList<Song> getAll() {
        return new ArrayList<>(songs);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name=name;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(c.toString().toUpperCase(Locale.ITALY),name);
        editor.apply();
    }

    @Override
    public Color getColor() {
        return c;
    }

    @Override
    public int getId() {
        return id;
    }

    @NonNull
    @Override
    public Iterator<Song> iterator() {
        return songs.iterator();
    }

    @Override
    public boolean equals(Object e){
        return id==((ConcretePlaylist) e).id;
    }
}
