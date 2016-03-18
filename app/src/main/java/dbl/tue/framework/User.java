package dbl.tue.framework;

/**
 * Created by s140878 on 9-3-2016.
 */
public class User {

    int UserID;
    String bio;

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
