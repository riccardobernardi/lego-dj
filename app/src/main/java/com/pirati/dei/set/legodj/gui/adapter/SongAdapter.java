package com.pirati.dei.set.legodj.gui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pirati.dei.set.legodj.R;
import com.pirati.dei.set.legodj.music.Playlist;
import com.pirati.dei.set.legodj.music.Song;

import java.util.function.UnaryOperator;

import static com.pirati.dei.set.legodj.gui.utility.Converter.conv;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongItem> {
    private static final String TAG = "PlaylistAdapter";

    private Playlist songList;
    private ItemListener<Song> listener;

    public SongAdapter(Playlist content, ItemListener<Song> listener) {
        this.songList = content;
        this.listener = listener;
    }

    //singola playlist
    public class SongItem extends RecyclerView.ViewHolder {
        TextView title;
        TextView artist;
        TextView time;
        RelativeLayout viewBackground;
        public RelativeLayout viewForeground;

        SongItem(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.song_name);
            artist= itemView.findViewById(R.id.song_artist);
            time = itemView.findViewById(R.id.song_time);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);

        }
    }

    @NonNull
    @Override
    public SongItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false);

        return new SongItem(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongItem holder, int position) {
        final Song e = songList.get(position);
        holder.title.setText(e.getTitle());
        holder.artist.setText(e.getArtist());
        holder.time.setText(String.valueOf(conv((int) e.getDuration())));

        holder.itemView.setOnClickListener(v -> listener.onClickListener(e));
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void removeItem(int position) {
        listener.onRemove(songList.get(position));
        songList.del(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Song s, int position) {
        songList.add(s);
        notifyDataSetChanged();
    }

}

