#include <stdlib.h>
#include <connectionHandler.h>
#include <thread>
#include "encoderDecoder.h"
#include "Sender.h"
/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    encoderDecoder encoderDecoder(connectionHandler);
    bool* verifiedLogout=new bool(false); // shared flag, indicates whether threads should terminate
    Sender sender(connectionHandler,encoderDecoder,*verifiedLogout);
    std::thread t1(&Sender::run, &sender); //sending messages to server

    //read messages from socket
    while (!*verifiedLogout) {
        std::string answer;
        char opCode[2];
        connectionHandler.getBytes(opCode, 2);
        short op = encoderDecoder.bytesToShort(opCode);
        answer=encoderDecoder.decode(op);
        std::cout<<answer<<std::endl;
        if(answer=="ERROR 3")
            sender.setStop(false);
        else if (answer == "ACK 3") {
            *verifiedLogout = true;
            t1.join();
        }
    }
    return 0;
}

