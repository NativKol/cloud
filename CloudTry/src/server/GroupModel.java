package server;

import java.util.ArrayList;
import java.util.*;
public class GroupModel {
    public String _id = "";   //unique
    public String groupName = "";
    public List<String> participants = new ArrayList<>();       //emails
    public List<String> invitedParticipants = new ArrayList<>();       //emails
}
