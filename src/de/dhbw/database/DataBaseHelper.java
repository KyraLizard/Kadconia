package de.dhbw.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
	
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Kadconia.db";
    
    // tables
    public static final DataBaseLinks mDataBaseLinks = new DataBaseLinks();
	       

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
    	
    	mDataBaseLinks.createTable(db);
    	mDataBaseLinks.initTable(db);

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    	// drop tables
        mDataBaseLinks.dropTable(db);
        
        // call onCreate to recreate the tables in the database
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
