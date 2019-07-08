package com.pirati.dei.set.legodj.gui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import com.pirati.dei.set.legodj.R;
import com.pirati.dei.set.legodj.gui.adapter.ItemListener;
import com.pirati.dei.set.legodj.gui.adapter.SongAdapter;
import com.pirati.dei.set.legodj.gui.fragment.FragmentAddList;
import com.pirati.dei.set.legodj.gui.swipe.RecyclerItemTouchHelper;
import com.pirati.dei.set.legodj.gui.utility.ButtonHandler;
import com.pirati.dei.set.legodj.gui.utility.SeekHandler;
import com.pirati.dei.set.legodj.music.ConcretePlayer;
import com.pirati.dei.set.legodj.music.Player;
import com.pirati.dei.set.legodj.music.Playlist;
import com.pirati.dei.set.legodj.music.Song;
import com.pirati.dei.set.legodj.service.MusicService;

import java.util.ArrayList;


public class BraniActivity extends AppCompatActivity{
    private static final String TAG = "BraniActivity";
    public static final String PLAYLIST = "plll";
    private int playlistId;

    private RecyclerView.LayoutManager lm;
    private RecyclerView rv;

    //service
    private MusicService musicSrv;
    private Intent playIntent;
    //binding
    private boolean musicBound = false;
    //player
    private Player player;
    private Playlist playlist;
    private SongAdapter songAdapter;

    private Menu menu;
    MenuItem add;

    FragmentManager fm;
    FragmentAddList AddDialog;
    SeekBar sb;
    private SeekHandler seekth;

    private Handler bHandler = new Handler();

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();
            musicBound = true;
            player = musicSrv.getPlayer();

            playlist = player.getPlaylist().get(playlistId);

            songAdapter = new SongAdapter(playlist, new ItemListener<Song>() {
                @Override
                public void onClickListener(Song song) {
                        player.play(playlist, song);
                }

                @Override
                public void onRemove(Song song) {
                    if(menu != null & add != null){
                        if (playlist.size() == player.getAll().size()) {
                            add.setVisible(false);
                        } else {
                            add.setVisible(true);
                        }
                    }
                }
            });

            rv.setAdapter(songAdapter);

            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, (viewHolder, direction, position) -> {
                if (viewHolder instanceof SongAdapter.SongItem) {
                    String name1 = playlist.get(position).getTitle();
                    final Song deletedItem = playlist.get(position);
                    songAdapter.removeItem(position);
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.coord), name1 + " Rimossa!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Annulla", view -> {

                        // undo is selected, restore the deleted item
                        songAdapter.restoreItem(deletedItem, -1);
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
            });
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv);


            setTitle(playlist.getName());

            new ButtonHandler(player,BraniActivity.this,
                    findViewById(R.id.play),
                    findViewById(R.id.prev),
                    findViewById(R.id.next),
                    findViewById(R.id.songName) );

            //Log.d("BRANIACT","creo il thread");
            sb = findViewById(R.id.seekBar);
            seekth = new SeekHandler(player,sb,bHandler, findViewById(R.id.remainingTime), findViewById(R.id.totalTime));
            seekth.start();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu=menu;

        getMenuInflater().inflate(R.menu.menu_aggiungi_brani, menu);

        add = menu.findItem(R.id.add);

        add.setOnMenuItemClickListener(menuItem -> {

            fm = getSupportFragmentManager();

            ArrayList<Song> temp = new ArrayList<>(player.getAll());
            for (Song song : playlist.getAll()) {
                temp.remove(song);
            }

            AddDialog = FragmentAddList.newInstance(getString(R.string.add_song_frag_title), playlist, temp, songAdapter, () -> {
                songAdapter.notifyDataSetChanged();

                if(player.getAll().size()==playlist.size()){
                    add.setVisible(false);
                }
            });

            AddDialog.show(fm, "fragment_alert");


            return false;
        });

        if (playlist != null && player != null){
            add.setVisible(playlist.size() != player.getAll().size());
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        try {
            unbindService(musicConnection);
            musicSrv = null;
            seekth.interrupt();
        } catch(Exception ignored){
        }
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brani);

        rv = findViewById(R.id.rv_brani);
        lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(lm);

        playlistId = getIntent().getIntExtra(PLAYLIST, 0);
        Log.e(TAG, "" + playlistId);
        if (playlistId < 0 || playlistId >= ConcretePlayer.N_PLAYLIST) {
            playlistId = 0;

        }
    }

}
