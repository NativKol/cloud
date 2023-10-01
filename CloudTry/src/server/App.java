package server;

import server.*;

import java.io.*;
import java.net.Inet4Address;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.filechooser.FileSystemView;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class App {
    private static final String Path = "D:\\magshimim\\Y3\\cloud\\galilmaaravi-603-cloud\\Cloud-sprint_1\\";
    private static final String normalSize = "1000000";   //(in kb)

    public static UserModel onlineUser = new UserModel();

    /*
    Function that generate random string ids
     */
    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    /*
    basic substring method that using for getting parts of bytes arrays
     */
    public static byte[] substring(byte[] array, int start, int end)
    {
        if (end <= start)
            return null;
        int length = (end - start);

        byte[] newArray = new byte[length];
        System.arraycopy(array, start, newArray, 0, length);
        return newArray;
    }

    /*
    Function that saves file by the algorithm of our cloud
    saves in the available drives ob our servers and splits the files
    and make from them duplicated and then save all the parts in different locations
     */
    public static FileModel saveBytesFiles(String inputPath, String fileName, int splits, int duplicates, String[] listDrives, String capacity)
    {
        int i = 0, j = 0, pos = 0, read = 0;
        int slice = listDrives.length;
        int size = 0;
        String path = "", storage = "";

        byte[] buff = new byte[10000];

        FileModel file = new FileModel();
        file.file_name = fileName;


        try {
            InputStream sourcefile = new FileInputStream(inputPath);
            //get the size of file
            InputStream sourcefileSize = new FileInputStream(inputPath);
            size = sourcefileSize.readAllBytes().length;
            System.out.println(size/1000 + " KB");

            file.sizeFile = String.valueOf(size);

            //you don't have enough storage
            if (Integer.parseInt(file.sizeFile) > Integer.parseInt(capacity))
            {
                System.out.println("you don't have enough storage space");
                System.out.println("File size: " + file.sizeFile + "KB");
                System.out.println("Available storage: " + storage + "KB");
                return null;
            }

            file._id = generateString();
            file.sharedUsers.add(onlineUser.email);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            while((read = sourcefile.read(buff)) != -1) {
                bytes.write(buff, 0, read);
            }

            //adding copies
            for (j = 0; j < duplicates; j++)
            {
                //save to file
                for (i = 0; i < splits; i++)
                {
                    byte[] saveFile = new byte[bytes.toByteArray().length];
                    pos = (i / slice) + j;
                    if (pos >= slice)
                    {
                        pos = 0;
                    }
                    path = listDrives[pos] + "/" +  String.valueOf(j) + String.valueOf(i) + "binaryFile_"  + file._id + ".txt";
                    //save path of parts of file
                    if (j == 0)
                    {
                        PathModel pathTmp = new PathModel();
                        pathTmp.path = path;
                        pathTmp.ip = Inet4Address.getLocalHost().getHostAddress();
                        file.adr.add(pathTmp);
                    }
                    else if (j == 1)
                    {
                        PathModel pathTmp = new PathModel();
                        pathTmp.path = path;
                        pathTmp.ip = Inet4Address.getLocalHost().getHostAddress();
                        file.adrCopy.add(pathTmp);
                    }

                    //System.out.println(path + " Saved");
                    OutputStream outputData = new FileOutputStream(path);
                    int skip = bytes.toByteArray().length / splits;
                    saveFile = substring(bytes.toByteArray(), i * skip, (i + 1) * skip);
                    outputData.write(saveFile);
                    outputData.close();
                }
            }
            System.out.println("Saved");
        }catch (Exception e) {
            e.printStackTrace();
        }

        //get the upload file date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        file.date = dtf.format(now);
        file.star = "false";

        String type = inputPath.substring(inputPath.length() - 6, inputPath.length());
        file.type = type.substring(type.indexOf(".") + 1, type.length());

        return file;
    }

    /*
    Function that gets the details of the files and extract from the drives the full file
    and download him to the required path
     */
    public static byte[] getBytesFromFiles(String filePath, FileModel reversed) throws IOException {
        int n = 0, read = 0;
        byte[] buff = new byte[10000];

        if(reversed.adr.size() > 1) {
            File dst = new File(filePath + "\\" + reversed.file_name + "." + reversed.type);
            if (!dst.exists()) {
                System.out.println("invalid path!");
                //return;
            }
            FileOutputStream out = new FileOutputStream(dst);

            for (int i = 0; i < reversed.adr.size(); i++) {

                File src = new File(reversed.adr.get(i).path);
                FileInputStream in = new FileInputStream(src);

                while ((n = in.read()) != -1) {
                    out.write(n);
                }
                if(in != null)
                    in.close();
            }
            if(out != null)
                out.close();
        }
        String tmp = filePath + "\\" + reversed.file_name + "." + reversed.type;
        System.out.println(tmp);
        InputStream sourcefile = new FileInputStream(tmp);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        while((read = sourcefile.read(buff)) != -1) {
            bytes.write(buff, 0, read);
        }
        byte[] saveFile = new byte[bytes.toByteArray().length];
        saveFile = substring(bytes.toByteArray(), 0, bytes.toByteArray().length);
        //System.out.println(new String(saveFile, 0));\

        //delete file
        File dst = new File(tmp);
        Files.deleteIfExists(dst.toPath());

        return saveFile;

    }

    /*
    Function that gets the file model and delete the file from all the drives
     */
    public static boolean deleteFile(FileModel file) throws IOException {

        for(int i = 0; i < file.adr.size(); i++)
        {
            File dst = new File(file.adr.get(i).path);
            if (!Files.deleteIfExists(dst.toPath()))
            {
                return false;
            }
        }
        for(int i = 0; i < file.adrCopy.size(); i++)
        {
            File dst = new File(file.adrCopy.get(i).path);
            if (!Files.deleteIfExists(dst.toPath()))
            {
                return false;
            }
        }
        return true;
    }

    public static void setup() throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int count = 0, follow = 0;

        File[] paths;
        FileSystemView fsv = FileSystemView.getFileSystemView();


        // returns pathnames for files and directory
        paths = File.listRoots();
        String drivesPath[] = new String[paths.length];

        System.out.println("To setup the cloud on your computer \nType V if you want to use the disk drive\nType any other key if not");
        System.out.println("=========================================");
        // for each pathname in pathname array
        for(File path:paths)
        {
            // prints file and directory paths
            if (!Files.exists(Paths.get(path.toString(), "CloudStorage"), LinkOption.NOFOLLOW_LINKS))
            {
                count++;
                System.out.println(count + ". Drive Name: "+path);
                System.out.println("Description: "+fsv.getSystemTypeDescription(path));
                String choice = br.readLine();
                if (choice.equals("V"))
                {
                    drivesPath[follow] = path.toString();
                    follow++;
                }
                System.out.println("=========================================");
            }
        }

        for (String path: drivesPath)
        {
            if (path != null)
            {
                String pathCloud = path + "CloudStorage";
                new File(pathCloud).mkdirs();
                System.out.println("created directory in " + pathCloud);
            }
        }
    }

    public static String[] checkSetup() throws IOException
    {
        File[] paths;
        int count = 0;

        // returns pathnames for files and directory
        paths = File.listRoots();
        String drivesPath[] = new String[paths.length];

        // for each pathname in pathname array
        for(File path:paths)
        {
            if (Files.exists(Paths.get(path.toString(), "CloudStorage"), LinkOption.NOFOLLOW_LINKS))
            {
                drivesPath[count] = path.toString() + "CloudStorage";
                count++;
            }
        }
        return drivesPath;
    }

    public static void menuFiles(String[] listDrives, Database db) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String choice = "10", name = "", type = "", name2 = "", storage;
        //setup
        String filePath = "";
        int splits = 3;
        int duplicates = 2;

        while (Integer.parseInt(choice) > 9 || Integer.parseInt(choice) < 0)
        {
            System.out.println("MENU FILES ACTIONS");
            System.out.println("0. EXIT");
            System.out.println("1. Print Files");
            System.out.println("2. Upload");
            System.out.println("3. Download");
            System.out.println("4. Share File To User");
            System.out.println("5. Print File Size");
            System.out.println("6. Star File");
            System.out.println("7. Change File Name");
            System.out.println("8. Delete File");
            System.out.println("9. Share File To Group");
            choice = br.readLine();
            if (choice.equals("1"))
            {
                //prints the files in the database
                db.printFilesOfUserDB(onlineUser.email);
            }
            else if (choice.equals("2"))
            {
                //upload files to server
                System.out.println("Enter File Path: ");
                filePath = br.readLine();

                System.out.println("Enter File Name: ");
                name = br.readLine();

                //gets the available storage of user before upload new file
                storage = db.getUserStorageAvailable(onlineUser);

                //save the image to bytes files
                FileModel tmp = saveBytesFiles(filePath, name, splits * listDrives.length, duplicates, listDrives, storage);
                if (tmp != null)
                    db.insertFile(tmp);
            }
            else if (choice.equals("3"))
            {
                //download file by name
                System.out.println("Enter File Path For Save: ");
                filePath = br.readLine();

                System.out.println("Enter File Name To Search: ");
                name = br.readLine();

                FileModel tmp = db.getFile(name, onlineUser.email);

                //save the image by the bytes
                getBytesFromFiles(filePath, tmp);

            }
            else if (choice.equals("4"))
            {
                //share file by changing the list of users shares in files collection
                System.out.println("Enter File Name To Search: ");
                name = br.readLine();

                System.out.println("Enter User Email To Share: ");
                name2 = br.readLine();

                FileModel file = db.getFile(name, onlineUser.email);
                if (file != null)
                {
                    if (file.sharedUsers.get(0).equals(onlineUser.email))
                        db.ShareFile(file, name2);
                    else
                        System.out.println("You are not the owner of the file..");
                }
                else
                    System.out.println("File not exists..");

            }
            else if (choice.equals("5"))
            {
                //prints the size of the file by name
                System.out.println("Enter File Name To Search: ");
                name = br.readLine();

                FileModel file = db.getFile(name, onlineUser.email);
                if (file != null)
                    System.out.println(Integer.parseInt(file.sizeFile)/ 1000 + " KB");
                else
                    System.out.println("File not exists..");
            }
            else if (choice.equals("6"))
            {
                //prints the size of the file by name
                System.out.println("Enter File Name To Search: ");
                name = br.readLine();

                FileModel file = db.getFile(name, onlineUser.email);

                if (file != null)
                {
                    if (file.sharedUsers.get(0).equals(onlineUser.email))
                        db.MarkStar(file);
                    else
                        System.out.println("You are not the owner of the file..");
                }
                else
                    System.out.println("File not exists..");

            }
            else if (choice.equals("7"))
            {
                //prints the size of the file by name
                System.out.println("Enter File Name To Change: ");
                name = br.readLine();

                System.out.println("Enter The New Name: ");
                name2 = br.readLine();

                FileModel file = db.getFile(name, onlineUser.email);

                if (file != null)
                {
                    if (file.sharedUsers.get(0).equals(onlineUser.email))
                        db.ChangeFileName(file, name2);
                    else
                        System.out.println("You are not the owner of the file..");
                }
                else
                    System.out.println("File not exists..");

            }
            else if (choice.equals("8"))
            {
                System.out.println("Enter File Name To Search: ");
                name = br.readLine();

                FileModel tmp = db.getFile(name, onlineUser.email);

                //save the image by the bytes
                if (deleteFile(tmp))
                {
                    //delete from db
                    db.deleteFileById(tmp);
                }
                else
                {
                    System.out.println("couldn't find the file");
                }
            }
            else if (choice.equals("9"))
            {
                //share file to group

                System.out.println("Enter File Name To Search: ");
                name = br.readLine();

                FileModel file = db.getFile(name, onlineUser.email);
                if (file != null)
                {
                    if (file != null)
                    {
                        if (!file.sharedUsers.get(0).equals(onlineUser.email))
                        {
                            System.out.println("You are not the owner of the file..");
                            break;
                        }
                    }
                    else
                        System.out.println("File not exists..");
                    List<GroupModel> list =  db.getGroupsByUser(onlineUser.email);
                    GroupModel grp = chooseGroup(list);
                    if (grp == null)
                    {
                        System.out.println("User don't have groups");
                        break;
                    }
                    db.ShareFileToGroup(file, grp._id);
                }
                else
                    System.out.println("File not exists..");

            }
            else if (choice.equals("0"))
            {
                break;
            }
            else
            {
                System.out.println("Choose number between 0-9");
            }
        }
    }

    public static void printGroups(List<GroupModel> list)
    {
        int i = 0;
        if (list != null)
        {
            for (i = 0; i < list.size(); i++) {
                System.out.println(Integer.toString(i + 1) + ". " + list.get(i).groupName);
                System.out.println(list.get(i).participants);
                        /*List<String> par = list.get(i).participants;
                        for (j = 0; j < par.size(); j++)
                        {
                            System.out.println(par.get(j));
                        }*/
                System.out.println("");
            }
        }
    }

    public static GroupModel chooseGroup(List<GroupModel> list) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String choice = "10";

        if (list == null)
        {
            return null;
        }

        while (Integer.parseInt(choice) > list.size() || Integer.parseInt(choice) < 1 )
        {
            System.out.println("Choose Group: ");
            printGroups(list);

            choice = br.readLine();
        }

        return list.get(Integer.parseInt(choice) - 1);
    }

    public static void menuGroups(Database db) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String choice = "10", name = "", type = "", name2 = "", storage;
        int i = 0, j = 0;
        //setup
        String imagePath = "";
        int splits = 3;
        int duplicates = 2;

        while (Integer.parseInt(choice) > 9 || Integer.parseInt(choice) < 0)
        {
            System.out.println("MENU FILES ACTIONS");
            System.out.println("0. EXIT");
            System.out.println("1. Print Groups");
            System.out.println("2. Create Group");
            System.out.println("3. Invite To Group");
            System.out.println("4. Leave Group");
            System.out.println("5. Change Group Name");
            System.out.println("6. Kick Member Of The Group");
            System.out.println("7. Accept Request To Join The Group");
            System.out.println("8. Reject Request To Join The Group");
            //System.out.println("9. Delete Group");
            choice = br.readLine();
            if (choice.equals("1"))
            {
                List<GroupModel> list =  db.getGroupsByUser(onlineUser.email);
                printGroups(list);
            }
            else if (choice.equals("2"))
            {
                //check there no double names by same owner
                List<GroupModel> list = db.getGroupsByOwner(onlineUser.email);

                GroupModel grp = new GroupModel();

                System.out.println("Enter Group Name: ");
                grp.groupName = br.readLine();
                grp._id = generateString();
                grp.participants.add(onlineUser.email);

                if (list != null)
                {
                    for (i = 0; i < list.size(); i++)
                    {
                        if (list.get(i).groupName.equals(grp.groupName))
                        {
                            System.out.println("You already have group called " + grp.groupName);
                            break;
                        }
                    }
                }

                db.insertGroup(grp);
            }
            else if (choice.equals("3"))
            {
                List<GroupModel> list =  db.getGroupsByOwner(onlineUser.email);
                GroupModel grp = chooseGroup(list);
                if (grp == null)
                {
                    System.out.println("User don't have groups");
                    break;
                }

                System.out.println("Enter User Email To Share: ");
                name = br.readLine();

                db.inviteToGroup(grp, name);
            }
            else if (choice.equals("4"))
            {
                List<GroupModel> list =  db.getGroupsByUser(onlineUser.email);
                GroupModel grp = chooseGroup(list);
                if (grp == null)
                {
                    System.out.println("User don't shared by any group");
                    break;
                }
                db.leaveGroup(grp, onlineUser.email);
            }
            else if (choice.equals("5"))
            {
                List<GroupModel> list =  db.getGroupsByOwner(onlineUser.email);
                GroupModel grp = chooseGroup(list);
                if (grp == null)
                {
                    System.out.println("User don't have groups");
                    break;
                }

                System.out.println("Enter New Name: ");
                name = br.readLine();

                db.ChangeGroupName(grp, name);
            }
            else if (choice.equals("6"))
            {
                List<GroupModel> list =  db.getGroupsByOwner(onlineUser.email);
                GroupModel grp = chooseGroup(list);
                if (grp == null)
                {
                    System.out.println("User don't have groups");
                    break;
                }

                System.out.println("Enter User Email To Kick: ");
                name = br.readLine();

                db.kickFromGroup(grp, name);
            }
            else if (choice.equals("7"))
            {
                List<GroupModel> list =  db.getGroupsByUserInvited(onlineUser.email);
                GroupModel grp = chooseGroup(list);
                if (grp == null)
                {
                    System.out.println("User don't have an requests");
                    break;
                }
                db.acceptGroup(grp, onlineUser.email);
            }
            else if (choice.equals("8"))
            {
                List<GroupModel> list =  db.getGroupsByUserInvited(onlineUser.email);
                GroupModel grp = chooseGroup(list);
                if (grp == null)
                {
                    System.out.println("User don't have an requests");
                    break;
                }
                db.rejectGroup(grp, onlineUser.email);
            }
            else if (choice.equals("0"))
            {
                break;
            }
            else
            {
                System.out.println("Choose number between 0-9");
            }
        }
    }

    public static void menuUsers(Database db, String[] listDrives) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String choice = "10", name = "", storage;
        //String email = "", username = "", password = "";

        while (Integer.parseInt(choice) > 8 || Integer.parseInt(choice) < 0)
        {
            System.out.println("MENU USER ACTIONS");
            System.out.println("0. EXIT");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Logout");
            System.out.println("4. Rename");
            System.out.println("5. Storage");
            //System.out.println("6. Change password");   //by email
            //System.out.println("7. Delete current user");   //with his files
            System.out.println("8. Files");
            System.out.println("9. Groups");
            choice = br.readLine();
            if (choice.equals("1"))
            {
                UserModel user = new UserModel();

                System.out.println("Enter Email: ");
                user.email = br.readLine();

                System.out.println("Enter Username: ");
                user.username = br.readLine();

                System.out.println("Enter Password: ");
                user.password = br.readLine();

                user.StorageSize = normalSize;

                db.insertUser(user);
                onlineUser = db.getUser(user.email, user.password);
            }
            else if (choice.equals("2"))
            {
                UserModel user = new UserModel();

                System.out.println("Enter Email: ");
                user.email = br.readLine();

                System.out.println("Enter Password: ");
                user.password = br.readLine();

                onlineUser = db.getUser(user.email, user.password);
            }
            else if (choice.equals("3"))
            {
                onlineUser = null;
            }
            else if (choice.equals("4"))
            {
                System.out.println("Enter New Username: ");
                name = br.readLine();

                db.ChangeUserName(onlineUser, name);
                onlineUser = db.getUser(onlineUser.email, onlineUser.password);
            }
            else if (choice.equals("5"))
            {
                storage = db.getUserStorageAvailable(onlineUser);
                System.out.println("Max capacity: " + onlineUser.StorageSize + "KB");
                System.out.println("Available storage: " + storage + "KB");
            }
            else if (choice.equals("8"))
            {
                menuFiles(listDrives, db);
            }
            else if (choice.equals("9"))
            {
                menuGroups(db);
            }
            else if (choice.equals("0"))
            {
                break;
            }
            else
            {
                System.out.println("Choose number between 0-8");
            }
        }


    }

    public static void menuServers(Database db) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String choice = "10", name = "", storage;

        while (Integer.parseInt(choice) > 2 || Integer.parseInt(choice) < 0)
        {
            System.out.println("MENU SERVER ACTIONS");
            System.out.println("0. EXIT");
            System.out.println("1. Connect New Server");
            System.out.println("2. Get Connected Servers");

            choice = br.readLine();
            if (choice.equals("1"))
            {
                //gets the id and ip from the server
                ConnectedServerModel srv = new ConnectedServerModel();
                srv._id = generateString();
                srv.ip = "10.0.0.59";

                db.insertServer(srv);
            }
            else if (choice.equals("2"))
            {
                List<ConnectedServerModel> lst = new ArrayList<>();

                lst = db.getServers();
            }

            else if (choice.equals("0"))
            {
                break;
            }
            else
            {
                System.out.println("Choose number between 0-2");
            }
        }


    }

    public static void menu(String[] listDrives, Database db) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String choice = "10";

        while (Integer.parseInt(choice) != 0)
        {
            System.out.println("MENU");
            System.out.println("0. EXIT");
            System.out.println("1. Setup");
            System.out.println("2. Print Files");
            System.out.println("3. Print Users");
            System.out.println("4. Print Groups");
            System.out.println("5. Print Servers");
            System.out.println("6. Users");
            System.out.println("7. Servers");
            System.out.println("8. Delete DB");
            choice = br.readLine();
            if (choice.equals("1"))
            {
                //adding disk drives
                setup();
                String[] list = checkSetup();
            }
            else if (choice.equals("2"))
            {
                //files list
               db.printFilesDB();
            }
            else if (choice.equals("3"))
            {
                //files list
                db.printUsersDB();
            }
            else if (choice.equals("4"))
            {
                //files list
                db.printGroupsDB();
            }
            else if (choice.equals("5"))
            {
                //servers list
                db.printServersDB();
            }
            else if (choice.equals("6"))
            {
                //users menu
                menuUsers(db, listDrives);
            }
            else if (choice.equals("7"))
            {
                //menu servers
                menuServers(db);
            }
            else if (choice.equals("8"))
            {
                //prints the files in the database
                db.deleteDB();
            }
            else if (choice.equals("0"))
            {
                break;
            }
            else
            {
                System.out.println("Choose number between 0-7");
            }
        }
    }


    public static void main(String[] args) throws Exception
    {
        System.out.println("Welcome to our cloud server!");
        Database db = new Database();

        String[] list = checkSetup();

        if (list[0] != null)
        {
          menu(list, db);
        }
        else
        {
          setup();
          list = checkSetup();
          menu(list, db);
        }

    }


}
