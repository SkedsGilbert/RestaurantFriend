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
    public static final String KEY_ROWID = "_id";
    //RESTAURANT TABLE
    public static final String KEY_RESTAURANT_NAME = "NAME";
    //LOCATIONS TABLE
    public static final String KEY_RESTAURANT_LOCALE = "NAME";
    //RESTAURANTS_HAVE_LOCATIONS
    public static final String KEY_RESTAURANT_HAVE_ID = "RESTAURANT_ID";
    public static final String KEY_LOCATIONS_HAVE_ID = "LOCATION_ID";
    //SERVER TABLE
    public static final String KEY_SERVER_NAME = "NAME";
    public static final String KEY_SERVER_LOCATION = "LOCATION_ID";

    // TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
    public static final int COL_RESTAURANT_NAME = 1;
    public static final int COL_RESTAURANT_LOCALE = 2;
    public static final int COL_SERVER_NAME = 3;

    public static final String[] ALL_KEYS_NAMES = new String[] {KEY_ROWID, KEY_RESTAURANT_NAME};

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
                    + " ("+ KEY_ROWID +" integer primary key autoincrement, "
                    + KEY_RESTAURANT_NAME +" string not null "
                    + ");";

    private static final String DATABASE_CREATE_LOCATIONS_TABLE =
            "create table " + DATABASE_TABLE_LOCATIONS
                    +" (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_RESTAURANT_LOCALE + " string not null "
                    + ");";

    private static final String DATABASE_CREATE_RESTAURANTS_HAVE_LOCATION =
            "create table " + DATABASE_TABLE_RESTAURANTS_HAVE_LOCATIONS
                    +" (" + KEY_RESTAURANT_HAVE_ID + " integer, "
                    + KEY_LOCATIONS_HAVE_ID + " integer, "
                    + "foreign key" + " (" + KEY_RESTAURANT_HAVE_ID + ") references " + DATABASE_TABLE_RESTAURANT_NAME
                    + " (" + KEY_ROWID + "), "
                    + "foreign key" + " (" + KEY_LOCATIONS_HAVE_ID + ") references " + DATABASE_TABLE_LOCATIONS
                    + " (" + KEY_ROWID + ") "
                    + ");";

    private static final String DATABASE_CREATE_SERVERS_TABLE =
            "create table " + DATABASE_TABLE_SERVER
                    +" (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_SERVER_NAME + " string not null, "
                    + KEY_SERVER_LOCATION + " string,"
                    + "foreign key" + " (" + KEY_SERVER_LOCATION + ") references " + DATABASE_TABLE_LOCATIONS
                    + " (" + KEY_ROWID + ") "
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
    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    public Long insertRestName(String restName){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RESTAURANT_NAME,restName);
        return db.insert(DATABASE_TABLE_RESTAURANT_NAME,null,initialValues);
    }

    public Cursor getAllRestName(){
        Cursor c = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_RESTAURANT_NAME, null );
        if (c != null){
            c.moveToFirst();
        }
        return c;
    }

    /*
         public String[] getAllRestaurantNames(){
        Cursor cursor = this.db.query(DATABASE_TABLE,new String[]{KEY_RESTAURANT_NAME},null,
                null,null,null,null);

        if(cursor.getCount() > 0){

            String[] str = new String[cursor.getCount()];
            int i = 0;

            while(cursor.moveToNext()){
                    str[i] = cursor.getString(cursor.getColumnIndex(KEY_RESTAURANT_NAME));
                    i++;
               }
            return str;
        }else{
            return  new String[]{};
        }
    }
     */

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
