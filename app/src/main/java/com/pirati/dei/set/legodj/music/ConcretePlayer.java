package com.pirati.dei.set.legodj.music;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import com.pirati.dei.set.legodj.R;

import java.util.ArrayList;
import java.util.Iterator;

public class ConcretePlayer implements Player, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{
    private Context context;
    private ContentResolver musicResolver;
    public static final Song EMPTY_SONG = new Song("","",-1,-1);

    private MediaPlayer player;
    private ArrayList<Song> songs;
    private ArrayList<Playlist> playlists;
    public static final int N_PLAYLIST=6;

    private int pos;
    private Playlist currentPlaylist;
    private boolean isPlaying =false;

    private ArrayList<OnChangeListener> listeners;
    private boolean first = true;

    public ConcretePlayer(Context context, ContentResolver musicResolver) {
        this.context = context;
        this.musicResolver=musicResolver;
        load();
        initMusicPlayer();

        pos=0;
        currentPlaylist = playlists.get(0);
        findPlaylistNotEmpty();
        listeners=new ArrayList<>();
    }


    private void load(){
        MusicLoader m=new MusicLoader( context.getSharedPreferences(
                context.getString(R.string.playlist_file), Context.MODE_PRIVATE),musicResolver);
        m.load();
        songs=m.getAllSong();
        playlists=m.getPlaylists();
    }


    private void initMusicPlayer(){
        player=new MediaPlayer();

        //set player properties
        player.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    private void findPlaylistNotEmpty(){
        Iterator<Playlist> it =playlists.iterator();
        while(it.hasNext() && currentPlaylist.size()==0){
            Playlist temp=it.next();
            if(temp.size()>0){
                currentPlaylist=temp;
            }
        }
    }


    @Override
    public void play() {
        if(currentPlaylist.isEmpty()){
            findPlaylistNotEmpty();
            pos=0;
        }
        if (currentPlaylist.isEmpty() || pos >= currentPlaylist.size()) {
            stop();
            return;
        }

        if(!isPlaying && !first) {
            onPrepared(player);
        }
        else{
            first = false;
            player.reset();

            //get id
            long currSong = currentPlaylist.get(pos).getId();
            //set uri
            Uri trackUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    currSong);
            //set the data source
            try {
                player.setDataSource(context, trackUri);
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }
            player.prepareAsync();
        }
    }
    @Override
    public void pause() {
        player.pause();
        isPlaying =false;
        for (OnChangeListener list: listeners){
            list.musicOnPause();
        }
    }

    @Override
    public void play(Playlist p, int pos) {
        currentPlaylist=p;
        this.pos=pos;
        play();
    }

    @Override
    public  void nextSong() {
        try {
            pos = (pos + 1) % currentPlaylist.size();
            isPlaying = true;
            play();
        }
        catch(Exception e){
            stop();
        }
    }

    @Override
    public void prevSong() {
        try {
            pos = (pos - 1 + currentPlaylist.size()) % currentPlaylist.size();
            isPlaying = true;
            play();
        }catch(Exception e){
            stop();
        }
    }

    @Override
    public void nextPlaylist() {
        int i=(playlists.indexOf(currentPlaylist)+1)%N_PLAYLIST;
        currentPlaylist=playlists.get(i);
        pos=0;
        play();
    }

    @Override
    public void stop() {
        isPlaying =false;
        player.stop();

        for (OnChangeListener list: listeners){
            list.musicOnStop();
        }

    }

    @Override
    public void destroy() {
        //player.stop(); //sembra illegale
        player.release();
    }

    @Override
    public Song getCurrentSong(){
        try {
            return currentPlaylist.get(pos);
        }
        catch(Exception e){
            return EMPTY_SONG;
        }
    }

    @Override
    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public int getCurrentPosition() {
        try {
            return player.getCurrentPosition();
        }catch(Exception e){
            return 0;
        }
    }

    @Override
    public void seek(int pos) {
        player.seekTo(pos);
    }

    @Override
    public ArrayList<Song> getAll() {
        return new ArrayList<>(songs);
    }

    @Override
    public ArrayList<Playlist> getPlaylist() {
        return new ArrayList<>(playlists);
    }


    @Override
    public void addOnChangeListener(OnChangeListener list) {
        listeners.add(list);
    }

    @Override
    public void removeOnChangeListener(OnChangeListener list) {
        listeners.remove(list);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        /*mp.reset();
        isPlaying=true;*/
        nextSong();

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        isPlaying=false;

        for (OnChangeListener list: listeners){
            list.musicOnStop();
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        isPlaying =true;
        for (OnChangeListener list: listeners){
            list.musicOnStart();
        }
    }
}
