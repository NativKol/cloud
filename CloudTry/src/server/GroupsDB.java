package server;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;

import java.util.ArrayList;
import java.util.List;

public interface GroupsDB {

    /*
   Static function that get the object of Group and convert it to the type of basicDBObject
   soo it can be inserted into the database
    */
    public static BasicDBObject convert(GroupModel grp)
    {
        BasicDBObject basicDBObject = new BasicDBObject();
        List<BasicDBObject> list1 = new ArrayList<BasicDBObject>();
        List<BasicDBObject> list2 = new ArrayList<BasicDBObject>();

        if (grp != null)
        {
            for (String par: grp.participants)
            {
                BasicDBObject tmp = new BasicDBObject();
                tmp.append("email", par);
                list1.add(tmp);
            }
            for (String par: grp.invitedParticipants)
            {
                BasicDBObject tmp = new BasicDBObject();
                tmp.append("email", par);
                list2.add(tmp);
            }
        }

        basicDBObject.append("_id", grp._id).append("groupName", grp.groupName)
                .append("participants", list1).append("invitedParticipants", grp.invitedParticipants);
        return basicDBObject;
    }
    /*
    Static function that get the details of the group with the full json string of the data
    about the file and fill the others fields of the file that couldn't be filled because
    they are arrays
    Returned the full Group Object
     */
    public static GroupModel jsonFillGroup(GroupModelDetail group, String json)
    {
        //todo: check if working get groups
        String jsonPath1 = "", jsonPath2 = "";
        GroupModel groupFull = new GroupModel();
        Gson gson = new Gson();

        groupFull._id = group._id;
        groupFull.groupName = group.groupName;

        jsonPath1 = json.substring(json.indexOf("\"participants"), json.indexOf(", \"invitedParticipants"));
        if (jsonPath1.contains("{"))
        {
            jsonPath2 = jsonPath1.substring(jsonPath1.indexOf("{"), jsonPath1.indexOf("}") + 1);
            String tmpEmail = jsonPath2.substring(jsonPath2.indexOf(":") + 3, jsonPath2.indexOf("\"}"));
            jsonPath1 = jsonPath1.substring(jsonPath1.indexOf("}") + 2, jsonPath1.length());
            groupFull.participants.add(tmpEmail);
        }
        else
        {
            jsonPath1 = jsonPath1.substring(jsonPath1.indexOf("[") + 1, jsonPath1.length());
            while (jsonPath1.contains("]"))
            {
                jsonPath2 = jsonPath1.substring(jsonPath1.indexOf("\"") + 1, jsonPath1.indexOf("]") + 1);
                String tmpEmail = "";
                if (jsonPath1.contains(","))
                {
                    tmpEmail = jsonPath2.substring(0, jsonPath2.indexOf(",") - 1);
                    jsonPath1 = jsonPath1.substring(jsonPath1.indexOf(",") + 1, jsonPath1.length());
                    groupFull.participants.add(tmpEmail);
                }
                else
                {
                    tmpEmail = jsonPath2.substring(0, jsonPath2.indexOf("]") - 1);
                    groupFull.participants.add(tmpEmail);
                    break;
                }
            }
        }

        jsonPath1 = json.substring(json.indexOf("\"invitedParticipants"), json.length());
        jsonPath1 = jsonPath1.substring(jsonPath1.indexOf("[") + 1, jsonPath1.length());
        while (jsonPath1.contains("]"))
        {
            jsonPath2 = jsonPath1.substring(jsonPath1.indexOf("\"") + 1, jsonPath1.indexOf("]") + 1);
            String tmpEmail = "";
            if (jsonPath1.contains(","))
            {
                tmpEmail = jsonPath2.substring(0, jsonPath2.indexOf(",") - 1);
                jsonPath1 = jsonPath1.substring(jsonPath1.indexOf(",") + 1, jsonPath1.length());
                groupFull.invitedParticipants.add(tmpEmail);
            }
            else
            {
                if (jsonPath2.equals("]"))
                    return groupFull;
                tmpEmail = jsonPath2.substring(0, jsonPath2.indexOf("]") - 1);
                groupFull.invitedParticipants.add(tmpEmail);
                return groupFull;
            }
        }

        return groupFull;
    }

    /*
    Function that takes as parameter the group object and insert it into the database
     */
    public void insertGroup(GroupModel grp);
    /*
    Query that adding new email of user to the invited participants of group
     */
    public void inviteToGroup(GroupModel grp, String email);
    /*
    Query that remove user from the group by email
     */
    public void kickFromGroup(GroupModel grp, String email);
    /*
    Query that adding user by his email to the group list from the invited list
     */
    public void acceptGroup(GroupModel grp, String email);  //from invited to participants
    /*
    Query that removing user by his email from the invited list of the group
     */
    public void rejectGroup(GroupModel grp, String email);  //delete invite
    /*
    Query that remove user from the participants of the group (not working on owner)
     */
    public void leaveGroup(GroupModel grp, String email);   //don't work on owner
    //public void deleteGroup(String id);   //work on owner only
    /*
    Query that returns the groups of the user
     */
    public List<GroupModel> getGroupsByUser(String email);
    /*
    Query that returns the groups the user appears in their invited list
     */
    public List<GroupModel> getGroupsByUserInvited(String email);
    /*
    Query that returns the groups the user is the owner of them
     */
    public List<GroupModel> getGroupsByOwner(String email);
    /*
    Query that returned group by the id
     */
    public GroupModel getGroupByID(String id);
    /*
    Query that change the group name
     */
    public void ChangeGroupName(GroupModel grp, String newName);
    /*
    Function that prints the collection of groups
     */
    public void printGroupsDB();
}
