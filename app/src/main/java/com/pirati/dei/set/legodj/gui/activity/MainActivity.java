package com.pirati.dei.set.legodj.gui.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;

import com.pirati.dei.set.legodj.Ev3.ConnectionListener;
import com.pirati.dei.set.legodj.R;
import com.pirati.dei.set.legodj.gui.adapter.ItemListener;
import com.pirati.dei.set.legodj.gui.adapter.PlaylistAdapter;
import com.pirati.dei.set.legodj.gui.utility.ButtonHandler;
import com.pirati.dei.set.legodj.gui.utility.SeekHandler;
import com.pirati.dei.set.legodj.music.Player;
import com.pirati.dei.set.legodj.music.Playlist;
import com.pirati.dei.set.legodj.service.MusicService;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main activity";
    //service
    private MusicService musicSrv;
    private Intent playIntent;
    //binding
    private boolean musicBound = false;
    //player
    private Player player;

    private RecyclerView.LayoutManager lm;
    private RecyclerView rv;

    SeekBar sb;
    private SeekHandler seekth;
    private Handler bHandler = new Handler();

    MenuItem disconnect;
    MenuItem connect;


    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();
            musicBound = true;
            player = musicSrv.getPlayer();

            ArrayList<Playlist> pll = player.getPlaylist();

            PlaylistAdapter pa = new PlaylistAdapter(pll, new ItemListener<Playlist>() {
                @Override
                public void onClickListener(Playlist playlist) {
                    startBraniActivity(playlist);
                    rv.getAdapter().notifyDataSetChanged();
                    seekth.interrupt();
                }

                @Override
                public void onLongClickListener(Playlist playlist) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.titolo_playlist);
                    final EditText input = new EditText(MainActivity.this);
                    final String oldTitle=playlist.getName();
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setText(playlist.getName());
                    builder.setView(input);

                    builder.setPositiveButton(R.string.conferma, (dialog, which) -> {
                        String m_Text = input.getText().toString();
                        if (!m_Text.equals("") && !m_Text.equals(oldTitle)) {
                            playlist.setName(m_Text);
                            rv.getAdapter().notifyItemChanged(playlist.getId());

                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.coordinator), "Playlist rinominata", Snackbar.LENGTH_LONG);
                            snackbar.setAction(R.string.cancel, view -> {
                                rv.getAdapter().notifyItemChanged(playlist.getId());
                                playlist.setName(oldTitle);
                            });
                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();

                        }
                    });
                    builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
                    builder.show();
                }
            });

            rv.setAdapter(pa);

            musicSrv.addConnectionListener(new ConnectionListener() {

                @Override
                public void onConnect() {
                    aux(getString(R.string.Connesso));
                }

                @Override
                public void onDisconnect() {
                    aux(getString(R.string.Disconesso));
                }

                @Override
                public void onError() {
                    aux(getString(R.string.NonConnesso));
                }

                private void aux(String msg) {
                    setVisibleMenu();
                    Snackbar.make(findViewById(R.id.coordinator), msg, Snackbar.LENGTH_SHORT).show();
                }
            });


            new ButtonHandler(player,MainActivity.this,
                    findViewById(R.id.play),
                    findViewById(R.id.prev),
                    findViewById(R.id.next),
                    findViewById(R.id.songName) );


            sb = findViewById(R.id.seekBar);
            seekth = new SeekHandler(player, sb, bHandler, findViewById(R.id.remainingTime), findViewById(R.id.totalTime));
            seekth.start();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv_main);
        lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(lm);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isStoragePermissionGranted()) {
            startMusicService();
        }

    }

    private void startMusicService() {
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Log.d("MAINACT","creo il thread");
            seekth = new SeekHandler(player, sb, bHandler, findViewById(R.id.remainingTime), findViewById(R.id.totalTime));
            seekth.start();
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            startMusicService();
        } else {
            //no permessi
            Log.v(TAG, "No Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);

        disconnect = menu.findItem(R.id.disconnect);
        disconnect.setVisible(false);

        connect = menu.findItem(R.id.connect);

        connect.setOnMenuItemClickListener(menuItem -> {
            musicSrv.connect();
            return false;
        });

        disconnect.setOnMenuItemClickListener(menuItem -> {
            musicSrv.disconnect();
            return false;
        });
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu){
        setVisibleMenu();
        return true;
    }

    private void setVisibleMenu() {
        if (musicSrv != null) {
            runOnUiThread(()-> {
                connect.setVisible(!musicSrv.isConnected());
                disconnect.setVisible(musicSrv.isConnected());
            });
        }
    }

    @Override
    protected void onDestroy() {
        seekth.interrupt();
        unbindService(musicConnection);
        stopService(playIntent);
        musicSrv = null;
        super.onDestroy();
    }

    void startBraniActivity(Playlist pl) {
        Intent i = new Intent(this, BraniActivity.class);
        i.putExtra(BraniActivity.PLAYLIST, pl.getId());
        startActivity(i);
    }
}
