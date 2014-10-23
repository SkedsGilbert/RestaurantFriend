package com.jobbolster.restaurantfriend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by Jsin on 8/11/2014.
 */
public class DBAdapter {

    private static final String TAG = "DBAdapter";
    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_ACTIVE = "ACTIVE";
    //RESTAURANT TABLE
    public static final String KEY_RESTAURANT_NAME = "NAME";
    //LOCATIONS TABLE
    public static final String KEY_RESTAURANT_LOCALE = "NAME";
    //RESTAURANTS_HAVE_LOCATIONS
    public static final String KEY_RESTAURANT_HAVE_ID = "RESTAURANT_ID";
    public static final String KEY_LOCATIONS_HAVE_ID = "LOCATION_ID";
    //SERVER TABLE
    public static final String KEY_SERVER_NAME = "NAME";
    public static final String KEY_SERVER_REST_HAVE_LOCATION = "REST_HAVE_LOC_ID";
    public static final String KEY_SERVER_NOTES = "NOTES";
    public static final String KEY_SERVER_SCORE = "SCORE";
    public static final String KEY_SERVER_SCORE_COUNT = "SCORE_COUNT";

    // TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
    public static final int COL_RESTAURANT_NAME = 1;
    public static final int COL_RESTAURANT_LOCALE = 2;
    public static final int COL_SERVER_NAME = 3;

    public static final String[] ALL_KEYS_NAMES = new String[] {KEY_ROW_ID, KEY_RESTAURANT_NAME};

    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "ServerInfoDb";
    public static final String DATABASE_TABLE_RESTAURANT_NAME = "RESTAURANT_NAME";
    public static final String DATABASE_TABLE_RESTAURANTS_HAVE_LOCATIONS = "RESTAURANT_HAVE_LOCATIONS";
    public static final String DATABASE_TABLE_LOCATIONS = "LOCATIONS";
    public static final String DATABASE_TABLE_SERVER = "SERVERS";

    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_RESTAURANT_TABLE =
            "create table " + DATABASE_TABLE_RESTAURANT_NAME
                    + " ("+ KEY_ROW_ID + " integer primary key autoincrement, "
                    + KEY_ACTIVE + " boolean default 'true', "
                    + KEY_RESTAURANT_NAME + " string not null "
                    + ");";

    private static final String DATABASE_CREATE_LOCATIONS_TABLE =
            "create table " + DATABASE_TABLE_LOCATIONS
                    +" (" + KEY_ROW_ID + " integer primary key autoincrement, "
                    + KEY_RESTAURANT_LOCALE + " string not null "
                    + ");";

    private static final String DATABASE_CREATE_RESTAURANTS_HAVE_LOCATION =
            "create table " + DATABASE_TABLE_RESTAURANTS_HAVE_LOCATIONS
                    +" (" + KEY_ROW_ID + " integer primary key autoincrement, "
                    + KEY_RESTAURANT_HAVE_ID + " integer, "
                    + KEY_LOCATIONS_HAVE_ID + " integer, "
                    + "foreign key" + " (" + KEY_RESTAURANT_HAVE_ID + ") references " + DATABASE_TABLE_RESTAURANT_NAME
                    + " (" + KEY_ROW_ID + "), "
                    + "foreign key" + " (" + KEY_LOCATIONS_HAVE_ID + ") references " + DATABASE_TABLE_LOCATIONS
                    + " (" + KEY_ROW_ID + ") "
                    + ");";

    private static final String DATABASE_CREATE_SERVERS_TABLE =
            "create table " + DATABASE_TABLE_SERVER
                    +" (" + KEY_ROW_ID + " integer primary key autoincrement, "
                    + KEY_SERVER_NAME + " string not null, "
                    + KEY_SERVER_REST_HAVE_LOCATION + " string,"
                    + KEY_SERVER_SCORE + " real, "
                    + KEY_SERVER_SCORE_COUNT + " integer, "
                    + KEY_SERVER_NOTES + " text, "
                    + "foreign key" + " (" + KEY_SERVER_REST_HAVE_LOCATION + ") references " + DATABASE_TABLE_RESTAURANT_NAME
                    + " (" + KEY_ROW_ID + ") "
                    + ");";

    // Context of application who uses us.

    private Context mContext;
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    /////////////////////////////////////////////////////////////////////
    //	Public methods:
    /////////////////////////////////////////////////////////////////////

    public DBAdapter(Context ctx){
        this.mContext = ctx;
        myDBHelper = new DatabaseHelper(mContext);
    }

    // Open the database connection.
    public DBAdapter openWrite() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    public DBAdapter openRead(){
        db = myDBHelper.getReadableDatabase();
        return this;
    }

    // Close the database connection.
    public void closeDB() {
        myDBHelper.close();
    }

    public Long insertRestName(String restName){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RESTAURANT_NAME,restName);
        return db.insert(DATABASE_TABLE_RESTAURANT_NAME,null,initialValues);
    }

    public Cursor getRestID(String restName){
        String getIDQuery = "SELECT " + KEY_ROW_ID + " FROM " + DATABASE_TABLE_RESTAURANT_NAME
                + " WHERE " + KEY_RESTAURANT_NAME + " = \"" + restName + "\";";
        Cursor c = db.rawQuery(getIDQuery,null);
        if (c != null){
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getAllRestName(){
        String isTrue = "true";
        String nameQuery = "SELECT * FROM " + DATABASE_TABLE_RESTAURANT_NAME
                +" WHERE " + KEY_ACTIVE + " =  \"" + isTrue + "\""
                + " ORDER BY " + KEY_RESTAURANT_NAME + " ASC";
        Cursor c = db.rawQuery(nameQuery, null );
        if (c != null){
            c.moveToFirst();
        }
        return c;
    }

    public Long insertLocale(String locale){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RESTAURANT_LOCALE,locale);
        return db.insert(DATABASE_TABLE_LOCATIONS,null,initialValues);
    }

    public Cursor getLocaleID(String localeName){
        String getIDQuery = "SELECT " + KEY_ROW_ID + " FROM " + DATABASE_TABLE_LOCATIONS
                + " WHERE " + KEY_RESTAURANT_LOCALE + " = \"" + localeName + "\";";
        Cursor c = db.rawQuery(getIDQuery,null);
        if (c != null){
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getAllLocations(){
        String locationQuery = "SELECT * FROM " + DATABASE_TABLE_LOCATIONS + " ORDER BY "
                + KEY_RESTAURANT_LOCALE + " ASC";
        Cursor c = db.rawQuery(locationQuery,null);
        if (c != null){
            c.moveToFirst();
        }
        return c;
    }

    public Long insertServer(String serverName, String restLocID){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SERVER_NAME,serverName);
        initialValues.put(KEY_SERVER_REST_HAVE_LOCATION,restLocID);
        initialValues.put(KEY_SERVER_SCORE,0.0);
        initialValues.put(KEY_SERVER_SCORE_COUNT,0);
        return db.insert(DATABASE_TABLE_SERVER,null,initialValues);
    }

    public Cursor getAllServer(String restLocID){
        String serverQuery = "SELECT * FROM " + DATABASE_TABLE_SERVER
                + " WHERE " + KEY_SERVER_REST_HAVE_LOCATION + " = \"" + restLocID + "\""
                + " ORDER BY " + KEY_SERVER_NAME + " ASC";
        Cursor c = db.rawQuery(serverQuery,null);
        if (c != null){
            c.moveToFirst();
        }
        return c;
    }

    public Long insertRestIdLocaleID(String restID, String locationID ){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RESTAURANT_HAVE_ID,restID);
        initialValues.put(KEY_LOCATIONS_HAVE_ID,locationID);
        return db.insert(DATABASE_TABLE_RESTAURANTS_HAVE_LOCATIONS,null,initialValues);
    }

    public Cursor getRestLocID(String restID, String locID){
        String getIDsQuery = "SELECT " + KEY_ROW_ID + " FROM " + DATABASE_TABLE_RESTAURANTS_HAVE_LOCATIONS
                + " WHERE " + KEY_RESTAURANT_HAVE_ID + " = \"" + restID + "\""
                + " AND " + KEY_LOCATIONS_HAVE_ID + " = \"" + locID + "\";";
        Cursor c = db.rawQuery(getIDsQuery,null);
        if(c != null){
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getServerScore(String id){
        String getServerIDQuery = "SELECT " + KEY_SERVER_SCORE + " FROM " + DATABASE_TABLE_SERVER
                + " WHERE " + KEY_ROW_ID + " = \"" + id +"\";";
        Cursor c = db.rawQuery(getServerIDQuery,null);
        if(c != null){
            c.moveToFirst();
        }
        return c;
    }

    public void updateServerNotes(String id, String notes){
        String updateNotesQuery = "UPDATE " + DATABASE_TABLE_SERVER + " SET "
                + KEY_SERVER_NOTES + " = \"" + notes + "\" WHERE " + KEY_ROW_ID + " = \""
                + id +"\";";
       db.execSQL(updateNotesQuery);
    }

    public void updateServerScore(String id, float rating){
        String updateScoreQuery = "UPDATE " + DATABASE_TABLE_SERVER + " SET "
                + KEY_SERVER_SCORE + " = \"" + rating + "\" WHERE " + KEY_ROW_ID + " = \""
                + id +"\";";
        db.execSQL(updateScoreQuery);
    }

    public Cursor getServerNotes(String id){
        String getNotesQuery = "SELECT " + KEY_SERVER_NOTES + " FROM " + DATABASE_TABLE_SERVER
                + " WHERE " + KEY_ROW_ID + " = \"" + id + "\";";
        Cursor c = db.rawQuery(getNotesQuery,null);
        if(c != null){
            c.moveToFirst();
        }
        return c;
    }




    /////////////////////////////////////////////////////////////////////
    //	Private Helper Classes:
    /////////////////////////////////////////////////////////////////////

    /**
     * Private class which handles database creation and upgrading.
     * Used to handle low-level database access.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {

            _db.execSQL(DATABASE_CREATE_RESTAURANT_TABLE);
            _db.execSQL(DATABASE_CREATE_LOCATIONS_TABLE);
            _db.execSQL(DATABASE_CREATE_RESTAURANTS_HAVE_LOCATION);
            _db.execSQL(DATABASE_CREATE_SERVERS_TABLE);


        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_RESTAURANT_NAME);
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_LOCATIONS);
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_RESTAURANTS_HAVE_LOCATIONS);
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_SERVER);

            // Recreate new database:
            onCreate(_db);
        }
    }
}
