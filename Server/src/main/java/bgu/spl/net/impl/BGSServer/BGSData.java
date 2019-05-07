package bgu.spl.net.impl.BGSServer;
import bgu.spl.net.impl.BGSServer.Messages.NOTIFICATIONMessage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BGSData {
    private ConcurrentHashMap<String,User> userList;
    private ConcurrentHashMap<String,Integer> onlineUsersMap;// keep track of all online users
    private ConcurrentHashMap<String, CopyOnWriteArrayList<NOTIFICATIONMessage>> awaitingMessages;
                                                            // for every username, keep track of all notification
                                                            // messages that should be sent to him upon logged in
    private Object lock;
    public BGSData() {
        userList = new ConcurrentHashMap<>();
        onlineUsersMap = new ConcurrentHashMap<>();
        awaitingMessages=new ConcurrentHashMap<>();
        lock=new Object();
    }
    //get User object with username or null if not exists
    public User getRegisteredUser(String username){
        return userList.get(username);
    }
    public boolean isRegistered(String username){return getRegisteredUser(username)!=null;}
    public boolean addUser(String username, String password) {
            if(userList.put(username,new User(username, password))==null)
                return true;
            return false;
    }
    public synchronized boolean addOnlneUser(String username,int connectionId){
         if(onlineUsersMap.putIfAbsent(username,connectionId)==null)
             return true;
         return false;
    }
    public boolean isConnected(String username){return onlineUsersMap.containsKey(username);}
    public boolean isConnected(int connectionId){return onlineUsersMap.containsValue(connectionId);}
    public void disconnectUser(int connectionId){onlineUsersMap.values().remove(connectionId);}
    //returns a username mapped to connectionId if exists, otherwise returns null.
    public String getUserName(int connectionId){
        for(Map.Entry<String,Integer> entry:onlineUsersMap.entrySet())
            if(entry.getValue()==connectionId)
                return entry.getKey();
            return null;
    }
    public int getConnectionId(String username){return onlineUsersMap.get(username);}
    public void addAwaitingNotification(String user,NOTIFICATIONMessage message){
        List list=awaitingMessages.computeIfAbsent(user,k->new CopyOnWriteArrayList<>());
        list.add(message);
    }
    public List<NOTIFICATIONMessage> getNotifications(String username){return awaitingMessages.get(username);}
    public List<String> getUserNamesList(){
        List<String>ret=new LinkedList<>();
        for(Map.Entry<String,User> entry: userList.entrySet())
            ret.add(entry.getKey());
        return ret;
    }

    //returns a list of users following username
    public List<String> getFollowers(String username){
        List<String> followers=new LinkedList<>();
        for(User user:userList.values())
            if(user.isFollowing(username))
                followers.add(user.getUsername());
        return followers;
    }

}