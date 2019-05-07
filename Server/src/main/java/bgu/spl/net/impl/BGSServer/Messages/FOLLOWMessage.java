package bgu.spl.net.impl.BGSServer.Messages;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSData;
import bgu.spl.net.impl.BGSServer.Message;
import bgu.spl.net.impl.BGSServer.Messages.ACK.ACKFollowMessage;

import java.util.List;

public class FOLLOWMessage extends Message {
    private int followOpcode;
    private List<String> userNameList;

    public FOLLOWMessage(int followOpcode, List<String> userNameList){
        super(4);
        this.followOpcode=followOpcode;
        this.userNameList=userNameList;
            }

    public void execute(BGSData data, Connections<Message> connections, int connectionId) {
        //returns an error if:
        //1. client is not connected
        //2. follow/unfollow failed for all users on userNameList
        if(!data.isConnected(connectionId))
            new ErrorMessage(opcode).execute(data,connections,connectionId);
        else {
            for(String username: userNameList)
                if(!data.isRegistered(username))
                    userNameList.remove(username);
            List<String> addedOrRemovedUsers=data.getRegisteredUser(data.getUserName(connectionId)).
                    updateFollowList(followOpcode,userNameList);
            //follow command failed
            if(addedOrRemovedUsers.isEmpty())
                new ErrorMessage(opcode).execute(data,connections,connectionId);
            else
                new ACKFollowMessage(followOpcode, addedOrRemovedUsers)
                    .execute(data, connections, connectionId);
        }
    }
}
