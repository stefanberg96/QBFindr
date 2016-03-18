package dbl.tue.framework;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by s140878 on 9-3-2016.
 */
public class Message {

    User user1;
    User opponent;
    double timestamp;
    String data;
    User sendBy;

    public Message(User user1, User opponent, String data, Context context, User sendBy) {
        this.user1 = user1;
        this.opponent = opponent;
        this.timestamp = System.currentTimeMillis();
        this.data = data;
        this.sendBy=sendBy;
        Send(context);
    }

    public User getUser1() {
        return user1;
    }

    public User getOpponent() {
        return opponent;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public String getData() {
        return data;
    }

    public void Send(Context context){

        LocalMessageDB dbHelper=new LocalMessageDB(context);
        SQLiteDatabase db= dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocalMessageDB.COLUMN_NAME_CURRENTUSER, user1.getUserID());
        values.put(LocalMessageDB.COLUMN_NAME_OPPONENT, opponent.getUserID());
        values.put(LocalMessageDB.COLUMN_NAME_DATA, data);
        values.put(LocalMessageDB.COLUMN_NAME_SENDBY,sendBy.getUserID() );
        values.put(LocalMessageDB.COLUMN_NAME_TIMESTAMP,timestamp);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                LocalMessageDB.TABLE_NAME,
                LocalMessageDB.COLUMN_NAME_NULLABLE,
                values);
    }
}
