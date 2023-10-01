import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import javax.imageio.*;
import javax.swing.filechooser.FileSystemView;

public class App {
  private static final String Path = "D:\\magshimim\\Y3\\cloud\\galilmaaravi-603-cloud\\Cloud-sprint_1\\";

  public static byte[] substring(byte[] array, int start, int end)
   {
    if (end <= start)
        return null;
    int length = (end - start);

    byte[] newArray = new byte[length];
    System.arraycopy(array, start, newArray, 0, length);
    return newArray;
  }

  public static void saveBytesFiles(String inputPath, String fileName, int splits, int duplicates, String[] listDrives)
  {
    int i = 0, j = 0, pos = 0;
    int slice = listDrives.length;
    String path = "";
    try {
      BufferedImage sourceimage = ImageIO.read(new File(inputPath));
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      ImageIO.write(sourceimage, "jpg", bytes);

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
          path = listDrives[pos] + "/" +  String.valueOf(j) + "binaryImage"  + fileName + String.valueOf(i) + ".txt";
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
  }

  public static void getBytesFromFiles(String inputPath, String fileName, int splits, int duplicates, String[] listDrives)
  {
    int i = 0, j = 0, pos = 0;
    int slice = listDrives.length;
    String path = "";
    ByteArrayOutputStream outputFile = new ByteArrayOutputStream();
    try 
    {
      //get from file
      //for (j = 0; j < duplicates; j++)
      for (j = 0; j < 1; j++)
      {
      //save to file
        for (i = 0; i < splits; i++)
        {
          pos = (i / slice) + j; 
          if (pos >= slice)
          {
            pos = 0;
          }
          path = listDrives[pos] + "/" +  String.valueOf(j) + "binaryImage"  + fileName + String.valueOf(i) + ".txt";
          InputStream inputData = new FileInputStream(path);
          inputData.transferTo(outputFile);
          inputData.close();
          //System.out.println(path + " Saved");

        }
      }

      createFile(outputFile, fileName);

    }catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void createFile(ByteArrayOutputStream outputFile, String fileName)
  {
    try
    {
      //decode
      ByteArrayInputStream bais = new ByteArrayInputStream(outputFile.toByteArray());
      //save new
      BufferedImage image = ImageIO.read(bais);
      ImageIO.write(image, "jpg", new File(Path + "imageOut" + fileName +  ".jpg"));
      System.out.println("Saved");

      outputFile.close();
    }catch (Exception e) {
      e.printStackTrace();
    }
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

  public static void menu(String[] listDrives) throws IOException
  {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String choice = "", name = "";
    //setup
    String imagePath = "";
    int splits = 3;
    int duplicates = 2;

    while (!(choice.equals("1")) || (choice.equals("2")) || (choice.equals("3")))
    {
      System.out.println("MENU");
      System.out.println("0. EXIT");
      System.out.println("1. Setup");
      System.out.println("2. Uplaod");   
      System.out.println("3. Download"); 
      choice = br.readLine();
      if (choice.equals("1"))
      {
        //adding disk drives
        setup();
        String[] list = checkSetup();
        menu(list);
      }
      else if (choice.equals("2"))
      {
        //upload files to server
        System.out.println("Enter File Path: ");
        imagePath = br.readLine();

        System.out.println("Enter File Name: ");
        name = br.readLine();

        //save the image to bytes files
        saveBytesFiles(imagePath, name, splits * listDrives.length, duplicates, listDrives);
      }
      else if (choice.equals("3"))
      {
        //download photo by name
        System.out.println("Enter File Path For Save: ");
        imagePath = br.readLine();

        System.out.println("Enter File Name To Search: ");
        name = br.readLine();

            
        //save the image by the bytes
        getBytesFromFiles(imagePath, name, splits * listDrives.length, duplicates, listDrives);
    
      }
      else if (choice.equals("0"))
      {
        break;
      }
      else
      {
        System.out.println("Choose number between 1-3"); 
      }
    }
  

  }

  public static void main(String[] args) throws Exception
  {
    System.out.println("Welcome to our cloud server!"); 
    String[] list = checkSetup();

    if (list[0] != null)
    {
      menu(list);
    }
    else
    {
      setup();
      list = checkSetup();
      menu(list);
    }
  }
}
