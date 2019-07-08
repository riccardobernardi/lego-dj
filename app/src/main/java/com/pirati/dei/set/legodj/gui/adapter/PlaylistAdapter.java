package com.pirati.dei.set.legodj.gui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pirati.dei.set.legodj.R;
import com.pirati.dei.set.legodj.music.Playlist;
import com.pirati.dei.set.legodj.music.Song;

import java.util.ArrayList;
import java.util.Iterator;

import static com.pirati.dei.set.legodj.gui.utility.Converter.conv;


public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistItem> {
    private static final String TAG = "PLAYLISTADAPTER";
    private ArrayList<Playlist> playlistList;
    private ItemListener<Playlist> listener;

    public PlaylistAdapter(ArrayList<Playlist> content, ItemListener<Playlist> listener) {
        this.playlistList = content;
        //this.tags = tags;
        this.listener = listener;
    }

    //singola playlist
    class PlaylistItem extends RecyclerView.ViewHolder {
        TextView Title;
        TextView len;
        TextView time;
        TextView color;


        PlaylistItem(View itemView, int viewType) {
            super(itemView);
            Title = itemView.findViewById(R.id.playlist_name);
            len = itemView.findViewById(R.id.playlist_len);
            time = itemView.findViewById(R.id.playlist_time);
            color = itemView.findViewById(R.id.playlist_color);
        }
    }

    @NonNull
    @Override
    public PlaylistItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_item, parent, false);

        return new PlaylistItem(itemView, viewType);
    }


    @Override
    public void onBindViewHolder(@NonNull PlaylistItem holder, int position) {
        Playlist e = playlistList.get(position);
        holder.Title.setText(e.getName());
        holder.len.setText(String.valueOf(e.size()));
        holder.color.setBackgroundColor(e.getColor().toARGB32());

        Iterator<Song> i = e.iterator();
        int acc=0;
        while(i.hasNext()){
            Song s = i.next();
            acc+=s.getDuration();
        }
        holder.time.setText(conv(acc));


        //con questo inserisco i bottoni in piu uso questo
        holder.itemView.setOnClickListener(v -> listener.onClickListener(e));
        holder.itemView.setOnLongClickListener(v -> {listener.onLongClickListener(e); return true;});
        Log.d(TAG,""+e.getColor().toInt());
        //holder.Title.setTextColor(e.getColor().toARGB32());
        //holder.itemView.setBackgroundColor(e.getColor().toARGB32());
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

}
