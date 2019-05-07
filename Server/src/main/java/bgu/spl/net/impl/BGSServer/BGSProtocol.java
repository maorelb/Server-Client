package bgu.spl.net.impl.BGSServer;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.Messages.LOGOUTMessage;

public class BGSProtocol implements BidiMessagingProtocol<Message>{
    private Connections<Message> connections;
    private int connectionId;
    private boolean shouldTerminate;
    private BGSData data;
    public BGSProtocol(BGSData data){
        this.data=data;}

    public void start(int connectionId, Connections<Message> connections) {
         this.connectionId=connectionId;
         this.connections=connections;
         shouldTerminate=false;
    }

    public void process(Message message) {
        if(message instanceof LOGOUTMessage && ((LOGOUTMessage) message).isShouldLogout())
            shouldTerminate=true;
        message.execute(data,connections,connectionId);
    }

    public boolean shouldTerminate() {
        return shouldTerminate;
    }

}