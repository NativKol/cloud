package server;

import java.io.*;
import java.net.Inet4Address;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileSystem
{

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
    public static FileModel saveBytesFiles(UploadPayload pay)
    {
        int i = 0, j = 0, pos = 0, read = 0;
        int slice = pay._listDrives.length;
        int size = 0;
        String path = "", storage = "";

        byte[] buff = new byte[10000];

        FileModel file = new FileModel();
        file.file_name = pay._fileName;


        try {
            InputStream sourcefile = new FileInputStream(pay._inputPath);
            //get the size of file
            InputStream sourcefileSize = new FileInputStream(pay._inputPath);
            size = sourcefileSize.readAllBytes().length;
            System.out.println(size/1000 + " KB");

            file.sizeFile = String.valueOf(size);

            //you don't have enough storage
            if (Integer.parseInt(file.sizeFile) > Integer.parseInt(pay._capacity))
            {
                System.out.println("you don't have enough storage space");
                System.out.println("File size: " + file.sizeFile + "KB");
                System.out.println("Available storage: " + storage + "KB");
                return null;
            }

            file._id = generateString();
            file.sharedUsers.add(pay._onlineUser.email);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            while((read = sourcefile.read(buff)) != -1) {
                bytes.write(buff, 0, read);
            }

            //adding copies
            for (j = 0; j < pay._duplicates; j++)
            {
                //save to file
                for (i = 0; i < pay._splits; i++)
                {
                    byte[] saveFile = new byte[bytes.toByteArray().length];
                    pos = (i / slice) + j;
                    if (pos >= slice)
                    {
                        pos = 0;
                    }
                    path = pay._listDrives[pos] + "/" +  String.valueOf(j) + String.valueOf(i) + "binaryFile_"  + file._id + ".txt";
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
                    int skip = bytes.toByteArray().length / pay._splits;
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

        String type = pay._inputPath.substring(pay._inputPath.length() - 6, pay._inputPath.length());
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
    Save File
     */
    public static void saveFile(byte[] file) throws IOException {
        //byte[] file = data.getBytes();

        File savePath = new File("D:\\magshimim\\Y3\\cloud\\galilmaaravi-603-cloud\\Cloud-sprint_1\\mc.docx");
        Files.write(savePath.toPath(), file);

        System.out.println("Saved!");

    }



    /*
    Function that gets the file model and delete the file from all the drives
     */
        /*
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
    */

}
