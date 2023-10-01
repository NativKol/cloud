package subserver;

import subserver.Database;

import java.util.Arrays;
import java.util.UUID;

public class App {

    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }
    public static void main(String[] args) throws Exception
    {
        System.out.println("Welcome to our sub server!");
        Database db = new Database();
        db.deleteDB();
        FileModel file = new FileModel();
        file._id = generateString();

        db.insertFile(file);
        db.printFilesDB();

        db.addRegAddress(file, "C:\\CloudStorage\\11binaryFilee721dd72-a3e8-4bad-96aa-f49dac2e82ac");
        db.addRegAddress(file, "C:\\CloudStorage\\12binaryFilee721dd72-a3e8-4bad-96aa-f49dac2e82ac");
        db.addRegAddress(file, "C:\\CloudStorage\\13binaryFilee721dd72-a3e8-4bad-96aa-f49dac2e82ac");

        db.addCopyAddress(file, "lol");
        db.addCopyAddress(file, "ok");
        db.addCopyAddress(file, "egg");

        //db.insertFile(file);
        db.printFilesDB();

        FileModel newFile = new FileModel();

        newFile = db.getFileByID(file._id);
        //newFile = db.getFileByID("dsa");

        if (newFile != null)
        {
            System.out.println("After: " + newFile._id);
            System.out.println(Arrays.toString(newFile.adr.toArray()));
            System.out.println(Arrays.toString(newFile.adrCopy.toArray()));
        }

    }
}
