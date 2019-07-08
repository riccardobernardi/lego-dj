package com.pirati.dei.set.legodj.music;

public interface OnChangeListener {
    void musicOnPause();
    void musicOnStop();
    void musicOnStart();

    void onChangePlaylist(Playlist p);
    void onChangeSong(Song p);
}
