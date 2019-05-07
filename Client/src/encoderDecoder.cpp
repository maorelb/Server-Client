
#include <encoderDecoder.h>
#include "encoderDecoder.h"

encoderDecoder::encoderDecoder(ConnectionHandler &CH):connectionHandler(CH),messageLength(0){}

const char* encoderDecoder::encode(std::string &line) {
    std::string command;
    unsigned long pos=line.find(' ');
    if(pos!= std::string::npos)
         command=line.substr(0,pos); //command is the first word of line
    else
        command=line;
    short opcode=getOpcode(command);
    switch(opcode){
        case 1:
            return encodeRegister(line.substr(pos+1));
        case 2:
            return encodeLogIn(line.substr(pos+1));
        case 3:
            return encodeLogOut(command);
        case 4:
            return encodeFollow(line.substr(pos+1));
        case 5:
            return encodePost(line.substr(pos+1));
        case 6:
            return encodePM(line.substr(pos+1));
        case 7:
            return encodeUserList(line.substr(pos+1));
        case 8:
           return encodeStat(line.substr(pos+1));
    }
    return nullptr; //invalid input
}

char *encoderDecoder::encodeRegister(std::string line) {
    messageLength = line.length() + 3;
    char* result=new char[messageLength];
    shortToBytes((short)1,result);
    encodeText(&result[2], line);
    return result;
}

char *encoderDecoder::encodeLogIn(std::string line) {
    messageLength = line.length() + 3;
    char* result=new char[messageLength];
    shortToBytes((short)2,result);
    encodeText(&result[2], line);
    return result;
}

char *encoderDecoder::encodeLogOut(std::string line) {
    messageLength = 2;
    char* result=new char[messageLength];
    shortToBytes((short) 3,result);
    return result;
}

char *encoderDecoder::encodeFollow(std::string line) {
    short users;
    char follow = (getToken(line))[0];
    users = (short) stoi(getToken(line));
    messageLength = line.length() + 6;
    char* result=new char[messageLength];
    shortToBytes(4,result);
    if(follow=='0')
        result[2] = 0;
    else
        result[2]=1;
    char temp[2];
    shortToBytes(users,temp);
    result[3]=temp[0];result[4]=temp[1];
    encodeText(&result[5], line);
    return result;
}

char *encoderDecoder::encodePost(std::string line) {
    messageLength = line.length() + 3;
    char* result=new char[messageLength];
    shortToBytes((short)5,result);
    for (unsigned int i=0;i<line.length();i++){
        result[i+2] = line[i];
    }
    result[messageLength-1] = '\0';
    return result;
}

char *encoderDecoder::encodePM(std::string line) {
    messageLength = line.length() + 3;
    char* result=new char[messageLength];
    shortToBytes((short)6,result);
    bool value= false;
    for (unsigned int i=0;i<line.length();i++){
        if ((line[i] == ' ') & !value){
            result[i+2] = '\0';
            value = true;
        }
        else
            result[i+2] = line[i];
    }
    result[messageLength-1] = '\0';
    return result;
}

char *encoderDecoder::encodeUserList(std::string line) {
    messageLength = 2;
    char* result=new char[messageLength];
    shortToBytes((short) 7,result);
    return result;
}

char *encoderDecoder::encodeStat(std::string line) {
    messageLength = line.length()+3;
    char* result=new char[messageLength];
    shortToBytes((short) 8,result);
    for (unsigned int i=0;i<line.length();i++){
        result[i+2] = line[i];
    }
    result[messageLength-1] = '\0';
    return result;
}

short encoderDecoder::getOpcode(std::string command) {
    if(command=="REGISTER")
        return 1;
    else if(command=="LOGIN")
        return 2;
    else if(command=="LOGOUT")
        return 3;
    else if(command=="FOLLOW")
        return 4;
    else if(command=="POST")
        return 5;
    else if(command=="PM")
        return 6;
    else if(command=="USERLIST")
        return 7;
    else if(command=="STAT")
        return 8;
    else return -1;//undefined command
    }

short encoderDecoder::bytesToShort(char *bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;

}

unsigned int encoderDecoder::getMessageLength() {
    return messageLength;
}

void encoderDecoder::shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

void encoderDecoder::encodeText(char *bytes, std::string line) {
    for (unsigned int i=0;i<line.length();i++){
        if (line[i] == *(" "))
            *bytes = '\0';
        else
            *bytes = line[i];
        bytes++;
    }
    *bytes = '\0';
}
std::string encoderDecoder::getToken(std::string &line) {
    std::string token;
    token = line.substr(0,line.find(" "));
    line = line.substr(line.find(" ")+1);
    return token;
}

std::string encoderDecoder::decode(short op) {
    switch(op){
        case 9:
            return decodeNotification();
        case 10:
            return decodeACK();
        case 11:
            return decodeError();
    }
    return nullptr;
}
std::string encoderDecoder::decodeNotification() {
    std::string ans= "NOTIFICATION";
    char notificationType[1];
    connectionHandler.getBytes(notificationType,1);
    if (notificationType[0]==0)
        ans.append(" PM ");
    else ans.append(" Public ");
    std::string postingUser,content;
    connectionHandler.getFrameAscii(postingUser,'\0');
    ans.append(postingUser+" ");
    connectionHandler.getFrameAscii(content,'\0');
    ans.append(content);
    return ans;
}

std::string encoderDecoder::decodeACK() {
    char messageOpcode[2];
    connectionHandler.getBytes(messageOpcode,2);
    short messageOp = bytesToShort(messageOpcode);
    std::string ret;
    switch(messageOp) {
        case 1:case 2: case 3: case 5: case 6:{
            std::string temp = "ACK";
            return temp + " " + std::to_string(messageOp);}
        case 4:
            return ACKFollow();
        case 7:
            return ACKUserList();
        case 8:
            return ACKStat();
    }
    return nullptr;
}

std::string encoderDecoder::ACKFollow() {
    std:: string ans="ACK 4 ";
    char users[2];
    connectionHandler.getBytes(users,2);
    ans.append(std::to_string(bytesToShort(users)));
    for (int i=0; i<bytesToShort(users);i++){
        std::string userName;
        connectionHandler.getFrameAscii(userName,'\0');
        ans.append(" "+userName);
    }
    return ans;
}

std::string encoderDecoder::ACKUserList() {
    std::string ans="ACK 7 ";
    char users[2];
    connectionHandler.getBytes(users,2);
    ans.append(std::to_string(bytesToShort(users)));
    for (int i=0; i<bytesToShort(users);i++){
        std::string userName;
        connectionHandler.getFrameAscii(userName,'\0');
        ans.append(" "+userName);
    }
    return ans;
}

std::string encoderDecoder::ACKStat() {
    std::string ans="ACK 8 ";
    char posts[2];
    connectionHandler.getBytes(posts,2);
    ans.append(std::to_string(bytesToShort(posts))+ " ");
    char followers[2];
    connectionHandler.getBytes(followers,2);
    ans.append(std::to_string(bytesToShort(followers))+ " ");
    char following[2];
    connectionHandler.getBytes(following,2);
    ans.append(std::to_string(bytesToShort(following))+ " ");
    return ans;
}

std::string encoderDecoder::decodeError() {
    char messageOpcode[2];
    connectionHandler.getBytes(messageOpcode,2);
    std::string temp = "ERROR";
    return temp + " " + std::to_string(bytesToShort(messageOpcode));
}



