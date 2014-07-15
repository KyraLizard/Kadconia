package de.dhbw.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DataBaseAdmins implements DataBaseTable{

    // table name
    private static final String TABLE_NAME = "admins";

    // table colums
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_RANK = "rank";
    private static final String KEY_DETAILEDRANK = "detailedrank";
    private static final String KEY_MEMBERSHIPDATE = "membershipdate";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_AGE = "age";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_POSTCOUNT = "postcount";
    private static final String KEY_LIKECOUNT = "likecount";
    private static final String KEY_POINTS = "points";


    // create table query
    private static final String CREATE_TABLE_LINKS_QUERY = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_IMAGE + " TEXT,"
            + KEY_RANK + " TEXT," + KEY_DETAILEDRANK + " TEXT," + KEY_MEMBERSHIPDATE + " INTEGER,"
            + KEY_LOCATION + " TEXT," + KEY_AGE + " INTEGER," + KEY_GENDER + " TEXT,"
            + KEY_POSTCOUNT + " INTEGER," + KEY_LIKECOUNT + " INTEGER," + KEY_POINTS + " INTEGER" + ");";

    public String getTableName() {
        return TABLE_NAME;
    }

    public void deleteAllEntries(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    public void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LINKS_QUERY);
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

    // admin functions

    public void addAdmin(SQLiteDatabase db, Admin mAdmin) {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(KEY_NAME, mAdmin.getName());
        mContentValues.put(KEY_IMAGE, mAdmin.getImage());
        mContentValues.put(KEY_RANK, mAdmin.getRank());
        mContentValues.put(KEY_DETAILEDRANK, mAdmin.getDetailedRank());
        mContentValues.put(KEY_MEMBERSHIPDATE, mAdmin.getMembershipDate());
        mContentValues.put(KEY_LOCATION, mAdmin.getLocation());
        mContentValues.put(KEY_AGE, mAdmin.getAge());
        mContentValues.put(KEY_GENDER, mAdmin.getGender());
        mContentValues.put(KEY_POSTCOUNT, mAdmin.getPostCount());
        mContentValues.put(KEY_LIKECOUNT, mAdmin.getLikeCount());
        mContentValues.put(KEY_POINTS, mAdmin.getPoints());

        db.insert(TABLE_NAME, null, mContentValues);
    }

    public void addAdmin(Context context, Admin mAdmin) {

        SQLiteDatabase db = getWritableDatabase(context);
        addAdmin(db, mAdmin);
        db.close();
    }

    public List<Admin> getAllAdmins(Context context)
    {
        SQLiteDatabase db = getReadableDatabase(context);
        List<Admin> mAdminList = new ArrayList<Admin>();
        String query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Admin mAdmin = new Admin();
                mAdmin.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                mAdmin.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                mAdmin.setImage(cursor.getString(cursor.getColumnIndex(KEY_IMAGE)));
                mAdmin.setRank(cursor.getString(cursor.getColumnIndex(KEY_RANK)));
                mAdmin.setDetailedRank(cursor.getString(cursor.getColumnIndex(KEY_DETAILEDRANK)));
                mAdmin.setMembershipDate(cursor.getLong(cursor.getColumnIndex(KEY_MEMBERSHIPDATE)));
                mAdmin.setLocation(cursor.getString(cursor.getColumnIndex(KEY_LOCATION)));
                mAdmin.setAge(cursor.getInt(cursor.getColumnIndex(KEY_AGE)));
                mAdmin.setGender(cursor.getString(cursor.getColumnIndex(KEY_GENDER)));
                mAdmin.setPostCount(cursor.getInt(cursor.getColumnIndex(KEY_POSTCOUNT)));
                mAdmin.setLikeCount(cursor.getInt(cursor.getColumnIndex(KEY_LIKECOUNT)));
                mAdmin.setPoints(cursor.getInt(cursor.getColumnIndex(KEY_POINTS)));

                // Adding workout to list
                mAdminList.add(mAdmin);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mAdminList;
    }

}
