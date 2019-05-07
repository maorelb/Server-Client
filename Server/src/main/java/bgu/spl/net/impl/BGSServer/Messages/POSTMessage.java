package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSData;
import bgu.spl.net.impl.BGSServer.Message;
import java.util.LinkedList;
import java.util.List;

public class POSTMessage extends Message {
    private String content;

    public POSTMessage(String content) {
        super(5);
        this.content = content;
    }

    @Override
    public void execute(BGSData data, Connections<Message> connections, int connectionId) {
        if (!data.isConnected(connectionId))
            new ErrorMessage(opcode).execute(data, connections, connectionId);
        else {
            new ACKMessage(opcode).execute(data, connections, connectionId);
            String postingUser=data.getUserName(connectionId);
            data.getRegisteredUser(postingUser).increaseNumOfPosts();
            List<String> followers = data.getFollowers(postingUser);
            List<String> reciepients=new LinkedList<>(followers);
            for(String name: getTaggedUsers(content))
                if(data.isRegistered(name))reciepients.add(name);
             //for every username x, if x is online sends a notification message
            // otherwise, add the message to the awaitingMessages list of x
            // those messages will be sent to him upon logged in.
             for (String username : reciepients) {
                 NOTIFICATIONMessage m = new NOTIFICATIONMessage(content,1,postingUser,username);
                 if (data.isConnected(username))
                    connections.send(data.getConnectionId(username), m);
                else
                    data.addAwaitingNotification(username, m);
            }
        }
    }

    private List<String> getTaggedUsers(String message) {
        List<String> taggedUsers = new LinkedList<>();
        int end = 0, begin;
        while (end < message.length()) {
            if (message.charAt(end) == '@') {
                begin = end + 1;
                while (end < message.length() && message.charAt(end) != ' ') end++;
                String name = message.substring(begin, end);
                if(!taggedUsers.contains(name))taggedUsers.add(name);
            }
            end++;
        }
        return taggedUsers;
    }
}
