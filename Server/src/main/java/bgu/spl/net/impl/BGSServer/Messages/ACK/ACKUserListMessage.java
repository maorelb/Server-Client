package bgu.spl.net.impl.BGSServer.Messages.ACK;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSData;
import bgu.spl.net.impl.BGSServer.Message;

import java.util.List;

public class ACKUserListMessage extends Message {
    private List<String> userNameList;

    public ACKUserListMessage(List<String> userNameList){
        super(10);
        this.userNameList=userNameList;
    }

    public List<String> getUserNameList() {
        return userNameList;
    }

    @Override
    public void execute(BGSData data, Connections<Message> connections, int connectionId) {
        connections.send(connectionId,this);
    }
}
