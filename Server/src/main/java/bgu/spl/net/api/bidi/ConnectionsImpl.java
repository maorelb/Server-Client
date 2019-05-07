package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {
    ConcurrentHashMap<Integer, ConnectionHandler> connections;

    public ConnectionsImpl(){
        connections=new ConcurrentHashMap<>();
    }

    public void addClientToMap(int connectionID, ConnectionHandler handler) {
        connections.put(connectionID, handler);
    }

    public boolean send(int connectionId, T msg) {
        if(!connections.containsKey(connectionId))
            return false;
        connections.get(connectionId).send(msg);
        return true;
    }

    public void broadcast(T msg) {
        for(ConnectionHandler ch: connections.values())
            ch.send(msg);
    }

    public void disconnect(int connectionId) {
        connections.remove(connectionId);
    }
}
