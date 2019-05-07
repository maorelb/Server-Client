package bgu.spl.net.impl.BGSServer.Messages.ACK;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSData;
import bgu.spl.net.impl.BGSServer.Message;
import java.util.List;

public class ACKFollowMessage extends Message {
    private int messageOpcode;
    private List<String> addedOrRemovedUserNames;

    public ACKFollowMessage(int messageOpcode, List<String> addedOrRemovedSuccesfully){
        super(10);
        this.messageOpcode=messageOpcode;
        this.addedOrRemovedUserNames =addedOrRemovedSuccesfully;
    }

    public List<String> getAddedOrRemovedUserNames() {
        return addedOrRemovedUserNames;
    }

    public int getNumberOfUsers(){return addedOrRemovedUserNames.size();}
    @Override
    public void execute(BGSData data, Connections<Message> connections, int connectionId) {
        connections.send(connectionId,this);

    }
}
