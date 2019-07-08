package LegoDJ;

import java.io.Serializable;

public class Pacchetto implements Serializable{

    public static final int STOP=1;
    public static final int PLAY=2;
    public static final int PAUSE=3;
    public static final int SEEK=4;
    public static final int NEXT_SONG=5;
    public static final int PREV_SONG=6;
    public static final int NEXT_PLAYLIST=7;
    public static final int PREV_PLAYLIST=8;
    public static final int PLAYLIST=9;
    public static final int VOLUME=10;
    public static final int SONG = 11;

    private int key;
    private Serializable info;

    private static final long serialversionUID = 13;

    public Pacchetto(int key) {
        this(key,null);
    }

    public Pacchetto(int key, Serializable info) {
        this.key = key;
        this.info = info;
    }

    public int getKey() {
        return key;
    }
    public Serializable getInfo() {
        return info;
    }

}