package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSData;
import bgu.spl.net.impl.BGSServer.Message;

public class NOTIFICATIONMessage extends Message {
    private String content;
    private int notificationType;
    private String sendingUser;
    private String reciepient;
    public NOTIFICATIONMessage(String content,int notificationType,String sendingUser,String reciepient){
        super(9);
        this.content=content;
        this.notificationType=notificationType;
        this.sendingUser=sendingUser;
        this.reciepient=reciepient;
    }
    public int getNotificationType(){return notificationType;}
    public String getSendingUser(){return sendingUser;}
    public String getContent(){return content;}
    @Override
    public void execute(BGSData data, Connections<Message> connections, int connectionId) {
        connections.send(data.getConnectionId(reciepient),this);
    }
}
