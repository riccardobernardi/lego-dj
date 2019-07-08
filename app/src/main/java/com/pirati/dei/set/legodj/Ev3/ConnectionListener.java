package com.pirati.dei.set.legodj.Ev3;

public interface ConnectionListener {
    void onConnect();
    void onDisconnect();
    void onError();
}
