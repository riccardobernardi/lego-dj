package com.pirati.dei.set.legodj.gui.adapter;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pirati.dei.set.legodj.R;
import com.pirati.dei.set.legodj.music.Song;

import java.util.ArrayList;

import static com.pirati.dei.set.legodj.gui.utility.Converter.conv;


public class AddSongsAdapter extends RecyclerView.Adapter<AddSongsAdapter.SongItem>  {

    private static final String TAG = "PlaylistAdapter";
    private ArrayList<Song> songList;
    private ItemListener<Song> listener;

    public void del (Song s){
        songList.remove(s);
        notifyDataSetChanged();
    }

    public AddSongsAdapter(ArrayList<Song> content, ItemListener<Song> listener) {
        this.songList = content;
        this.listener = listener;
        //Log.d(TAG, "Creato l'adapter con " + content.size() + " amici");
    }

    //singola playlist
    class SongItem extends RecyclerView.ViewHolder {
        TextView title;
        TextView artist;
        TextView time;
        FloatingActionButton add;

        SongItem(View itemView, int viewType) {
            super(itemView);
            //Inizializzo le view
            title = itemView.findViewById(R.id.song_name);
            artist= itemView.findViewById(R.id.song_artist);
            time = itemView.findViewById(R.id.song_time);
            add = itemView.findViewById(R.id.add);
            //Log.d("errore2", String.valueOf(add==null));
        }
    }

    @NonNull
    @Override
    public AddSongsAdapter.SongItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_song_item, parent, false);

        return new AddSongsAdapter.SongItem(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull AddSongsAdapter.SongItem holder, int position) {
        final Song e = songList.get(position);
        holder.title.setText(e.getTitle());
        holder.artist.setText(e.getArtist());
        holder.time.setText(conv((int) e.getDuration()));

        //con questo inserisco i bottoni in piu
        holder.itemView.setOnClickListener(v -> listener.onClickListener(e));

        holder.add.setOnClickListener(v -> listener.onButtonClick(e));
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }


}
