package dbl.tue.framework;

import com.quickblox.users.model.QBUser;

/**
 * Created by s140878 on 16-3-2016.
 */
public class Globals {
    private static Globals instance;
    QBUser currentuser;

    public QBUser getCurrentuser() {
        return currentuser;
    }

    public void setCurrentuser(QBUser currentuser) {
        this.currentuser = currentuser;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
