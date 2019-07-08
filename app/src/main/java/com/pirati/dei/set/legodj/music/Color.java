package com.pirati.dei.set.legodj.music;

public enum Color {
    TRANSPARENT,
    BLACK,
    BLUE,
    GREEN,
    YELLOW,
    RED,
    WHITE,
    BROWN;

    public int toARGB32() {
        switch (this) {
            case TRANSPARENT:   return 0x00000000;
            case BLACK:         return 0xff000000;
            case BLUE:          return 0xff0000ff;
            case GREEN:         return 0xff00ff00;
            case YELLOW:        return 0xffffff00;
            case RED:           return 0xffff0000;
            case WHITE:         return 0xffffffff;
            default:            return 0xff000000 | 180 << 16 | 142 << 8 | 92;
        }
    }

    public String toString(){
        switch (this) {
            case TRANSPARENT:   return "TRASPARENTE";
            case BLACK:         return "NERO";
            case BLUE:          return "BLU";
            case GREEN:         return "VERDE";
            case YELLOW:        return "GIALLO";
            case RED:           return "ROSSO";
            case WHITE:         return "BIANCO";
            default:            return "BIANCO";
        }
    }

    public int toInt(){
        switch (this) {
            case TRANSPARENT:   return -1;
            case BLACK:         return 0;
            case BLUE:          return 1;
            case GREEN:         return 2;
            case YELLOW:        return 3;
            case RED:           return 4;
            case WHITE:         return 5;
            default:            return -2;
        }
    }

    public static Color getColor(int i){
        switch (i) {
            case -1:          return TRANSPARENT;
            case 0:           return BLACK;
            case 1:           return BLUE;
            case 2:           return GREEN;
            case 3:           return YELLOW;
            case 4:           return RED;
            case 5:           return WHITE;
            default:          return  BLACK;

        }
    }

}
