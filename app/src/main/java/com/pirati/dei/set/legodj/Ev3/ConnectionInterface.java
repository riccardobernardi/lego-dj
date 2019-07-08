package com.pirati.dei.set.legodj.Ev3;


public interface ConnectionInterface {
    boolean isConnected();
    void connect();
    void disconnect();

    void addConnectionListener(ConnectionListener c);
}
