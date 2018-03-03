package edu.buffalo.cse.cse486586.groupmessenger2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static edu.buffalo.cse.cse486586.groupmessenger2.data.GroupMessengerContract.GroupMessengerEntry.KEY_FIELD;
import static edu.buffalo.cse.cse486586.groupmessenger2.data.GroupMessengerContract.GroupMessengerEntry.TABLE_NAME;
import static edu.buffalo.cse.cse486586.groupmessenger2.data.GroupMessengerContract.GroupMessengerEntry.VALUE_FIELD;

/**
 * Created by priyankanaik on 12/02/2018.
 */

public class GroupMessengerDbHelper extends SQLiteOpenHelper {

    /*
     * Name of the database file
     */
    private static final String DATABASE_NAME = "messages.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 5;

    /**
     * Constructs a new instance of {@link GroupMessengerDbHelper}.
     *
     * @param context of the app
     */
    public GroupMessengerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the messages table
        String SQL_CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + KEY_FIELD + " TEXT NOT NULL UNIQUE, "
                + VALUE_FIELD + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop the old table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //Create the table again
        onCreate(db);
    }
}
