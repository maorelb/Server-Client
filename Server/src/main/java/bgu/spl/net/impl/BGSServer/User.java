package bgu.spl.net.impl.BGSServer;
import java.util.LinkedList;
import java.util.List;

public class User {
private final String username;
private final String password;
private List<String> followList;
private int posts;

public User(String username,String password){
    this.username=username;
    this.password=password;
    followList=new LinkedList<>();
    posts=0;
}
    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }
    public void increaseNumOfPosts(){posts++;}
    public int getPosts() { return posts;}
    public int following(){return followList.size();}


    //this method returns a list of succesfully added or removed usernames from or to followList
        //if no changes perfomed, returns an empty list.
    public List<String> updateFollowList(int followOpcode,List<String> userNameList){
    List<String> list=new LinkedList<>();
        if(followOpcode==0) { //follow
            for(String username: userNameList)
                if(!followList.contains(username)) {
                    followList.add(username);
                    list.add(username);
                }
        }
        else{ //unfollow
            for(String username:userNameList)
                if(followList.contains(username)){
                    followList.remove(username);
                    list.add(username);
                }
        }
        return list;
    }
    public boolean isFollowing(String username){return followList.contains(username);}
  }
