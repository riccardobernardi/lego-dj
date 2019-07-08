package com.pirati.dei.set.legodj.music;

import java.util.ArrayList;

public interface Playlist extends Iterable<Song> {

    boolean add(Song s);
    boolean add(ArrayList<Song> s);

    boolean del(Song s);

    default boolean del(int position){
        return del(get(position));
    }

    int size();

    default boolean isEmpty(){
        return size()==0;
    }

    Song get(int pos);

    ArrayList<Song> getAll(); //copia

    String getName();

    void setName(String name);

    Color getColor();

    int getId();
}
