package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSData;
import bgu.spl.net.impl.BGSServer.Message;

public class LOGOUTMessage extends Message {
    private boolean shouldLogout=false;
    public LOGOUTMessage(){
        super(3);
    }
    public void execute(BGSData data, Connections<Message> connections, int connectionId) {
        //send en error message if client is not logged in
            if (!data.isConnected(connectionId))
            new ErrorMessage(opcode).execute(data, connections, connectionId);
       // client is logged in and should be disconnected
       else{
           shouldLogout=true;
            data.disconnectUser(connectionId);
        new ACKMessage(opcode).execute(data,connections,connectionId);
        }
    }
    public boolean isShouldLogout(){return shouldLogout;}
}


