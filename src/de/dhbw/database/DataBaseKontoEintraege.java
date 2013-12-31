package de.dhbw.database;

import android.content.ContentValues;
import android.content.Context;
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

	}

    @Override
    public SQLiteDatabase getReadableDatabase(Context context) {
        return (new DataBaseHelper(context)).getReadableDatabase();
    }

    @Override
    public SQLiteDatabase getWritableDatabase(Context context) {
        return (new DataBaseHelper(context)).getWritableDatabase();
    }
	
	// kontoeinträge functions

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

    public void addKontoEintrag(Context context, Kontoeintrag mKontoeintrag) {

        SQLiteDatabase db = getWritableDatabase(context);
        addKontoEintrag(db, mKontoeintrag);
        db.close();
    }

    public boolean isKontoeintragInDatabase(Context context, Kontoeintrag kontoeintrag) {

        SQLiteDatabase db = getReadableDatabase(context);
        int count = -1;
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + KEY_DATE + "=? AND " + KEY_USERKONTO + "=? AND "
                + KEY_BETRAG + "=? AND " + KEY_PARTNERKONTO + "=? AND " + KEY_TYPE + "=? AND " + KEY_NEWSALDO + "=? AND "
                + KEY_SERVER + "=? AND " + KEY_ITEM + "=?";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(kontoeintrag.getDate()), kontoeintrag.getUserKontoName(),
                                String.valueOf(kontoeintrag.getBetrag()), kontoeintrag.getPartnerKontoName(), kontoeintrag.getType(),
                                String.valueOf(kontoeintrag.getNewSaldo()), kontoeintrag.getServer(), kontoeintrag.getItem()});
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count > 0;
    }

    public int getKontoEintraegeCount(Context context)
    {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase(context);
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public List<Kontoeintrag> getKontoEintraege(Context context, int count, int page)
    {
        if (count < 1)
            throw new IllegalArgumentException("Ungültige Anzahl angeforderter Kontoeinträge!");
        if (page < 1 || page > getKontoEintraegeCount(context) / count + 1)
            throw new IllegalArgumentException("Ungültige Seite! (Angefordert: "+page+", Erlaubt: 1-"+(getKontoEintraegeCount(context)/count+1)+")");

        SQLiteDatabase db = getReadableDatabase(context);
        List<Kontoeintrag> mKontoList = new ArrayList<Kontoeintrag>();
        String query = "SELECT * FROM " + TABLE_NAME + " LIMIT " + count + " OFFSET " + (page-1)*count;

        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Kontoeintrag mKontoeintrag = new Kontoeintrag();
                mKontoeintrag.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                mKontoeintrag.setDate(cursor.getLong(cursor.getColumnIndex(KEY_DATE)));
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
        cursor.close();
        db.close();
        return mKontoList;
    }

    public List<Kontoeintrag> getAllKontoEintraege(Context context)
    {
        SQLiteDatabase db = getReadableDatabase(context);
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
        cursor.close();
        db.close();
        return mKontoList;
    }
		
}
