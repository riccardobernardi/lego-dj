package com.pirati.dei.set.legodj.music;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


class MusicLoader {
    private ArrayList<Song> allSong;
    private ConcretePlaylist[] playlists;
    private SharedPreferences sharedPref;
    private ContentResolver musicResolver;

    static String generateKey(int idP, long idS){
        return String.format(Locale.ITALY, "%d-%d",idP,idS).toUpperCase();
    }

    MusicLoader(SharedPreferences sharedPref, ContentResolver musicResolver) {
        allSong=new ArrayList<>();
        playlists=new ConcretePlaylist[ConcretePlayer.N_PLAYLIST];
        this.sharedPref=sharedPref;
        this.musicResolver=musicResolver;

        for(int i = 0; i<ConcretePlayer.N_PLAYLIST; i++){
            playlists[i]=new ConcretePlaylist(i,sharedPref);
        }
    }

    void load(){
        allSong.clear();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        //iterate over results if valid
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex (android.provider.MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex (MediaStore.Audio.Media.DURATION);

            //add songs to list
            do {
                long id = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                long duration = musicCursor.getLong(durationColumn);
                Song s=new Song(title,artist,id,duration);
                allSong.add(s);

                for(int i = 0; i< ConcretePlayer.N_PLAYLIST; i++){
                    boolean v = sharedPref.getBoolean(generateKey(i,id),false);

                    if(v){
                        playlists[i].add(s,false);
                    }
                }
            }
            while (musicCursor.moveToNext());
            musicCursor.close();

        }
    }

    ArrayList<Song> getAllSong() {
        return allSong;
    }

    ArrayList<Playlist> getPlaylists() {
        return new ArrayList<>(Arrays.asList(playlists));
    }
}
