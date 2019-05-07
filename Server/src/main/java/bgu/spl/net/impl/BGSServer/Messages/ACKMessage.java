package bgu.spl.net.impl.BGSServer.Messages;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSData;
import bgu.spl.net.impl.BGSServer.Message;

public class ACKMessage extends Message {
    private int messageOpcode;

    public ACKMessage(int messageOpcode) {
        super(10);
        this.messageOpcode = messageOpcode;
    }

    public int getMessageOpcode() {
        return messageOpcode;
    }

    public void execute(BGSData data, Connections<Message> connections, int connectionId) {
        //sends an ACk message to client via connections.
        connections.send(connectionId, this);
    }
}




