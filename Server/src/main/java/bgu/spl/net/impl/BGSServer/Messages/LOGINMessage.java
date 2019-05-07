package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSData;
import bgu.spl.net.impl.BGSServer.Message;
import bgu.spl.net.impl.BGSServer.User;

import java.util.List;


public class LOGINMessage extends Message {

    private final String username;
    private final String password;
    private Object lock;

    public LOGINMessage(String username,String password){
        super(2);
        this.username=username;
         this.password=password;
    lock=new Object();}

    @Override
    public void execute(BGSData data, Connections<Message> connections, int connectionId) {
            User user = data.getRegisteredUser(username);
            //returns an error if one of the following occures:
            // 1. username does not exist
            // 2. password is not correct
            // 3. client has already succesfuly logged in
            if (user == null || !user.getPassword().equals(password) || data.isConnected(connectionId))
                new ErrorMessage(opcode).execute(data, connections, connectionId);
            else {
                if(data.addOnlneUser(username, connectionId)) {
                    new ACKMessage(opcode).execute(data, connections, connectionId);
                    //after use has logged in, he can now receive notifications.
                    List<NOTIFICATIONMessage> notifications = data.getNotifications(username);
                    if (notifications != null) {
                        for (NOTIFICATIONMessage message : notifications) {
                            notifications.remove(message);
                            message.execute(data, connections, connectionId);
                        }
                    }
                }
                else
                    new ErrorMessage(opcode).execute(data, connections, connectionId);
           }
    }
  }

