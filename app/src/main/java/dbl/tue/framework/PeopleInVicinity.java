package dbl.tue.framework;

import android.os.Parcel;
import android.os.Parcelable;

import com.quickblox.users.model.QBUser;

import java.io.Serializable;

/**
 * Created by s140878 on 19-3-2016.
 */
public class PeopleInVicinity implements Serializable{

    QBUser user;
    double distance;

    public PeopleInVicinity(QBUser user, double distance) {
        this.user = user;
        this.distance = distance;
    }



}
