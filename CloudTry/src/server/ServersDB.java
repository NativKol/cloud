package server;

import com.mongodb.BasicDBObject;

import java.util.List;

public interface ServersDB {

    /*
  Static function that get the object of Server and convert it to the type of basicDBObject
  soo it can be inserted into the database
   */
    public static BasicDBObject convert(ConnectedServerModel srv)
    {
        BasicDBObject basicDBObject = new BasicDBObject();

        basicDBObject.append("_id", srv._id).append("ip", srv.ip);
        return basicDBObject;
    }

    /*
    Function that takes as parameter the server object and insert it into the database
     */
    public void insertServer(ConnectedServerModel srv);

    /*
    get the servers saved in the collection
     */
    public List<ConnectedServerModel> getServers();

    /*
    Prints the collection of servers
     */
    public void printServersDB();
}
