package de.dhbw.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DataBaseInfos implements DataBaseTable{

	// table name
		private static final String TABLE_NAME = "infos";

	// table colums
	    private static final String KEY_ID = "id";
	    private static final String KEY_NAME = "name";
	    private static final String KEY_IMAGE = "image";

	// create table query
	    private static final String CREATE_TABLE_LINKS_QUERY = "CREATE TABLE " + TABLE_NAME + "("
	            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_IMAGE + " TEXT" + ");";

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

        addInfo(db, new Info("Serverregeln", "ic_info_rules"));
        addInfo(db, new Info("Server-IPs\n(Adressen)", "ic_info_ips"));
        addInfo(db, new Info("Liste\nMods/Admins", "ic_info_admins"));
        addInfo(db, new Info("Kontakt", "ic_info_kontakt"));
	}
	
	// link functions
	
	private void addInfo(SQLiteDatabase db, Info mInfo) {
		
		ContentValues mContentValues = new ContentValues();
		mContentValues.put(KEY_NAME, mInfo.getName());
		mContentValues.put(KEY_IMAGE, mInfo.getImage());
		
		db.insert(TABLE_NAME, null, mContentValues);
	}

    public List<Info> getAllInfos(SQLiteDatabase db)
    {
        List<Info> mInfoList = new ArrayList<Info>();
        String query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Info mInfo = new Info();
                mInfo.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                mInfo.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                mInfo.setImage(cursor.getString(cursor.getColumnIndex(KEY_IMAGE)));

                // Adding workout to list
                mInfoList.add(mInfo);
            } while (cursor.moveToNext());
        }
        return mInfoList;
    }
		
}
