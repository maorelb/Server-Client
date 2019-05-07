
#ifndef CLIENT_SENDER_H
#define CLIENT_SENDER_H
#include <thread>
#include "connectionHandler.h"
#include "encoderDecoder.h"
class Sender {
private:
    ConnectionHandler& connectionHandledr;
    encoderDecoder& encodeDecoder;
    bool& verifiedLogout;
    bool stop;
public:
    Sender(ConnectionHandler& CH, encoderDecoder& encd,bool& value);
    void run();
    void setStop(bool value);
    };

#endif //CLIENT_SENDER_H
