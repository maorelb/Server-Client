package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        BGSData data = new BGSData();
        Server.reactor(
                Integer.parseInt(args[1]),//n.o threads
                Integer.parseInt(args[0]), //port
                () -> new BGSProtocol(data),
                BGSEncDec::new
        ).serve();
    }
}
