package com.pirati.dei.set.legodj.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.pirati.dei.set.legodj.Ev3.ConnectionInterface;
import com.pirati.dei.set.legodj.Ev3.ConnectionListener;
import com.pirati.dei.set.legodj.Ev3.Ev3Connection;
import com.pirati.dei.set.legodj.music.ConcretePlayer;
import com.pirati.dei.set.legodj.music.OnChangeListener;
import com.pirati.dei.set.legodj.music.Player;
import com.pirati.dei.set.legodj.music.Playlist;
import com.pirati.dei.set.legodj.music.Song;
import com.pirati.dei.set.legodj.notification.CustomNotification;
import com.pirati.dei.set.legodj.notification.NotificationReceiver;


public class MusicService extends Service implements ConnectionInterface, OnChangeListener {


    private Player player;
    private final IBinder musicBind = new MusicBinder();
    private Ev3Connection ev3;
    private CustomNotification customNotification;
    private NotificationManager notificationManager;
    NotificationReceiver notificationReceiver;


    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        return false;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("SERVIZIO","----START");
        player=new ConcretePlayer(getApplicationContext(), getContentResolver() );
        player.addOnChangeListener(this);

        ev3 = new Ev3Connection(player, this);
        ev3.connect();

        notificationReceiver =new NotificationReceiver(player);
        registerReceiver(notificationReceiver, NotificationReceiver.getIntentFilter());

        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        customNotification = new CustomNotification(this);
        customNotification.createNotificationChannel(this);
        startForeground(CustomNotification.NOTIFY_ID, customNotification.getDefaultNotification());
    }

    @Override
    public void onDestroy() {
        player.destroy();
        ev3.disconnect();
        unregisterReceiver(notificationReceiver);
        super.onDestroy();
    }

    @Override
    public boolean isConnected() {
        return ev3.isConnected();
    }

    @Override
    public void connect() {
        ev3.connect();
    }

    @Override
    public void disconnect() {
        ev3.disconnect();
    }

    @Override
    public void addConnectionListener(ConnectionListener c) {
        ev3.addConnectionListener(c);
    }

    @Override
    public void musicOnPause() {
        notification(CustomNotification.PAUSE);
    }

    @Override
    public void musicOnStop() {
        notification(CustomNotification.PAUSE);
    }

    @Override
    public void musicOnStart() {
        notification(CustomNotification.PLAY);
    }

    @Override
    public void onChangePlaylist(Playlist p) {
        notification(CustomNotification.PLAY);
    }

    @Override
    public void onChangeSong(Song p) {
        notification(CustomNotification.PLAY);
    }

    private void notification(int mode){
        notificationManager.notify(CustomNotification.NOTIFY_ID, customNotification.getNotification(player.getCurrentPlaylist(), player.getCurrentSong(),mode));
    }

    public Player getPlayer() {
        return player;
    }

}
