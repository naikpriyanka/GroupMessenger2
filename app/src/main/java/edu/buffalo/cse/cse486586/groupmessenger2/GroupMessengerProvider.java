package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import edu.buffalo.cse.cse486586.groupmessenger2.data.GroupMessengerDbHelper;

import static edu.buffalo.cse.cse486586.groupmessenger2.data.GroupMessengerContract.GroupMessengerEntry.KEY_FIELD;
import static edu.buffalo.cse.cse486586.groupmessenger2.data.GroupMessengerContract.GroupMessengerEntry.TABLE_NAME;
import static edu.buffalo.cse.cse486586.groupmessenger2.data.GroupMessengerContract.GroupMessengerEntry.VALUE_FIELD;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {

    public static final String LOG_TAG = GroupMessengerProvider.class.getSimpleName();

    private GroupMessengerDbHelper mDbHelper;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * COMPLETED: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that we used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */
        // Check that the key is not null
        String key = values.getAsString(KEY_FIELD);
        if (key == null) {
            throw new IllegalArgumentException("Message requires a key");
        }

        // Check that the value is not null
        String value = values.getAsString(VALUE_FIELD);
        if (value == null) {
            throw new IllegalArgumentException("Message requires a value");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        /*
         * Insert the new message with the given values
         *
         * Since the table can already have the same key used insertWithOnConflict
         * The existing value for that key will be replaced with new value
         */
        long id = database.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the message content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
        mDbHelper = new GroupMessengerDbHelper(getContext());
        return true;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        /*
         * COMPLETED: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         *
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         *
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        /*
         * Since the selection variable has the key value to search, filled selectionArgs with value
         * from selection
         *
         * Changed the selection to query value containing equal to and question mark
         */
        selectionArgs = new String[]{selection};
        selection = KEY_FIELD + "=?";

        /*
         * This cursor will hold the result of the query
         *
         * Query the message table directly with the given projection, selection, selection
         * arguments, and sort order. The cursor contains single row of the messages table.
         */
        Cursor resultCursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        resultCursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.v("query", selectionArgs[0]);
        return resultCursor;
    }
}
