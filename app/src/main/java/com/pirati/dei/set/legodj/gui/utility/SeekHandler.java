package com.pirati.dei.set.legodj.gui.utility;

import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pirati.dei.set.legodj.music.Player;

import static com.pirati.dei.set.legodj.gui.utility.Converter.conv;

public class SeekHandler extends Thread {

    private final SeekBar sb;
    private final Player player;
    private Handler bHandler ;
    private TextView remaining;
    private TextView total;


    public SeekHandler(Player player, SeekBar sb, Handler bHandler, TextView remaining, TextView total){
        this.sb = sb;
        this.player = player;
        this.bHandler = bHandler;
        this.remaining = remaining;
        this.total=total;

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    player.seek(i);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void run() {

        if(player != null && player.isPlaying()) {
            int mediaPos_new = player.getCurrentPosition();
            int mediaMax_new = (int) player.getCurrentSong().getDuration();
            sb.setMax(mediaMax_new);
            sb.setProgress(mediaPos_new);

            //problema: quando faccio back esco dallo scope e vai in casino
            if(remaining!=null && remaining.isCursorVisible()){
                remaining.setText(conv(mediaPos_new));
            }

            if(total!=null && total.isCursorVisible()){
                total.setText(conv(mediaMax_new));
            }

            //remaining.setText(mediaPos_new);
            //total.setText(mediaMax_new);
        }

        bHandler.postDelayed(this, 200); //Looping the thread after 0.1 second

        //Log.d("SEEKTH", "non sto facendo nulla");

    }

}
