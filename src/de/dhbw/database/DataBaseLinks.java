package de.dhbw.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseLinks{

	// table name
		private static final String TABLE_NAME = "links";

	// table colums
	    private static final String KEY_ID = "id";
	    private static final String KEY_NAME = "name";
	    private static final String KEY_URL = "url";
	    private static final String KEY_IMAGE = "image";

	// create table query
	    private static final String CREATE_TABLE_LINKS_QUERY = "CREATE TABLE " + TABLE_NAME + "("
	            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + KEY_URL
	            + " TEXT" + KEY_IMAGE + " TEXT" + ");";

	public String getTableName() {
		return TABLE_NAME;
	}

	public void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}
	
	public void createTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_LINKS_QUERY);
	}
	
	public void initTable (SQLiteDatabase db) {
		
		// TODO: Links hinzufügen
	}
	
	// link functions
	
	private void addLink(SQLiteDatabase db, Link mLink) {
		
		ContentValues mContentValues = new ContentValues();
		mContentValues.put(KEY_NAME, mLink.getName());
		mContentValues.put(KEY_URL, mLink.getUrl());
		mContentValues.put(KEY_IMAGE, mLink.getImage());
		
		db.insert(TABLE_NAME, null, mContentValues);
	}
		
}
