package bgu.spl.net.impl.BGSServer.Messages;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSData;
import bgu.spl.net.impl.BGSServer.Message;

public class RegisterMessage extends Message {
    private String username;
    private String password;


    public RegisterMessage(String username, String password) {
        super(1);
        this.username = username;
        this.password = password;
    }

    public void execute(BGSData data, Connections<Message> connections, int connectionId) {
        if (data.addUser(username, password))
            new ACKMessage(opcode).execute(data, connections, connectionId);
        else
            new ErrorMessage(opcode).execute(data, connections, connectionId);
    }
}


