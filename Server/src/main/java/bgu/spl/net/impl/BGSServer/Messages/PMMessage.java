package bgu.spl.net.impl.BGSServer.Messages;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSData;
import bgu.spl.net.impl.BGSServer.Message;

public class PMMessage extends Message {
    private final String reciepient;
    private final String content;

    public PMMessage(String reciepient,String content){
        super(6);
        this.reciepient=reciepient;
        this.content=content;
    }

    public void execute(BGSData data, Connections<Message> connections, int connectionId) {
        if(!data.isConnected(connectionId) || !data.isRegistered(reciepient))
            new ErrorMessage(opcode).execute(data,connections,connectionId);
        else{
            new ACKMessage(opcode).execute(data, connections, connectionId);
            String sender=data.getUserName(connectionId);
            NOTIFICATIONMessage m=new NOTIFICATIONMessage(content,0,sender,reciepient);
            if(data.isConnected(reciepient))
                connections.send(data.getConnectionId(reciepient),m);
            else
                data.addAwaitingNotification(reciepient,m);
        }
    }
}
