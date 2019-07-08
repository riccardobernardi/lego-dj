package com.pirati.dei.set.legodj.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.pirati.dei.set.legodj.music.Player;

public class NotificationReceiver extends BroadcastReceiver {

    public final static  String ACTION_PLAY = "legodj_play";
    public final static  String ACTION_PAUSE = "legodj_pause";
    public final static  String ACTION_STOP = "legodj_stop";
    public final static  String ACTION_NEXT = "legodj_next";
    public final static  String ACTION_PREV = "legodj_prev";
    public final static  String ACTION_PLAYLIST = "legodj_playlist";

    public static IntentFilter getIntentFilter(){
        IntentFilter filter=new IntentFilter();
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_PREV);
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_STOP);
        filter.addAction(ACTION_PLAYLIST);
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        return filter;
    }

    private Player player;

    public NotificationReceiver(Player player) {
        this.player = player;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(ACTION_PLAY)){
            player.play();
        }
        else if(intent.getAction().equals(ACTION_PAUSE)){
            player.pause();
        }
        else if(intent.getAction().equals(ACTION_STOP)){
            player.stop();
        }
        else if(intent.getAction().equals(ACTION_NEXT)){
            player.nextSong();
        }
        else if(intent.getAction().equals(ACTION_PREV)){
            player.prevSong();
        }
        else if(intent.getAction().equals(ACTION_PLAYLIST)){
            player.play(player.getPlaylist().get(intent.getIntExtra(ACTION_PLAYLIST,0)));
        }
        else if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)){
            player.pause();
        }
    }
}
