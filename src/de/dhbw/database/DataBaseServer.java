package de.dhbw.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 21.11.13.
 */
public class DataBaseServer implements DataBaseTable{

    // table name
        private static final String TABLE_NAME = "server";

    // table colums
        private static final String KEY_ID = "id";
        private static final String KEY_NAME = "name";
        private static final String KEY_OWNER = "owner";
        private static final String KEY_IP = "ip";
        private static final String KEY_PORT = "port";

    // create table query
    private static final String CREATE_TABLE_SERVER_QUERY = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_OWNER + " TEXT," + KEY_IP
            + " TEXT," + KEY_PORT + " INTEGER" + ");";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    @Override
    public void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SERVER_QUERY);
    }

    @Override
    public void initTable(SQLiteDatabase db) {

        addServer(db, new Server("Server 1", "kadcon", "94.23.160.180", 51332));
        addServer(db, new Server("Server 2", "kadcon", "94.23.160.180", 41332));
        addServer(db, new Server("Server 3", "kadcon", "94.23.160.180", 31332));

        addServer(db, new Server("Website", "mojang", "107.20.160.105", 80));
        addServer(db, new Server("Skinserver", "mojang", "54.243.83.21", 80));
        addServer(db, new Server("Accountserver", "mojang", "174.129.244.131", 443));
        addServer(db, new Server("Authentifikationsserver", "mojang", "23.23.247.204", 443));
        addServer(db, new Server("Sessionserver", "mojang", "23.21.76.163", 443));
    }


    // server functions

    private void addServer(SQLiteDatabase db, Server mServer) {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(KEY_NAME, mServer.getName());
        mContentValues.put(KEY_OWNER, mServer.getOwner());
        mContentValues.put(KEY_IP, mServer.getIp());
        mContentValues.put(KEY_PORT, mServer.getPort());

        db.insert(TABLE_NAME, null, mContentValues);
    }

    public List<Server> getAllServer(SQLiteDatabase db)
    {
        List<Server> mServerList = new ArrayList<Server>();
        String query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Server mServer = new Server();
                mServer.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                mServer.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                mServer.setOwner(cursor.getString(cursor.getColumnIndex(KEY_OWNER)));
                mServer.setIp(cursor.getString(cursor.getColumnIndex(KEY_IP)));
                mServer.setPort(cursor.getInt(cursor.getColumnIndex(KEY_PORT)));

                // Adding workout to list
                mServerList.add(mServer);
            } while (cursor.moveToNext());
        }
        return mServerList;
    }

    public List<Server> getAllServerByOwner(SQLiteDatabase db, String owner)
    {
        List<Server> mServerList = new ArrayList<Server>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_OWNER + "='" + owner + "'";

        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Server mServer = new Server();
                mServer.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                mServer.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                mServer.setOwner(cursor.getString(cursor.getColumnIndex(KEY_OWNER)));
                mServer.setIp(cursor.getString(cursor.getColumnIndex(KEY_IP)));
                mServer.setPort(cursor.getInt(cursor.getColumnIndex(KEY_PORT)));

                // Adding workout to list
                mServerList.add(mServer);
            } while (cursor.moveToNext());
        }
        return mServerList;
    }

    public List<String> getOwners(SQLiteDatabase db)
    {
        List<String> mOwnerList = new ArrayList<String>();
        String query = "SELECT DISTINCT " + KEY_OWNER + " FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                mOwnerList.add(cursor.getString(cursor.getColumnIndex(KEY_OWNER)));
            } while (cursor.moveToNext());
        }

        return mOwnerList;
    }
}
