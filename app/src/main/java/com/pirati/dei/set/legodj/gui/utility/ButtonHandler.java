package com.pirati.dei.set.legodj.gui.utility;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pirati.dei.set.legodj.R;
import com.pirati.dei.set.legodj.music.OnChangeListener;
import com.pirati.dei.set.legodj.music.Player;
import com.pirati.dei.set.legodj.music.Playlist;
import com.pirati.dei.set.legodj.music.Song;

public class ButtonHandler implements OnChangeListener {

    private Player player;
    private Activity activity;
    private FloatingActionButton play;
    private ImageButton prev;
    private ImageButton next;
    private TextView text;

    public ButtonHandler(Player player, Activity activity, FloatingActionButton play, ImageButton prev, ImageButton next, TextView text) {
        this.player = player;
        this.activity = activity;
        this.play = play;
        this.prev = prev;
        this.next = next;
        this.text = text;

        player.addOnChangeListener(this);

        play.setOnClickListener(view -> {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.play();
            }
        });

        prev.setOnClickListener(view -> player.prevSong());
        next.setOnClickListener(view -> player.nextSong());
        setPlayBottom();

        if (player.isPlaying()) {
            setText();
        }
    }


    private void setPlayBottom(){
        activity.runOnUiThread(()-> play.setImageResource(player.isPlaying() ? R.drawable.ic_pause_black_40dp : R.drawable.ic_play_arrow_black_40dp));
    }

    private void setText(){
        activity.runOnUiThread(() -> text.setText(player.getCurrentSong().getTitle()));
    }

    @Override
    public void musicOnPause() {
        setPlayBottom();
    }

    @Override
    public void musicOnStop() {
        setPlayBottom();
    }

    @Override
    public void musicOnStart() {
        setPlayBottom();
        setText();
    }

    @Override
    public void onChangePlaylist(Playlist p) {

    }

    @Override
    public void onChangeSong(Song p) {
    }
}
