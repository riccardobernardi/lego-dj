package com.pirati.dei.set.legodj.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.pirati.dei.set.legodj.R;
import com.pirati.dei.set.legodj.gui.activity.BraniActivity;
import com.pirati.dei.set.legodj.gui.activity.MainActivity;
import com.pirati.dei.set.legodj.music.ConcretePlayer;
import com.pirati.dei.set.legodj.music.Playlist;
import com.pirati.dei.set.legodj.music.Song;
import com.pirati.dei.set.legodj.service.MusicService;

import static android.app.Notification.VISIBILITY_PUBLIC;

public class CustomNotification {


    public static final int NOTIFY_ID = 13;
    private static final String CHANNEL_ID = "Channel_ID";
    public static final int PLAY = 1;
    public static final int PAUSE = 0;

    private MusicService musicService;

    public CustomNotification(MusicService musicService) {
        this.musicService = musicService;
    }

    public Notification getNotification(Playlist playlist, Song song, int status){
        if(song == null || song == ConcretePlayer.EMPTY_SONG){
            return getDefaultNotification();
        }

        //creating main intent
        Intent intent = new Intent(musicService, BraniActivity.class);
        Log.e("CustomNotification", "playlist " + playlist.getName() + " " + playlist.getId());
        intent.putExtra(BraniActivity.PLAYLIST, playlist.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(musicService, 0, intent, 0);

        //creating media button intents
        //play and pause intent
        Intent playPauseIntent = new Intent();
        if(status==PAUSE)
            playPauseIntent.setAction(NotificationReceiver.ACTION_PLAY);
        else if (status==PLAY)
            playPauseIntent.setAction(NotificationReceiver.ACTION_PAUSE);
        PendingIntent playPausePending = PendingIntent.getBroadcast(musicService, 0, playPauseIntent, 0);


        //next song intent
        Intent nextIntent = new Intent();
        nextIntent.setAction(NotificationReceiver.ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(musicService, 0, nextIntent, 0);

        //previous song intent
        Intent prevIntent = new Intent();
        prevIntent.setAction(NotificationReceiver.ACTION_PREV);
        PendingIntent prevPending = PendingIntent.getBroadcast(musicService, 0, prevIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(musicService, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setContentTitle(song.getTitle() + " - "+ song.getArtist())
                .setContentText(playlist.getName())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setVisibility(VISIBILITY_PUBLIC)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle())
                .addAction(new NotificationCompat.Action(R.drawable.ic_skip_previous_black_24dp, "Previous", prevPending));
        if(status==PAUSE)
            mBuilder.addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp, "Play", playPausePending));
        else if(status==PLAY)
            mBuilder.addAction(new NotificationCompat.Action(R.drawable.ic_pause_black_24dp, "Pause", playPausePending));

        mBuilder.addAction(new NotificationCompat.Action(R.drawable.ic_skip_next_black_24dp, "Next", nextPending));

        return mBuilder.build();

    }

    public Notification getDefaultNotification(){
        Intent intent = new Intent(musicService, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(musicService, 0, intent, 0);

        Intent playIntent = new Intent();
        playIntent.setAction(NotificationReceiver.ACTION_PLAY);

        PendingIntent pausePending = PendingIntent.getBroadcast(musicService, 0, playIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(musicService, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setContentTitle("LegoDJ")
                .setContentText("BEST MEDIA PLAYER APP!!!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle())
                .setVisibility(VISIBILITY_PUBLIC)
                .addAction(
                        new NotificationCompat.Action(
                                R.drawable.ic_play_arrow_black_24dp,
                                "Play",
                                pausePending
                        )
                );
        return mBuilder.build();
    }

    public void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "LegoDJ";
            String description = "BEST.APP.EVER";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            channel.enableVibration(false);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            //NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

}
