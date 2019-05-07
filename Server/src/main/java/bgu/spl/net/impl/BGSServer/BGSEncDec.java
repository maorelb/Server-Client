package bgu.spl.net.impl.BGSServer;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.BGSServer.Messages.*;
import bgu.spl.net.impl.BGSServer.Messages.ACK.ACKFollowMessage;
import bgu.spl.net.impl.BGSServer.Messages.ACK.ACKStatMessage;
import bgu.spl.net.impl.BGSServer.Messages.ACK.ACKUserListMessage;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BGSEncDec implements MessageEncoderDecoder<Message>{
    private byte[] bytes=new byte[1<<10];
    private int len=0;
    private short opcode;
    private int zerosToRead=-1;
    private int zerosRead=0;

    @Override
    public Message decodeNextByte(byte nextByte) {
        pushByte(nextByte);
        if(len==2)
            setOpAndZerosToRead(Arrays.copyOfRange(bytes, 0, 2));

         if(opcode==4 && len==5) { //numOfUsers
            zerosToRead =zerosRead+ bytesToShort(Arrays.copyOfRange(bytes, 3, 5));
        }
        if(nextByte=='\0')
            zerosRead++;

        if(zerosRead==zerosToRead) {
            return messageCreator();
        }
       return null; //not a command yet
    }
    public void setOpAndZerosToRead(byte[] opBytes){
        opcode = bytesToShort(opBytes);
        switch(opcode){
            case 1: case 2: case 6: // REGISTER OR LOGIN OR PM
                zerosToRead=3;
                break;
            case 3: case 7: // LOGOUT OR USERLIST
            zerosToRead=1;
                break;
            case 5: case 8: //POST OR STAT
                zerosToRead=2;
                break;
        }
   }

    public void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private Message messageCreator() {
        Message result=null; //message to be returned
        byte[] bytesRemained = Arrays.copyOfRange(bytes, 2, len);
        switch (opcode) {
            case 1:
                String[] temp1=getStringsTillZeroByte(bytesRemained);
                result=new RegisterMessage(temp1[0],temp1[1]);
                break;
            case 2:
                String[] temp2=getStringsTillZeroByte(bytesRemained);
                result = new LOGINMessage(temp2[0], temp2[1]);//temp2[0]=username, temp2[1]=password
                break;
            case 3:
                result = new LOGOUTMessage();
                break;
            case 4:
                int followOpcode = bytesRemained[0];
                result = new FOLLOWMessage(followOpcode, getUserNames(bytesRemained));
                break;
            case 5:
                result = new POSTMessage(new String(bytesRemained,
                        0, bytesRemained.length-1, StandardCharsets.UTF_8));
                break;
            case 6:
                String[] temp3=getStringsTillZeroByte(bytesRemained);
                result=new PMMessage(temp3[0],temp3[1]);//temp3[0]=reciepient, temp3[1]=content
                break;
            case 7:
                result=new USERLISTMessage();
                break;
            case 8:
                result=new STATMessage(new String(bytesRemained,0,bytesRemained.length-1,StandardCharsets.UTF_8));
                break;
        }
        len=0;
        zerosRead=0;
        zerosToRead=-1;
        return result; // illegal input
    }

    private String[] getStringsTillZeroByte(byte[] bytesRemained){
        String[] result=new String[2];
        int i = 0;
        while (bytesRemained[i] != '\0')
            i++;
        result[0] = new String(bytesRemained, 0, i, StandardCharsets.UTF_8);
        result[1] = new String(bytesRemained, i + 1, bytesRemained.length - i - 2, StandardCharsets.UTF_8);
        return result;
    }

    private List<String> getUserNames(byte[]bytesRemained) {
        List<String>list=new LinkedList<>();
        int begin = 3, end = 3;
        while (end < bytesRemained.length) {
            if (bytesRemained[end] == '\0') {
                list.add(new String(bytesRemained, begin, end - begin, StandardCharsets.UTF_8));
                begin = end + 1;
            }
            end++;
        }
        return list;
    }

    private short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    private byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
    @Override
    public byte[] encode(Message message) {
        if (message instanceof ErrorMessage)
            return concatenate(shortToBytes((short) message.opcode),
                    shortToBytes((short) ((ErrorMessage) message).getMessageOpcode()));

        else if (message instanceof ACKMessage)
            return concatenate(shortToBytes((short) message.opcode),
                    shortToBytes((short) ((ACKMessage) message).getMessageOpcode()));

        else if (message instanceof ACKFollowMessage) {
            ACKFollowMessage m = ((ACKFollowMessage) message);
            byte[] temp = concatenate((concatenate(shortToBytes((short) m.opcode),
                    shortToBytes((short) 4))),
                    shortToBytes((short) m.getNumberOfUsers()));
            return concatenate(temp, listToString(m.getAddedOrRemovedUserNames()).getBytes());

        } else if (message instanceof NOTIFICATIONMessage) {
            NOTIFICATIONMessage m = ((NOTIFICATIONMessage) message);
            byte[] type={(byte)m.getNotificationType()};
            byte temp1[]=concatenate(shortToBytes((short) m.opcode),type);
            byte[] temp2 = concatenate((m.getSendingUser() + '\0').getBytes(),
                    (m.getContent() + '\0').getBytes());
            return concatenate(temp1, temp2);

        } else if (message instanceof ACKUserListMessage) {
            ACKUserListMessage m = ((ACKUserListMessage) message);
            byte[] temp1 = concatenate(shortToBytes((short) message.opcode),
                    shortToBytes((short) 7));
            byte[] temp2 = concatenate((shortToBytes((short) m.getUserNameList().size())),
                    listToString(m.getUserNameList()).getBytes());
            return concatenate(temp1, temp2);
        }
        else if(message instanceof ACKStatMessage){
            ACKStatMessage m=((ACKStatMessage)message);
            byte[] temp1 = concatenate(shortToBytes((short) message.opcode),
                    shortToBytes((short) 8));
            byte[] temp2 = concatenate((shortToBytes((short) m.getPosts())),
                    shortToBytes((short)m.getFollowers()));
            return concatenate(concatenate(temp1,temp2),shortToBytes((short)m.getFollowing()));
        }
        return null;
    }
    //convert a list to a string of words seperated with /0
    private String listToString(List<String> list){
            String userNames=new String();
            for(String name: list)
                userNames=userNames+name+'\0';
            return userNames;

        }

        //concatenate 2 bytes array into one
    private byte[] concatenate(byte[] a,byte[] b){
        byte[] result=new byte[a.length+b.length];
        System.arraycopy(a,0,result,0,a.length);
        System.arraycopy(b,0,result,a.length,b.length);
        return result;
    }
}

