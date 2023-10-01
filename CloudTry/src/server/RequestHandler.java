package server;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RequestHandler implements RequesrHandleUser, RequestHandleGroup, RequestHandleFile, RequestHandleServer
{
    public ClientState state;
    CommsCodes code = CommsCodes.RENAME;
    public List<String> params = new ArrayList<>();
    Database db;

    RequestHandler(ClientState cl, int c, List<String> par)
    {
        state = cl;
        code = code.findByAbbr(c);
        params = par;
        db = new Database();
    }

    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    //if the code is 201/202 (register / login)
    @Override
    public String handlePreRequest()
    {
        List<String> list = new ArrayList<>();
        UserModel onlineUser = new UserModel();
        UserModel user = new UserModel();
        String json = "";

        switch (code) {
            case REGISTER:
                //Register
                user = new UserModel();
                user.email = params.get(0);
                user.username = params.get(1);
                user.password = params.get(2);
                user.StorageSize = params.get(3);   //"1000000";   //(in kb)

                db.insertUser(user);
                onlineUser = db.getUser(user.email, user.password);
                break;
            case LOG:
                //LOGIN
                user = new UserModel();
                user.email = params.get(0);
                user.password = params.get(1);

                onlineUser = db.getUser(user.email, user.password);
                break;
            default:
                return null;
        }

        if (onlineUser != null)
        {
            //checks if there is difference in user
            if (onlineUser.email == "")
            {
                System.out.println("Not logged in..");
                return "ERROR";
            }
            else
            {
                state.setIs_lobby(true);
                state.setUserState(onlineUser);
                //ok - create user model and save it on state
            }
        }
        else
            return "ERROR";

        json = new Gson().toJson(list);
        return json;
    }

    @Override
    public String handlePreFileRequest() throws IOException {
        FileModelDetail tmp;
        FileModel save = new FileModel();
        List<String> list = new ArrayList<>();
        List<FileModelDetail> files = new ArrayList<>();
        String json = "", storage = "", name = "", name2 = "";
        List<FileModelDetail> tmpFiles = new ArrayList<>();

        switch (code) {
        case FILE_GET_OWNER:
            files = db.getFilesByOwner(state.getUserState().email);
            if (files != null) {
                for (int i = 0; i < files.size(); i++) {
                    tmpFiles.add(files.get(i));
                }
            }
            break;
        case FILE_GET:
            name = params.get(0);
            tmp = db.getFileDetails(name, state.getUserState().email);
            tmpFiles.add(tmp);
            save = db.getFile(name, state.getUserState().email);
            state.setFileState(save);
            state.setIs_file(true);
            break;
        case FILES_GET:
            files = db.getFilesByUser(state.getUserState().email);
            if (files != null) {
                for (int i = 0; i < files.size(); i++) {
                    tmpFiles.add(files.get(i)); //.toJson() + ",");
                }
            }
            break;
        }

        json = new Gson().toJson(tmpFiles);
        return json;
    }

    @Override
    public String handlePreGroupRequest()
    {
        List<GroupModel> groups = new ArrayList<>();
        GroupModel grp = new GroupModel();
        String json = "", id = "";
        List<GroupModel> list = new ArrayList<>();

        switch (code)
        {
            case GROUP_CREATE:
                //check there no double names by same owner
                list = db.getGroupsByOwner(state.getUserState().email);

                grp.groupName = params.get(0);
                grp._id = generateString();
                grp.participants.add(state.getUserState().email);

                if (list != null)
                {
                    for (int i = 0; i < list.size(); i++)
                    {
                        if (list.get(i).groupName.equals(grp.groupName))
                        {
                            System.out.println("You already have group called " + grp.groupName);
                            return "ERROR";
                        }
                    }
                }

                db.insertGroup(grp);
                break;
            case GROUP_GET_OWNER:
                groups = db.getGroupsByOwner(state.getUserState().email);
                break;
            case GROUP_GET:
                groups = db.getGroupsByUser(state.getUserState().email);
                break;
            case GROUP_GET_INVITED:
                groups = db.getGroupsByUserInvited(state.getUserState().email);
                break;
            case GROUP_GET_ID:
                id = params.get(0);

                grp = (db.getGroupByID(id));
                if (grp == null || grp._id == "")
                    return "ERROR";
                groups.add(grp);
                state.setGroupState(grp);
                state.setIs_group(true);
                break;
        }

        json = new Gson().toJson(groups);
        return json;
    }

    @Override
    public String handleUploadRequest(byte[] data) throws IOException
    {
        String json = "";
        List<FileModelDetail> tmpFiles = new ArrayList<>();
        System.out.println("UPLOAD");

        //get the upload file date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        int fileSize = Integer.parseInt(params.get(1));
        int totalSize = data.length - 46;
        byte[] fileData = Arrays.copyOfRange(data, totalSize - fileSize, totalSize);

        String tmp2 = new String( fileData, StandardCharsets.UTF_8); // for UTF-8 encoding
        System.out.println(tmp2);

        String type = params.get(0).substring(params.get(0).length() - 6, params.get(0).length());
        String fileName = params.get(0).substring(0, params.get(0).indexOf("."));

        FileModel fileSave = new FileModel();
        fileSave._id = FileSystem.generateString();
        fileSave.file_name = fileName;
        fileSave.sizeFile = params.get(1);
        fileSave.date = dtf.format(now);
        fileSave.star = "false";
        fileSave.type = type.substring(type.indexOf(".") + 1, type.length());
        fileSave.sharedUsers.add(state.getUserState().email);

        db.insertFile(fileSave);

        //FileSystem.saveFile(fileData);

        json = new Gson().toJson(tmpFiles);
        return json;
    }

    /*
    Separates the handlers between types of requests
     */
    public String handleRequest(ClientState cl) throws IOException {
        String json = "";
        UserModel usr = new UserModel();
        state = cl;

        if (!cl.getIs_lobby())   //not logged yet
        {
            json = handlePreRequest();
        }
        else
        {
            if (code.isUser())
            {
               json =  handleUserRequest(state.getUserState());
            }
            else if (code.isGroup())
            {
                if (!cl.getIs_group())
                {
                    json = handlePreGroupRequest();
                }
                else
                {
                    json =  handleGroupRequest(state.getUserState(), state.getGroupState());
                }
            }
            else if (code.isFile())
            {
                if (!cl.getIs_file())   //not logged yet
                {
                    json = handlePreFileRequest();
                }
                else
                {
                    json =  handleFileRequest(state.getUserState(), state.getFileState());
                }
            }
            else if (code.isServer())
            {
                json = handleServerRequest();
            }
        }
        if (json != "ERROR")
        {
            cl = state;
        }

        //none of codes fits
        return json;
    }

    @Override
    public String handleUserRequest(UserModel usr)
    {
        String name = "", json = "";
        StorageModel storage = new StorageModel();
        List<StorageModel> list = new ArrayList<>();
        List<UserModel> list2 = new ArrayList<>();
        UserModel user = new UserModel();

        switch (code){
            case REGISTER:
                return "ERROR";
            case LOG:
                list2.add(db.getUser(usr.email, usr.password));
                json = new Gson().toJson(list2);
                return json;
            case RENAME:
                //Register
                name = params.get(0);
                db.ChangeUserName(usr, name);
                user = db.getUser(usr.email, usr.password);
                state.setUserState(user);
                break;
            case STORAGE:
                name = db.getUserStorageAvailable(usr);
                storage.storage = name;
                System.out.println("Max capacity: " + usr.StorageSize + "KB");
                System.out.println("Available storage: " + storage + "KB");
                list.add(storage);
                break;
            case USER_PASSWORD:
                break;
            case USER_DELETE:
                break;
            case SIGNOUT:
                state = null;
                break;
        }

        json = new Gson().toJson(list);
        return json;
    }

    @Override
    public String handleGroupRequest(UserModel usr, GroupModel g)
    {
        List<GroupModel> groups = new ArrayList<>();
        List<GroupModel> list = new ArrayList<>();
        GroupModel grp = new GroupModel();
        String json = "", id = "", name = "";

        switch (code)
        {
            case INVITE_GROUP:
                list =  db.getGroupsByOwner(usr.email);

                grp = db.getGroupByID(g._id);
                if (grp == null)
                {
                    System.out.println("User don't have groups");
                    return "ERROR";
                }

                name = params.get(0);

                db.inviteToGroup(grp, name);
                break;
            case KICK_GROUP:
                grp = db.getGroupByID(g._id);
                if (grp == null)
                {
                    System.out.println("User don't have groups");
                    return "ERROR";
                }
                if (grp.participants.size() == 1)
                {
                    System.out.println("User can't remove himself");
                    return "ERROR";
                }

                name = params.get(0);

                db.kickFromGroup(grp, name);
                break;
            case GROUP_NAME:
                list =  db.getGroupsByOwner(usr.email);

                grp = db.getGroupByID(g._id);
                if (grp == null)
                {
                    System.out.println("User don't have groups");
                    return "ERROR";
                }

                name = params.get(0);

                db.ChangeGroupName(grp, name);
                break;
            case GROUP_DELETE:
                break;
            case GROUP_LEAVE:
                list =  db.getGroupsByUser(usr.email);

                grp = db.getGroupByID(g._id);
                if (grp == null)
                {
                    System.out.println("User don't shared by any group");
                    return "ERROR";
                }
                db.leaveGroup(grp, usr.email);
                break;
            case GROUP_ACCEPT:
                list =  db.getGroupsByUserInvited(usr.email);

                grp = db.getGroupByID(g._id);
                if (grp == null)
                {
                    System.out.println("User don't have an requests");
                    return "ERROR";
                }
                db.acceptGroup(grp, usr.email);
                break;
            case GROUP_REJECT:
                list =  db.getGroupsByUserInvited(usr.email);

                grp = db.getGroupByID(g._id);
                if (grp == null)
                {
                    System.out.println("User don't have an requests");
                    return "ERROR";
                }
                db.rejectGroup(grp, usr.email);
                break;
            case GROUP_GET_OWNER, GROUP_GET, GROUP_GET_INVITED, GROUP_GET_ID, GROUP_CREATE:
                return handlePreGroupRequest();

        }
        json = new Gson().toJson(groups);
        return json;
    }

    @Override
    public String handleFileRequest(UserModel usr, FileModel f) throws IOException {
        String json = "", storage = "", name = "", name2 = "";
        List<FileModelDetail> tmpFiles = new ArrayList<>();
        List<FileModelDetail> files = new ArrayList<>();
        FileModel file;
        switch (code)
        {
            case FILE_STAR:
               file = db.getFile(f.file_name, usr.email);

                if (file != null)
                {
                    if (file.sharedUsers.get(0).equals(usr.email))
                        db.MarkStar(file);
                    else
                    {
                        System.out.println("You are not the owner of the file..");
                        return "ERROR";
                    }
                }
                else
                {
                    System.out.println("File not exists..");
                    return "ERROR";
                }
                break;
            case FILE_NAME:
                name = params.get(0);
                file = db.getFile(f.file_name,usr.email);

                if (file != null)
                {
                    if (file.sharedUsers.get(0).equals(usr.email))
                        db.ChangeFileName(file, name);
                    else
                    {
                        System.out.println("You are not the owner of the file..");
                        return "ERROR";
                    }
                }
                else
                {
                    System.out.println("File not exists..");
                    return "ERROR";
                }
                break;
            case FILE_SHARE:
                //share file by changing the list of users shares in files collection
                name = params.get(0);

                file = db.getFile(f.file_name, usr.email);
                if (file.sharedUsers.contains(name))
                    return "ERROR";

                if (file != null)
                {
                    if (file.sharedUsers.get(0).equals(usr.email))
                        db.ShareFile(file, name);
                    else {
                        System.out.println("You are not the owner of the file..");
                        return "ERROR";
                    }
                }
                else
                {
                    System.out.println("File not exists..");
                    return "ERROR";
                }
                break;
            case FILE_SHARE_GROUP:
                name = params.get(0);

                file = db.getFile(f.file_name, usr.email);
                if (file.sharedGroups.contains(name))
                    return "ERROR";
                if (file != null)
                {
                    if (file != null)
                    {
                        if (!file.sharedUsers.get(0).equals(usr.email))
                        {
                            System.out.println("You are not the owner of the file..");
                            return "ERROR";
                        }
                    }
                    else
                    {
                        System.out.println("File not exists..");
                        return "ERROR";
                    }

                    db.ShareFileToGroup(file, name);
                }
                else
                    System.out.println("File not exists..");
                break;
            case FILE_GET_OWNER, FILE_GET, FILES_GET, FILE_ADD:
                return handlePreFileRequest();
            case FILES_DELETE:
                file = db.getFile(f.file_name, usr.email);
                //FileSystem.deleteFile(file);
                break;
            case FILE_DOWNLOAD:
                file = db.getFile(f.file_name, usr.email);
                byte[] data = FileSystem.getBytesFromFiles("D:\\magshimim\\Y3\\cloud\\galilmaaravi-603-cloud\\Cloud-sprint_1", file);
                json = "{" + data + "}";
                return json;
        }

        json = new Gson().toJson(tmpFiles);
        return json;
    }

    @Override
    public String handleServerRequest()
    {
        switch (code)
        {
            case SERVER_ADD:
                break;
            case SERVER_GET:
                break;
        }
        return null;
    }
}
