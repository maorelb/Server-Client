
#include <Sender.h>

#include "Sender.h"

Sender::Sender(ConnectionHandler &CH, encoderDecoder &encd ,bool& value) :
        connectionHandledr(CH),encodeDecoder(encd),verifiedLogout(value),stop(false){}

void Sender::run() {
std::string line;
    while (!verifiedLogout){
        std::string line;
        if(!stop) {
            std::getline(std::cin, line);
            if (line == "LOGOUT")
                stop = true;
            const char *bytes = encodeDecoder.encode(line);
            unsigned int length = encodeDecoder.getMessageLength();
            connectionHandledr.sendBytes(bytes, length);
        }
        }
}

void Sender::setStop(bool value) { stop=value;}




