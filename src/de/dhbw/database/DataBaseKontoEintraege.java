package de.dhbw.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataBaseKontoEintraege implements DataBaseTable{

	// table name
		private static final String TABLE_NAME = "konto";

	// table colums
	    private static final String KEY_ID = "id";
	    private static final String KEY_DATE = "date";
	    private static final String KEY_USERKONTO = "userkonto";
	    private static final String KEY_BETRAG = "betrag";
        private static final String KEY_PARTNERKONTO = "partnerkonto";
        private static final String KEY_TYPE = "type";
        private static final String KEY_NEWSALDO = "newsaldo";
        private static final String KEY_SERVER = "server";
        private static final String KEY_ITEM = "item";

	// create table query
	    private static final String CREATE_TABLE_KONTO_QUERY = "CREATE TABLE " + TABLE_NAME + "("
	            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " INTEGER," + KEY_USERKONTO + " TEXT,"
	            + KEY_BETRAG + " REAL," + KEY_PARTNERKONTO + " TEXT," + KEY_TYPE + " TEXT,"
                + KEY_NEWSALDO + " REAL," + KEY_SERVER + " TEXT," + KEY_ITEM + " TEXT" +");";

	public String getTableName() {
		return TABLE_NAME;
	}

	public void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}
	
	public void createTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_KONTO_QUERY);
	}
	
	public void initTable (SQLiteDatabase db) {

        //Testeinträge
        addKontoEintrag(db, new Kontoeintrag(System.currentTimeMillis(), "Vettel1", 20.0, "TestUser", "trade", "", "Server 1", 2013.14));
        addKontoEintrag(db, new Kontoeintrag(System.currentTimeMillis(), "Vettel1", -21.0, "TestUser", "trade", "", "Server 1", 2013.14));
        addKontoEintrag(db, new Kontoeintrag(System.currentTimeMillis(), "Vettel1", 22.0, "TestUser", "trade", "", "Server 1", 2013.14));
        addKontoEintrag(db, new Kontoeintrag(System.currentTimeMillis(), "Vettel1", -23.0, "TestUser", "trade", "", "Server 1", 2013.14));
	}
	
	// link functions

    public void addKontoEintrag(SQLiteDatabase db, Kontoeintrag mKontoeintrag) {
		
		ContentValues mContentValues = new ContentValues();
		mContentValues.put(KEY_DATE, mKontoeintrag.getDate());
		mContentValues.put(KEY_USERKONTO, mKontoeintrag.getUserKontoName());
        mContentValues.put(KEY_BETRAG, mKontoeintrag.getBetrag());
        mContentValues.put(KEY_PARTNERKONTO, mKontoeintrag.getPartnerKontoName());
        mContentValues.put(KEY_TYPE, mKontoeintrag.getType());
        mContentValues.put(KEY_NEWSALDO, mKontoeintrag.getNewSaldo());
        mContentValues.put(KEY_SERVER, mKontoeintrag.getServer());
        mContentValues.put(KEY_ITEM, mKontoeintrag.getItem());
		
		db.insert(TABLE_NAME, null, mContentValues);
	}

    public List<Kontoeintrag> getAllKontoEintraege(SQLiteDatabase db)
    {
        List<Kontoeintrag> mKontoList = new ArrayList<Kontoeintrag>();
        String query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Kontoeintrag mKontoeintrag = new Kontoeintrag();
                mKontoeintrag.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                mKontoeintrag.setDate(cursor.getInt(cursor.getColumnIndex(KEY_DATE)));
                mKontoeintrag.setUserKontoName(cursor.getString(cursor.getColumnIndex(KEY_USERKONTO)));
                mKontoeintrag.setBetrag(cursor.getFloat(cursor.getColumnIndex(KEY_BETRAG)));
                mKontoeintrag.setPartnerKontoName(cursor.getString(cursor.getColumnIndex(KEY_PARTNERKONTO)));
                mKontoeintrag.setType(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
                mKontoeintrag.setNewSaldo(cursor.getFloat(cursor.getColumnIndex(KEY_NEWSALDO)));
                mKontoeintrag.setServer(cursor.getString(cursor.getColumnIndex(KEY_SERVER)));
                mKontoeintrag.setItem(cursor.getString(cursor.getColumnIndex(KEY_ITEM)));

                // Adding workout to list
                mKontoList.add(mKontoeintrag);
            } while (cursor.moveToNext());
        }
        return mKontoList;
    }
		
}
