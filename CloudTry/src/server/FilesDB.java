package server;

import com.google.gson.Gson;
import com.mongodb.*;

import java.util.ArrayList;
import java.util.List;

interface FilesDB {

    /*
    Static function that get the object of file and convert it to the type of basicDBObject
    soo it can be inserted into the database
     */
    public static BasicDBObject convert(FileModel file)
    {
        BasicDBObject basicDBObject = new BasicDBObject();
        List<BasicDBObject> list1 = new ArrayList<BasicDBObject>();
        List<BasicDBObject> list2 = new ArrayList<BasicDBObject>();
        List<BasicDBObject> list3 = new ArrayList<BasicDBObject>();
        List<BasicDBObject> list4 = new ArrayList<BasicDBObject>();
        if (file != null)
        {
            for (PathModel path: file.adr)
            {
                BasicDBObject tmp = new BasicDBObject();
                tmp.append("path", path.path).append("ip", path.ip);
                list1.add(tmp);
            }
            for (PathModel path: file.adrCopy)
            {
                BasicDBObject tmp = new BasicDBObject();
                tmp.append("path", path.path).append("ip", path.ip);
                list2.add(tmp);
            }
            for (String user: file.sharedUsers)
            {
                BasicDBObject tmp = new BasicDBObject();
                tmp.append("email", user);
                list3.add(tmp);
            }
            for (String group: file.sharedGroups)
            {
                BasicDBObject tmp = new BasicDBObject();
                tmp.append("_id", group);
                list4.add(tmp);
            }
        }

        basicDBObject.append("_id", file._id).append("file_name", file.file_name).
                append("date", file.date).append("star", file.star).append("type", file.type).
                append("adr", list1).append("adrCopy", list2).append("sizeFile", file.sizeFile)
                .append("sharedUsers", list3).append("sharedGroups", list4);

        return basicDBObject;
    }

    /*
    Static function that get the details of the file with the full json string of the data
    about the file and fill the others fields of the file that couldn't be filled because
    they are arrays
    Returned the full File Object
     */
    public static FileModel jsonFillFile(FileModelDetail file, String json)
    {
        String jsonPath1 = "", jsonPath2 = "";
        FileModel fileFull = new FileModel();
        Gson gson = new Gson();

        fileFull.date = file.date;
        fileFull._id = file._id;
        fileFull.file_name = file.file_name;
        fileFull.star = file.star;
        fileFull.type = file.type;
        fileFull.sizeFile = file.sizeFile;

        jsonPath1 = json.substring(json.indexOf("\"adr"), json.indexOf("adrCopy"));
        while (jsonPath1.contains("{"))
        {
            jsonPath2 = jsonPath1.substring(jsonPath1.indexOf("{"), jsonPath1.indexOf("}") + 1);
            PathModel path = gson.fromJson(jsonPath2, PathModel.class);
            jsonPath1 = jsonPath1.substring(jsonPath1.indexOf("},") + 2, jsonPath1.length());
            if (jsonPath1.contains("{"))
                fileFull.adr.add(path);
        }

        jsonPath1 = json.substring(json.indexOf("\"adrCopy"), json.indexOf("sizeFile"));
        while (jsonPath1.contains("{"))
        {
            jsonPath2 = jsonPath1.substring(jsonPath1.indexOf("{"), jsonPath1.indexOf("}") + 1);
            PathModel path = gson.fromJson(jsonPath2, PathModel.class);
            jsonPath1 = jsonPath1.substring(jsonPath1.indexOf("},") + 2, jsonPath1.length());
            if (jsonPath1.contains("{"))
                fileFull.adrCopy.add(path);
        }

        jsonPath1 = json.substring(json.indexOf("\"shared"), json.indexOf(", \"sharedGroups"));
        if (jsonPath1.contains("{"))
        {
            jsonPath2 = jsonPath1.substring(jsonPath1.indexOf("{"), jsonPath1.indexOf("}") + 1);
            String tmpEmail = jsonPath2.substring(jsonPath2.indexOf(":") + 3, jsonPath2.indexOf("\"}"));
            jsonPath1 = jsonPath1.substring(jsonPath1.indexOf("}") + 2, jsonPath1.length());
            fileFull.sharedUsers.add(tmpEmail);
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
                    fileFull.sharedUsers.add(tmpEmail);
                }
                else
                {
                    tmpEmail = jsonPath2.substring(0, jsonPath2.indexOf("]") - 1);
                    fileFull.sharedUsers.add(tmpEmail);
                    break;
                }
            }
        }

        jsonPath1 = json.substring(json.indexOf("\"sharedGroups"), json.length());
        jsonPath1 = jsonPath1.substring(jsonPath1.indexOf("[") + 1, jsonPath1.length());
        while (jsonPath1.contains("]"))
        {
            jsonPath2 = jsonPath1.substring(jsonPath1.indexOf("\"") + 1, jsonPath1.indexOf("]") + 1);
            String tmpEmail = "";
            if (jsonPath1.contains(","))
            {
                tmpEmail = jsonPath2.substring(0, jsonPath2.indexOf(",") - 1);
                jsonPath1 = jsonPath1.substring(jsonPath1.indexOf(",") + 1, jsonPath1.length());
                fileFull.sharedGroups.add(tmpEmail);
            }
            else
            {
                if (jsonPath2.equals("]"))
                    return fileFull;
                tmpEmail = jsonPath2.substring(0, jsonPath2.indexOf("]") - 1);
                fileFull.sharedGroups.add(tmpEmail);
                return fileFull;
            }
        }

        return fileFull;
    }
    /*
    Function that delete the whole database collection
     */
    public void deleteDB();
    /*
    Function that takes as parameter the file object and insert it into the database
     */
    public void insertFile(FileModel file);

    /*OWNER ACTIONS*/

    /*
    Query that change the field of markStar between true to false
     */
    public void MarkStar(FileModel file);
    /*
    Query that change the name of the file
     */
    public void ChangeFileName(FileModel file, String newName);
    /*
    Query that add new emails to shared array of the file
     */
    public void ShareFile(FileModel file, String newEmail);
    /*
    Query thar add new groups to the shared groups of the file
     */
    public void ShareFileToGroup(FileModel file, String groupID);

    /*
    Query that returns file object by the file name and the owner name
     */
    public FileModel getFile(String file_name, String email);
    /*
    Query that returns file details object by the file name and the owner name
     */
    public FileModelDetail getFileDetails(String file_name, String email);
    /*
    Query that returns files objects by email (not owner necessarily)
     */
    public List<FileModelDetail> getFilesByUser(String email);
    /*
    Query that returns files objects by email (owner necessarily)
     */
    public List<FileModelDetail> getFilesByOwner(String email);
    /*
    Function that print the collection of the files database
     */
    public void printFilesDB();
    /*
    Function that prints in the console the list of files by the user
     */
    public void printFilesOfUserDB(String email);

    /*
    Function that remove file from collection
     */
    public void deleteFileById(FileModel file);
}
