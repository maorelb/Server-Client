package bgu.spl.net.impl.BGSServer.Messages.ACK;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSData;
import bgu.spl.net.impl.BGSServer.Message;

public class ACKStatMessage extends Message {
private String user;
private int posts;
private int followers;
private int following;
public ACKStatMessage(String user,int posts,int followers,int following)
{
    super(10);
    this.user=user;
    this.posts=posts;
    this.followers=followers;
    this.following=following;
}

    public int getPosts() {
        return posts;
    }

    public int getFollowers() {
        return followers;
    }

    public int getFollowing() {
        return following;
    }

    @Override
    public void execute(BGSData data, Connections<Message> connections, int connectionId) {
        connections.send(connectionId,this);
    }
}
