package server;

public interface RequestHandleGroup
{
    public String handleGroupRequest(UserModel usr, GroupModel g);
    public String handlePreGroupRequest();
}
