package server;

public class UploadPayload
{

    String _inputPath = "";
    String _fileName = "";
    int _splits = 0;
    int _duplicates = 0;
    String[] _listDrives;
    String _capacity = "";
    UserModel _onlineUser;

    UploadPayload(String inputPath, String fileName, int splits, int duplicates, String[] listDrives, String capacity, UserModel onlineUser)
    {
        _inputPath = inputPath;
        _fileName = fileName;
        _splits = splits;
        _duplicates = duplicates;
        _listDrives = listDrives;
        _capacity = capacity;
        _onlineUser = onlineUser;
    }

    UploadPayload()
    {
        _inputPath = "";
        _fileName = "";
        _splits = 0;
        _duplicates = 0;
        _listDrives = new String[100];
        _capacity = "";
        _onlineUser = new UserModel();
    }

};

