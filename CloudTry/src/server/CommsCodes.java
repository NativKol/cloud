package server;

public enum CommsCodes
{
    REGISTER(101), LOG(102), RENAME(103), STORAGE(104), USER_PASSWORD(105), USER_DELETE(106), SIGNOUT(107),
    GROUP_CREATE(201), INVITE_GROUP(202), KICK_GROUP(203), GROUP_NAME(204), GROUP_DELETE(205),
    GROUP_LEAVE(206), GROUP_ACCEPT(207), GROUP_REJECT(208), GROUP_GET_OWNER(209), GROUP_GET(210), GROUP_GET_INVITED(211), GROUP_GET_ID(212),
    FILE_ADD(301), FILE_STAR(302), FILE_NAME(303), FILE_SHARE(304), FILE_SHARE_GROUP(305),
    FILE_GET_OWNER(306), FILE_GET(307), FILES_GET(308), FILES_DELETE(309), FILE_DOWNLOAD(310),
    SERVER_ADD(401), SERVER_GET(402);

    private final int value;
    CommsCodes(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }

    public  CommsCodes findByAbbr(int abbr){
        for(CommsCodes c : values()){
            if( c.value ==abbr ){
                return c;
            }
        }
        return null;
    }

    /*
    Checks the type of request to separate handlers
     */

    public boolean isUser()
    {
        if ( value > 100 && value < 108 )
            return true;
        return false;
    }

    public boolean isGroup()
    {
        if ( value > 200 && value < 213 )
            return true;
        return false;
    }

    public boolean isFile()
    {
        if ( value > 300 && value < 309 )
            return true;
        return false;
    }

    public boolean isServer()
    {
        if ( value > 400 && value < 403 )
            return true;
        return false;
    }
}
