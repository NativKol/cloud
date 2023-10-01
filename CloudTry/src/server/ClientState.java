package server;

public class ClientState extends UserModel{
    private boolean is_lobby;
    private boolean is_file;
    private boolean is_group;
    private UserModel user;
    private FileModel file;
    private GroupModel group;
    public ClientState(){
        is_lobby = false;
        is_file = false;
        is_group = false;
        user = null;
        file = null;
        group = null;
    }

    boolean getIs_lobby(){
        return is_lobby;
    }
    boolean getIs_file(){
        return is_file;
    }
    boolean getIs_group(){
        return is_group;
    }

    UserModel getUserState()
    {
        return user;
    }
    FileModel getFileState()
    {
        return file;
    }

    GroupModel getGroupState()
    {
        return group;
    }

    void setIs_lobby(boolean lobby)
    {
        is_lobby = lobby;
    }

    void setIs_file(boolean lobby)
    {
        is_file = lobby;
    }

    void setIs_group(boolean lobby)
    {
        is_group = lobby;
    }

    void setUserState(UserModel usr)
    {
        user = usr;
    }
    void setFileState(FileModel f)
    {
        file = f;
    }
    void setGroupState(GroupModel grp)
    {
        group = grp;
    }

}
