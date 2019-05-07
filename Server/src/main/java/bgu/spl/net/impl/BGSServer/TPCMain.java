package bgu.spl.net.impl.BGSServer;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        BGSData data = new BGSData();
        Server.threadPerClient(
                Integer.parseInt(args[0]), //port
                () -> new BGSProtocol(data),
                BGSEncDec::new
        ).serve();
    }
}

