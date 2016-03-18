package dbl.tue.framework;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by s140878 on 9-3-2016.
 */
public class LocalMessageDB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LocalMessageDB.db";

    public static final String TABLE_NAME = "Messages";
    public static final String COLUMN_NAME_ENTRY_ID = "entry";
    public static final String COLUMN_NAME_CURRENTUSER = "currentuser";
    public static final String COLUMN_NAME_OPPONENT = "opponent";
    public static final String COLUMN_NAME_DATA = "data";
    public static final String COLUMN_NAME_SENDBY = "sender";
    public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    public static final String COLUMN_NAME_NULLABLE="NULLHACK";


    private static final String TEXT_TYPE = " TEXT";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LocalMessageDB.TABLE_NAME + " (" +
                   // LocalMessageDB.COLUMN_NAME_ENTRY_ID + INT_TYPE + COMMA_SEP +
                    LocalMessageDB.COLUMN_NAME_CURRENTUSER + INT_TYPE + COMMA_SEP +
                    LocalMessageDB.COLUMN_NAME_OPPONENT + INT_TYPE + COMMA_SEP +
                    LocalMessageDB.COLUMN_NAME_DATA + TEXT_TYPE + COMMA_SEP +
                    LocalMessageDB.COLUMN_NAME_SENDBY + INT_TYPE + COMMA_SEP +
                    LocalMessageDB.COLUMN_NAME_TIMESTAMP + DOUBLE_TYPE +
                    LocalMessageDB.COLUMN_NAME_NULLABLE+ TEXT_TYPE+

                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LocalMessageDB.TABLE_NAME;

    public LocalMessageDB(Context context) {
        super(context, LocalMessageDB.DATABASE_NAME, null, LocalMessageDB.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
