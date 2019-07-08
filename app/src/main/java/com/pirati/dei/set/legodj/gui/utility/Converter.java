package com.pirati.dei.set.legodj.gui.utility;

public class Converter {

    public static String conv(int sec){

        sec=sec/1000;

        int secondi = sec%60;
        String seconds = secondi<10?"0"+secondi: String.valueOf(secondi);
        int minuti = (sec/60)%60;
        String minutes = minuti<10?"0"+minuti: String.valueOf(minuti);
        int ore = (sec/3600)%60;
        String hours = ore<10?"0"+ore: String.valueOf(ore);

        return hours + ":" + minutes + ":" + seconds;
    }
}
