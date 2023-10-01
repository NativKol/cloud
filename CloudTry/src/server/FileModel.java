package server;

import java.util.ArrayList;
import java.util.*;

public class FileModel {
    public String _id = "";
    public String file_name = "";
    public String date = "";
    public String star = "false";
    public String type = "";
    public String  sizeFile = "";
    public List<PathModel> adr = new ArrayList<PathModel>();
    public List<PathModel> adrCopy = new ArrayList<PathModel>();
    public List<String> sharedUsers = new ArrayList<>();
    public List<String> sharedGroups = new ArrayList<>();
}
