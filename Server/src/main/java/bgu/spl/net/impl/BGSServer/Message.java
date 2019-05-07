package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.Connections;

public abstract class Message {
    protected int opcode;

    public Message(int opcode){this.opcode=opcode;}

public abstract void execute(BGSData data, Connections<Message> connections, int connectionId);

}
