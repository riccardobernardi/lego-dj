package com.pirati.dei.set.legodj.gui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pirati.dei.set.legodj.R;
import com.pirati.dei.set.legodj.gui.adapter.AddSongsAdapter;
import com.pirati.dei.set.legodj.gui.adapter.ItemListener;
import com.pirati.dei.set.legodj.gui.adapter.SongAdapter;
import com.pirati.dei.set.legodj.music.Playlist;
import com.pirati.dei.set.legodj.music.Song;

import java.util.ArrayList;

public class FragmentAddList extends DialogFragment {

    //player
    private ArrayList<Song> songList;
    private RecyclerView rvf;
    private Playlist playlist;
    private SongAdapter sa;
    private onDialogDismissListener ddl;
    private String title;

    public FragmentAddList() {

    }

    public static FragmentAddList newInstance(String title, Playlist playlist, ArrayList<Song> songList, SongAdapter sa, onDialogDismissListener ddl) {
        FragmentAddList frag = new FragmentAddList();
        frag.playlist=playlist;
        frag.songList=songList;
        frag.ddl = ddl;
        frag.title=title;
        frag.sa = sa;
        return frag;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       getDialog().setTitle(title);
        return inflater.inflate(R.layout.fragment_add_list, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rvf = view.findViewById(R.id.rv_fragment);
        rvf.setLayoutManager(lm);

        rvf.setAdapter(new AddSongsAdapter(songList, new ItemListener<Song>() {

            @Override
            public void onButtonClick(Song song) {
                playlist.add(song);
                songList.remove(song);
                rvf.getAdapter().notifyDataSetChanged();
                if (songList.size()<=0 ) {
                    getDialog().dismiss();
                }

            }
        }));

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        ddl.onUpdate();
        sa.notifyDataSetChanged();
    }

    public interface onDialogDismissListener {
        void onUpdate();
    }
}
