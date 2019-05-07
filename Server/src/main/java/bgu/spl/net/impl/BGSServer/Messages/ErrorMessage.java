package bgu.spl.net.impl.BGSServer.Messages;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSData;
import bgu.spl.net.impl.BGSServer.Message;

public class ErrorMessage extends Message {
    private int messageOpcode;
    public ErrorMessage(int messageOpcodeopcode){
        super(11);
        this.messageOpcode=messageOpcodeopcode;
    }

    public int getMessageOpcode() {
        return messageOpcode;
    }

    public void execute(BGSData data, Connections<Message> connections, int connectionId) {
        //send en error msg to the client via connections.
        connections.send(connectionId,this);
    }
}
