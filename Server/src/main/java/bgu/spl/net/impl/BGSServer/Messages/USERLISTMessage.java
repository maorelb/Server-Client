package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSData;
import bgu.spl.net.impl.BGSServer.Message;
import bgu.spl.net.impl.BGSServer.Messages.ACK.ACKUserListMessage;

public class USERLISTMessage extends Message {

    public USERLISTMessage(){
        super(7);
    }

    @Override
    public void execute(BGSData data, Connections<Message> connections, int connectionId) {
        if(!data.isConnected(connectionId))
            new ErrorMessage(opcode).execute(data,connections,connectionId);
        else
            new ACKUserListMessage(data.getUserNamesList()).execute(data,connections,connectionId);
    }
}
