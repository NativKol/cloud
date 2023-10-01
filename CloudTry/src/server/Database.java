package server;

import com.google.gson.Gson;
import com.mongodb.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database implements FilesDB, UsersDB, GroupsDB, ServersDB {

    private DB db1;

    Database() {
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);

        MongoClient mongo1 = new MongoClient("localhost", 27017);
        DB db = mongo1.getDB("cloudMain");
        this.db1 = db;
    }

    @Override
    public void deleteDB() {
        this.db1.dropDatabase();
    }

    @Override
    public void insertFile(FileModel file) {
        DBCollection coll = this.db1.createCollection("files", null);
        BasicDBObject tmp = new BasicDBObject();
        tmp = FilesDB.convert(file);
        //check if file in this name already exist in this user

        coll.insert(tmp);
    }

    @Override
    public void MarkStar(FileModel file) {
        BasicDBObject query = new BasicDBObject();
        if (file.star.equals("false")) {
            query.put("_id", file._id); // (1)

            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("star", "true"); // (2)

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument); // (3)

            this.db1.getCollection("files").update(query, updateObject); // (4)
        } else {
            query.put("_id", file._id); // (1)

            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("star", "false"); // (2)

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument); // (3)

            this.db1.getCollection("files").update(query, updateObject); // (4)
        }
    }

    @Override
    public void ChangeFileName(FileModel file, String newName) {
        BasicDBObject query = new BasicDBObject();
        if (!file.file_name.equals(newName)) {
            query.put("_id", file._id); // (1)

            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("file_name", newName); // (2)

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument); // (3)

            this.db1.getCollection("files").update(query, updateObject); // (4)
        }
    }

    @Override
    public void ShareFile(FileModel file, String newEmail) {
        BasicDBObject query = new BasicDBObject();

        query.put("_id", file._id); // (1)

        file.sharedUsers.add(newEmail);

        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("sharedUsers", file.sharedUsers); // (2)

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument); // (3)

        this.db1.getCollection("files").update(query, updateObject); // (4)

    }
    @Override
    public void ShareFileToGroup(FileModel file, String groupID)
    {
        BasicDBObject query = new BasicDBObject();


        query.put("_id", file._id); // (1)

        file.sharedGroups.add(groupID);

        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("sharedGroups", file.sharedGroups); // (2)

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument); // (3)

        this.db1.getCollection("files").update(query, updateObject); // (4)
    }

    @Override
    public List<FileModelDetail> getFilesByUser(String email) {
        List<GroupModel> groups = getGroupsByUser(email);

        List<FileModelDetail> list = new ArrayList<>();
        String json = "", jsonPath1 = "", jsonPath2 = "";
        Gson gson = new Gson();
        DBCollection coll = this.db1.getCollection("files");
        DBCursor cur1 = coll.find();
        while (cur1.hasNext()) {
            json = cur1.next().toString();
            //server.FileModel file = gson.fromJson(json, server.FileModel.class);      //to fix - getFile

            FileModelDetail file = gson.fromJson(json, FileModelDetail.class);      //to fix - getFile
            FileModel fileFull = FilesDB.jsonFillFile(file, json);

            if (fileFull.sharedUsers.contains(email))
            {
                list.add(file);
            }
            else
            {
                if (groups != null)
                {
                    for (int i = 0; i < groups.size(); i++)
                    {
                        if (fileFull.sharedGroups.contains(groups.get(i)._id))
                        {
                            list.add(file);
                            i =  groups.size(); //goto end of loop so it won't add twice..
                        }
                    }
                }
            }
        }
        if (list.isEmpty())
            return null;
        return list;
    }

    @Override
    public List<FileModelDetail> getFilesByOwner(String email) {
        List<FileModelDetail> list = new ArrayList<>();
        String json = "", jsonPath1 = "", jsonPath2 = "";
        Gson gson = new Gson();
        DBCollection coll = this.db1.getCollection("files");
        DBCursor cur1 = coll.find();
        while (cur1.hasNext()) {
            json = cur1.next().toString();
            //server.FileModel file = gson.fromJson(json, server.FileModel.class);      //to fix - getFile

            FileModelDetail file = gson.fromJson(json, FileModelDetail.class);      //to fix - getFile

            FileModel fileFull = FilesDB.jsonFillFile(file, json);
            if (!fileFull.sharedUsers.isEmpty()) {
                if (fileFull.sharedUsers.get(0).equals(email)) {
                    list.add(file);
                }
            }
        }
        if (list.isEmpty())
            return null;
        return list;
    }

    @Override
    public FileModel getFile(String file_name, String email)
    {
        List<GroupModel> groups = getGroupsByUser(email);

        String json = "", jsonPath1 = "", jsonPath2 = "";
        Gson gson = new Gson();
        DBCollection coll = this.db1.getCollection("files");
        DBCursor cur1 = coll.find();
        while (cur1.hasNext()) {
            json = cur1.next().toString();
            //server.FileModel file = gson.fromJson(json, server.FileModel.class);

            FileModelDetail file = gson.fromJson(json, FileModelDetail.class);
            if (file._id.equals(""))
                return null;
            FileModel fileFull = FilesDB.jsonFillFile(file, json);

            if (fileFull.file_name.equals(file_name) && fileFull.sharedUsers.contains(email))
            {
                return fileFull;
            }
            else
            {
                if (groups != null)
                {
                    for (int i = 0; i < groups.size(); i++)
                    {
                        if (fileFull.sharedGroups.contains(groups.get(i)._id) && fileFull.file_name.equals(file_name))
                        {
                            return fileFull;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public FileModelDetail getFileDetails(String file_name, String email)
    {
        List<GroupModel> groups = getGroupsByUser(email);

        String json = "", jsonPath1 = "", jsonPath2 = "";
        Gson gson = new Gson();
        DBCollection coll = this.db1.getCollection("files");
        DBCursor cur1 = coll.find();
        while (cur1.hasNext()) {
            json = cur1.next().toString();
            //server.FileModel file = gson.fromJson(json, server.FileModel.class);

            FileModelDetail file = gson.fromJson(json, FileModelDetail.class);
            if (file._id.equals(""))
                return null;
            FileModel fileFull = FilesDB.jsonFillFile(file, json);

            if (fileFull.file_name.equals(file_name) && fileFull.sharedUsers.contains(email))
            {
                return file;
            }
            else
            {
                if (groups != null)
                {
                    for (int i = 0; i < groups.size(); i++)
                    {
                        if (fileFull.sharedGroups.contains(groups.get(i)._id) && fileFull.file_name.equals(file_name))
                        {
                            return file;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void printFilesDB()
    {
        DBCollection coll = this.db1.createCollection("files", null);
        DBCursor cur1 = coll.find();

        while (cur1.hasNext()) {
            System.out.println(cur1.next());
        }
    }

    @Override
    public void printFilesOfUserDB(String email) {
        DBCollection coll = this.db1.createCollection("files", null);
        DBCursor cur1 = coll.find();
        String json = "";
        Gson gson = new Gson();

        while (cur1.hasNext()) {
            json = cur1.next().toString();
            FileModelDetail file = gson.fromJson(json, FileModelDetail.class);      //debug to fix
            FileModel fileFull = FilesDB.jsonFillFile(file, json);
            if (fileFull.sharedUsers.contains(email)) {
                System.out.println(file.file_name + "." + file.type);
                System.out.println("Size: " + Integer.parseInt(file.sizeFile) / 1000 + "KB (" + file.sizeFile + " Bytes)");
                System.out.println("Date: " + file.date);
            }
        }
    }
    @Override
    public void deleteFileById(FileModel file)
    {
        DBCollection coll = this.db1.createCollection("files", null);
        BasicDBObject query = new BasicDBObject();
        query = FilesDB.convert(file);

        coll.remove(query);     //delete the file from collection
    }
    @Override
    public boolean checkUserExists(String email)
    {
        String json = "";
        Gson gson = new Gson();
        DBCollection coll = this.db1.getCollection("users");
        DBCursor cur1 = coll.find();
        while (cur1.hasNext())
        {
            json = cur1.next().toString();
            UserModel user = gson.fromJson(json, UserModel.class);
            if (user.email.equals(email))
                return true;
        }
        return false;
    }

    @Override
    public void insertUser(UserModel user)
    {
        if(this.checkUserExists(user.email))
        {
            System.out.println("This user already exist..");
            return;
        }
        DBCollection coll = this.db1.createCollection("users", null);
        BasicDBObject tmp = new BasicDBObject();
        tmp = UsersDB.convert(user);
        coll.insert(tmp);
    }

    @Override
    public UserModel getUser(String email, String password) {
        String json = "";
        Gson gson = new Gson();
        DBCollection coll = this.db1.getCollection("users");
        DBCursor cur1 = coll.find();
        while (cur1.hasNext()) {
            json = cur1.next().toString();
            UserModel user = gson.fromJson(json, UserModel.class);
            if (user.email.equals(email)) {
                if (user.password.equals(password)) {
                    return user;
                } else {
                    System.out.println("Wrong Password!");
                }
            }
        }

        return null;
    }

    @Override
    public void ChangeUserName(UserModel user, String newName) {
        if (user == null) {
            System.out.println("No Logged User");
            return;
        }
        BasicDBObject query = new BasicDBObject();
        if (!user.username.equals(newName)) {
            query.put("username", user.username); // (1)

            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("username", newName); // (2)

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument); // (3)

            this.db1.getCollection("users").update(query, updateObject); // (4)
        }
    }

    @Override
    public void printUsersDB() {
        DBCollection coll = this.db1.createCollection("users", null);
        DBCursor cur1 = coll.find();

        while (cur1.hasNext()) {
            System.out.println(cur1.next());
        }
    }

    @Override
    public String getUserStorageAvailable(UserModel user) {
        String sizeStr = "";
        int start = 0;
        List<FileModelDetail> list = new ArrayList<>();
        list = getFilesByOwner(user.email);
        if (list == null)
            return user.StorageSize;

        start = Integer.parseInt(user.StorageSize);
        for (int i = 0; i < list.size(); i++) {
            start -= Integer.parseInt(list.get(i).sizeFile);
        }
        sizeStr = Integer.toString(start);
        return sizeStr;
    }
    @Override
    public void insertGroup(GroupModel grp)
    {
        DBCollection coll = this.db1.createCollection("groups", null);
        BasicDBObject tmp = new BasicDBObject();
        tmp = GroupsDB.convert(grp);
        coll.insert(tmp);
    }
    @Override
    public void inviteToGroup(GroupModel grp, String email)
    {
        if(!this.checkUserExists(email))
        {
            System.out.println("This user do not exist..");
            return;
        }

        BasicDBObject query = new BasicDBObject();

        query.put("_id", grp._id); // (1)

        grp.invitedParticipants.add(email);

        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("invitedParticipants", grp.invitedParticipants); // (2)

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument); // (3)

        this.db1.getCollection("groups").update(query, updateObject); // (4)
    }
    @Override
    public void kickFromGroup(GroupModel grp, String email)
    {
        if (grp.invitedParticipants.contains(email))
        {
            //delete from invited
            BasicDBObject query = new BasicDBObject();

            query.put("_id", grp._id); // (1)

            grp.invitedParticipants.remove(email);
            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("invitedParticipants", grp.invitedParticipants); // (2)

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument); // (3)

            this.db1.getCollection("groups").update(query, updateObject); // (4)
        }
        if (grp.participants.contains(email))
        {
            //delete from participants
            BasicDBObject query2 = new BasicDBObject();

            query2.put("_id", grp._id); // (1)

            grp.participants.remove(email);
            BasicDBObject newDocument2 = new BasicDBObject();
            newDocument2.put("participants", grp.participants); // (2)

            BasicDBObject updateObject2 = new BasicDBObject();
            updateObject2.put("$set", newDocument2); // (3)

            this.db1.getCollection("groups").update(query2, updateObject2); // (4)
        }
    }
    @Override
    public void acceptGroup(GroupModel grp, String email)
    {
        if (!grp.invitedParticipants.contains(email))
        {
            System.out.println("You are not invited");
            return;
        }
        //delete from invited
        BasicDBObject query = new BasicDBObject();

        query.put("_id", grp._id); // (1)

        grp.invitedParticipants.remove(email);
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("invitedParticipants", grp.invitedParticipants); // (2)

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument); // (3)

        this.db1.getCollection("groups").update(query, updateObject); // (4)

        //add to participants
        BasicDBObject query2 = new BasicDBObject();

        query2.put("_id", grp._id); // (1)

        grp.participants.add(email);
        BasicDBObject newDocument2 = new BasicDBObject();
        newDocument2.put("participants", grp.participants); // (2)

        BasicDBObject updateObject2 = new BasicDBObject();
        updateObject2.put("$set", newDocument2); // (3)

        this.db1.getCollection("groups").update(query2, updateObject2); // (4)
    }
    @Override
    public void rejectGroup(GroupModel grp, String email)
    {
        if (!grp.invitedParticipants.contains(email))
        {
            System.out.println("You are not invited");
            return;
        }

        //delete from invited
        BasicDBObject query = new BasicDBObject();

        query.put("_id", grp._id); // (1)

        grp.invitedParticipants.remove(email);
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("invitedParticipants", grp.invitedParticipants); // (2)

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument); // (3)

        this.db1.getCollection("groups").update(query, updateObject); // (4)

    }
    @Override
    public void leaveGroup(GroupModel grp, String email)
    {
        if (!grp.participants.contains(email))
        {
            System.out.println("You are not in group");
            return;
        }
        if (grp.participants.get(0).equals(email))
        {
            System.out.println("You are the owner you can't leave the group.. try delete the group.");
            return;
        }

        //delete from participants
        BasicDBObject query2 = new BasicDBObject();

        query2.put("_id", grp._id); // (1)

        grp.participants.remove(email);
        BasicDBObject newDocument2 = new BasicDBObject();
        newDocument2.put("participants", grp.participants); // (2)

        BasicDBObject updateObject2 = new BasicDBObject();
        updateObject2.put("$set", newDocument2); // (3)

        this.db1.getCollection("groups").update(query2, updateObject2); // (4)
    }
    @Override
    public List<GroupModel> getGroupsByUser(String email)
    {
        List<GroupModel> list = new ArrayList<>();
        String json = "", jsonPath1 = "", jsonPath2 = "";
        Gson gson = new Gson();
        DBCollection coll = this.db1.getCollection("groups");
        DBCursor cur1 = coll.find();
        while (cur1.hasNext()) {
            json = cur1.next().toString();

            GroupModelDetail group = gson.fromJson(json, GroupModelDetail.class);      //to fix - getFile
            GroupModel groupFull = GroupsDB.jsonFillGroup(group, json);

            if (groupFull.participants.contains(email)) {
                list.add(groupFull);
            }
        }
        if (list.isEmpty())
            return null;
        return list;
    }
    @Override
    public List<GroupModel> getGroupsByUserInvited(String email)
    {
        List<GroupModel> list = new ArrayList<>();
        String json = "", jsonPath1 = "", jsonPath2 = "";
        Gson gson = new Gson();
        DBCollection coll = this.db1.getCollection("groups");
        DBCursor cur1 = coll.find();
        while (cur1.hasNext()) {
            json = cur1.next().toString();

            GroupModelDetail group = gson.fromJson(json, GroupModelDetail.class);      //to fix - getFile
            GroupModel groupFull = GroupsDB.jsonFillGroup(group, json);

            if (groupFull.invitedParticipants.contains(email)) {
                list.add(groupFull);
            }
        }
        if (list.isEmpty())
            return null;
        return list;
    }
    @Override
    public List<GroupModel> getGroupsByOwner(String email)
    {
        List<GroupModel> list = new ArrayList<>();
        String json = "", jsonPath1 = "", jsonPath2 = "";
        Gson gson = new Gson();
        DBCollection coll = this.db1.getCollection("groups");
        DBCursor cur1 = coll.find();
        while (cur1.hasNext()) {
            json = cur1.next().toString();

            GroupModelDetail group = gson.fromJson(json, GroupModelDetail.class);      //to fix - getFile
            GroupModel groupFull = GroupsDB.jsonFillGroup(group, json);

            if (!groupFull.participants.isEmpty()) {
                if (groupFull.participants.get(0).equals(email)) {
                    list.add(groupFull);
                }
            }
        }
        if (list.isEmpty())
            return null;
        return list;
    }
    @Override
    public GroupModel getGroupByID(String id)
    {
        List<GroupModel> list = new ArrayList<>();
        String json = "", jsonPath1 = "", jsonPath2 = "";
        Gson gson = new Gson();
        DBCollection coll = this.db1.getCollection("groups");
        DBCursor cur1 = coll.find();
        while (cur1.hasNext())
        {
            json = cur1.next().toString();

            GroupModelDetail group = gson.fromJson(json, GroupModelDetail.class);      //to fix - getFile
            GroupModel groupFull = GroupsDB.jsonFillGroup(group, json);

            if (groupFull._id.equals(id))
            {
                return groupFull;
            }
        }
        return null;
    }
    @Override
    public void ChangeGroupName(GroupModel grp, String newName)
    {
        BasicDBObject query = new BasicDBObject();
        if (!grp.groupName.equals(newName)) {
            query.put("_id", grp._id); // (1)

            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("groupName", newName); // (2)

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument); // (3)

            this.db1.getCollection("groups").update(query, updateObject); // (4)
        }
    }
    @Override
    public void printGroupsDB()
    {
        DBCollection coll = this.db1.createCollection("groups", null);
        DBCursor cur1 = coll.find();

        while (cur1.hasNext()) {
            System.out.println(cur1.next());
        }
    }
    @Override
    public void insertServer(ConnectedServerModel srv)
    {
        DBCollection coll = this.db1.createCollection("servers", null);
        //Document tmp = new Document();
        BasicDBObject tmp = new BasicDBObject();
        tmp = ServersDB.convert(srv);

        coll.insert(tmp);
    }
    @Override
    public List<ConnectedServerModel> getServers()
    {
        List<ConnectedServerModel> list = new ArrayList<>();
        String json = "";
        Gson gson = new Gson();
        DBCollection coll = this.db1.getCollection("servers");
        DBCursor cur1 = coll.find();
        while (cur1.hasNext()) {
            json = cur1.next().toString();

            ConnectedServerModel srv = gson.fromJson(json, ConnectedServerModel.class);
            list.add(srv);
        }
        if (list.isEmpty())
            return null;
        return list;
    }
    @Override
    public void printServersDB()
    {
        DBCollection coll = this.db1.createCollection("servers", null);
        DBCursor cur1 = coll.find();

        while (cur1.hasNext()) {
            System.out.println(cur1.next());
        }
    }


}