package server;

import com.mongodb.BasicDBObject;

public interface UsersDB
{
    /*
    Static function that get the object of user and convert it to the type of basicDBObject
    soo it can be inserted into the database
     */
    public static BasicDBObject convert(UserModel user)
    {
        BasicDBObject basicDBObject = new BasicDBObject();

        basicDBObject.append("email", user.email).append("username", user.username).
                append("password", user.password).append("StorageSize", user.StorageSize);
        return basicDBObject;
    }
    /*
    Query that returns if the user exists by the email
     */
    public boolean checkUserExists(String email);
    /*
    Function that takes as parameter the user object and insert it into the database
     */
    public void insertUser(UserModel user);
    /*
    Query that returns the user by the email and the password
     */
    public UserModel getUser(String email, String password);
    /*
    Query that change the username
     */
    public void ChangeUserName(UserModel user, String newName);
    /*
    Query that return the amount of storage available of user
     */
    public String getUserStorageAvailable(UserModel user);
    /*
    Function that prints the collection of users
     */
    public void printUsersDB();

    //delete user
}
