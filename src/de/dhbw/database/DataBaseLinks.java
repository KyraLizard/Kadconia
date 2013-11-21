package de.dhbw.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DataBaseLinks implements DataBaseTable{

	// table name
		private static final String TABLE_NAME = "links";

	// table colums
	    private static final String KEY_ID = "id";
	    private static final String KEY_NAME = "name";
	    private static final String KEY_URL = "url";
	    private static final String KEY_IMAGE = "image";

	// create table query
	    private static final String CREATE_TABLE_LINKS_QUERY = "CREATE TABLE " + TABLE_NAME + "("
	            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_URL
	            + " TEXT," + KEY_IMAGE + " TEXT" + ");";

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

        addLink(db, new Link("Forum", "http://m.kadcon.de/index.php?page=Portal", "ic_link_forum"));
        addLink(db, new Link("Shop", "http://shop.kadcon.de/", "ic_link_shop"));
        addLink(db, new Link("Bans", "http://kadcon.de/banmanagement/", "ic_link_ban"));
        addLink(db, new Link("Facebook", "https://m.facebook.com/Kadcon.de", "ic_link_facebook"));
        addLink(db, new Link("Youtube", "http://m.youtube.com/user/kadconDE", "ic_link_youtube"));
        addLink(db, new Link("Twitter\n(Kademlia)", "https://mobile.twitter.com/Kademlias", "ic_link_twitter"));
	}
	
	// link functions
	
	private void addLink(SQLiteDatabase db, Link mLink) {
		
		ContentValues mContentValues = new ContentValues();
		mContentValues.put(KEY_NAME, mLink.getName());
		mContentValues.put(KEY_URL, mLink.getUrl());
		mContentValues.put(KEY_IMAGE, mLink.getImage());
		
		db.insert(TABLE_NAME, null, mContentValues);
	}

    public List<Link> getAllLinks(SQLiteDatabase db)
    {
        List<Link> mLinkList = new ArrayList<Link>();
        String query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Link mLink = new Link();
                mLink.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                mLink.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                mLink.setUrl(cursor.getString(cursor.getColumnIndex(KEY_URL)));
                mLink.setImage(cursor.getString(cursor.getColumnIndex(KEY_IMAGE)));

                // Adding workout to list
                mLinkList.add(mLink);
            } while (cursor.moveToNext());
        }
        return mLinkList;
    }
		
}
