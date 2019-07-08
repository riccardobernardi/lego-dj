package com.pirati.dei.set.legodj.Ev3;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.ParcelUuid;
import android.util.Log;

import com.pirati.dei.set.legodj.music.OnChangeListener;
import com.pirati.dei.set.legodj.music.Player;
import com.pirati.dei.set.legodj.music.Playlist;
import com.pirati.dei.set.legodj.music.Song;
import com.pirati.dei.set.legodj.notification.NotificationReceiver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Set;

import LegoDJ.Pacchetto;

public class Ev3Connection implements ConnectionInterface, OnChangeListener {

    private static final String TAG = "Ev3_Connection";
    //volume
    private static final double MAX_DISTANCE = 0.20;
    private int maxVolume;
    private AudioManager audioManager;

    private Player player;

    //listener
    private ArrayList<ConnectionListener> listeners;

    private static String MAC_ADDRESS;
    private boolean isConnected;

    private BluetoothSocket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private Context context;



    public Ev3Connection(Player player, Context context) {
        this.player = player;
        this.context=context;
        isConnected =false;
        player.addOnChangeListener(this);
        listeners = new ArrayList<>();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    private void connectionError(String msg){
        isConnected = false;
        for (ConnectionListener list: listeners){
            list.onError();
        }
        Log.e(TAG, msg);

    }

    @Override
    public void connect() {
        if(!isConnected) {
            try {
                BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
                if (blueAdapter != null) {
                    if (blueAdapter.isEnabled()) {/*ritorna true se il bluetooth è attivo sul telefono*/
                        Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
                        BluetoothDevice device = searchDevice(bondedDevices);/*ritorna un bluetooth device*/
                        if (device != null) {
                            ParcelUuid[] uuids = device.getUuids();
                            this.socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                            getStream();
                        } else {
                            connectionError("No appropriate paired devices.");
                        }
                    } else {
                        connectionError("Bluetooth is disabled.");
                    }
                }
                else {
                    connectionError("No bluetooth adapter");
                }

            } catch (Exception e) {
                connectionError("Eccezione");
                //e.printStackTrace();
            }
        }

    }

    private BluetoothDevice searchDevice(Set<BluetoothDevice> devices) {
        for(BluetoothDevice device : devices){
            String deviceName=device.getName();
            if(deviceName.equals("EV3")){
                MAC_ADDRESS = device.getAddress();
                return device;
            }
        }
        return null;
    }

    private void getStream() {
        Thread t=new Thread(() -> {
            try {
                socket.connect();
                Log.w(TAG, "Device connected. MAC " + MAC_ADDRESS);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.flush();
                inputStream = new ObjectInputStream(socket.getInputStream());
                isConnected = true;
                startReceiver();
                sendInitialState();
                for (ConnectionListener list: listeners){
                    list.onConnect();
                }
            }
            catch (Exception e){
                //e.printStackTrace();
                connectionError("Stream non creati");
            }
        });
        t.start();
    }

    private void sendInitialState() {
        ArrayList<Pacchetto> temp = new ArrayList<>();
        temp.add(new Pacchetto(Pacchetto.PLAYLIST, player.getCurrentPlaylist().getId()));
        if(player.isPlaying()){
            temp.add(new Pacchetto(Pacchetto.SONG, player.getCurrentSong()));
        }
        send(temp);
    }


    @Override
    public synchronized void disconnect() {
        //Log.e(TAG, "disconnect chiamata " + isConnected);
        if(isConnected) {
            isConnected = false;
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {

                }
                inputStream = null;
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {

                }
                inputStream = null;
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {

                }
                socket = null;
            }
            Log.e(TAG, "disconnected");
            for (ConnectionListener list : listeners) {
                list.onDisconnect();
            }
        }
    }

    @Override
    public void addConnectionListener(ConnectionListener c) {
        this.listeners.add(c);
    }

    @Override
    public void musicOnPause() {
        if(isConnected){
            send(new Pacchetto(Pacchetto.PAUSE));
        }
    }

    @Override
    public void musicOnStop() {
        if(isConnected){
            send(new Pacchetto(Pacchetto.STOP));
        }
    }

    @Override
    public void musicOnStart() {
        if(isConnected){
            ArrayList<Pacchetto> temp = new ArrayList<>();
            temp.add(new Pacchetto(Pacchetto.PLAYLIST, player.getCurrentPlaylist().getColor().toInt()));
            temp.add(new Pacchetto(Pacchetto.PLAY, player.getCurrentSong()));
            send(temp);
        }
    }

    @Override
    public void onChangePlaylist(Playlist p) {
        if(isConnected){
            send(new Pacchetto(Pacchetto.PLAYLIST, player.getCurrentPlaylist().getColor().toInt()));
        }
    }

    @Override
    public void onChangeSong(Song p) {

    }


    private void setVolume(Float n) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) Math.round(n/ MAX_DISTANCE *maxVolume), 0 );
        //Log.e(TAG, "volume change" + n);
    }

    private void send(Pacchetto p){
        ArrayList<Pacchetto> temp= new ArrayList<>();
        temp.add(p);
        send(temp);
    }

    private void send(Iterable<Pacchetto> list){
        Thread t = new Thread(() -> {
            synchronized (outputStream) {
                try {

                    for (Pacchetto p : list) {
                        outputStream.writeObject(p);
                        outputStream.flush();

                       // Log.e(TAG, p.getKey() + " - " + p.getInfo());
                    }
                } catch (Exception e) {
                    disconnect();
                }
            }
        });
        t.start();
    }

    //ascoltatore
    private void startReceiver(){
        Thread t = new Thread(() -> {
            boolean flag = true;
            while (flag && isConnected) {
                try {
                    Pacchetto p = (Pacchetto) inputStream.readObject();

                    //Log.e(TAG, "------ "+p.getKey() + " - " + p.getInfo());
                    Intent intent;
                    switch (p.getKey()){

                        case Pacchetto.PLAY:
                            if (!player.isPlaying()) {
                                player.play();
                            }
                            break;

                        case Pacchetto.PAUSE:
                            if (player.isPlaying()) {
                                player.pause();
                            }
                            break;

                        case Pacchetto.NEXT_SONG:
                            intent = new Intent();
                            intent.setAction(NotificationReceiver.ACTION_NEXT);
                            context.sendBroadcast(intent);
                            //player.nextSong();
                            break;

                        case Pacchetto.PREV_SONG:
                            player.prevSong();
                            break;

                        case Pacchetto.NEXT_PLAYLIST:
                            player.nextPlaylist();
                            break;

                        case Pacchetto.PREV_PLAYLIST:
                            //non implementato
                            break;

                        case Pacchetto.PLAYLIST:

                            intent = new Intent();
                            intent.setAction(NotificationReceiver.ACTION_PLAYLIST);
                            intent.putExtra(NotificationReceiver.ACTION_PLAYLIST, p.getInfo());
                            context.sendBroadcast(intent);
                            //player.play(player.getPlaylist().get((Integer) p.getInfo())); //VA MALE!! VIENE CHIAMATA ANCHE ONPREPARED
                            break;

                        case Pacchetto.VOLUME:
                            setVolume((Float) p.getInfo());
                            break;

                    }
                }
                catch (Exception e) {
                    flag = false;
                    //Log.e(TAG, "Chiuso dalla app");
                    disconnect();
                    //e.printStackTrace();
                }
            }

        });
        t.start();
    }
}
