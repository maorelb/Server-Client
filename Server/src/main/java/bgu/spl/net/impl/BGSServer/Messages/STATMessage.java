package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSData;
import bgu.spl.net.impl.BGSServer.Message;
import bgu.spl.net.impl.BGSServer.Messages.ACK.ACKStatMessage;
import bgu.spl.net.impl.BGSServer.User;

public class STATMessage extends Message {
    private String username;

    public STATMessage(String username) {
        super(8);
        this.username = username;
    }

    @Override
    public void execute(BGSData data, Connections<Message> connections, int connectionId) {
        if (!data.isConnected(connectionId))
            new ErrorMessage(opcode).execute(data, connections, connectionId);
        else{
            User user=data.getRegisteredUser(username);
            new ACKStatMessage(username,user.getPosts(),data.getFollowers(username).size(),
                    user.following()).execute(data,connections,connectionId);
        }

    }
}
