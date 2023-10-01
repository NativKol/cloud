package server;

import java.io.IOException;

public interface RequestHandleFile
{
    public String handleFileRequest(UserModel usr, FileModel f) throws IOException;
    public String handlePreFileRequest() throws IOException;

    public String handleUploadRequest(byte[] data) throws IOException;
}
