package de.dhbw.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
	
    public static final int DATABASE_VERSION = 26;
    public static final String DATABASE_NAME = "Kadconia.db";

    private List<DataBaseTable> mDatabaseTables = new ArrayList<DataBaseTable>();

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDatabaseTables.add(new DataBaseLinks());
        mDatabaseTables.add(new DataBaseInfos());
        mDatabaseTables.add(new DataBaseServer(context));
        mDatabaseTables.add(new DataBaseKontoEintraege());
        mDatabaseTables.add(new DataBaseAdmins());
    }

    public void onCreate(SQLiteDatabase db) {

        for (DataBaseTable mDataBaseTable : mDatabaseTables)
            mDataBaseTable.createTable(db);

        for (DataBaseTable mDataBaseTable : mDatabaseTables)
            mDataBaseTable.initTable(db);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    	// drop tables
        for (DataBaseTable mDataBaseTable : mDatabaseTables)
            mDataBaseTable.dropTable(db);
        
        // call onCreate to recreate the tables in the database
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
