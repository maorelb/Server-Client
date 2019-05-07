#ifndef ENCODERDECODER_H
#define ENCODERDECODER_H
#include "connectionHandler.h"

class encoderDecoder {
private:
public:
    encoderDecoder(ConnectionHandler& CH);
    const char* encode(std::string& line); //encode a string to array of bytes.
    unsigned int getMessageLength();
    std::string decode(short op);
    short bytesToShort(char* bytesArr);

private:
    ConnectionHandler& connectionHandler;
    unsigned int messageLength;
    short getOpcode(std::string command);
    char* encodeRegister(std::string line);
    char* encodeLogIn(std::string line);
    char* encodeLogOut(std::string line);
    char* encodeFollow(std::string line);
    char* encodePost(std::string line);
    char* encodePM(std::string line);
    char* encodeUserList(std::string line);
    char* encodeStat(std::string line);
    void encodeText(char *bytes, std::string line);
    std::string getToken(std::string &line);
    void shortToBytes(short num, char* bytesArr);
    std::string decodeNotification();
    std:: string decodeACK();
    std::string decodeError();
    std::string ACKFollow();
    std::string ACKUserList();
    std:: string ACKStat();
};

#endif
