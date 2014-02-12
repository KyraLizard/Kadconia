package de.dhbw.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import de.dhbw.navigation.R;

/**
 * Created by Mark on 21.11.13.
 */
public class DataBaseServer implements DataBaseTable{

    private Context mContext;

    // table name
        private static final String TABLE_NAME = "server";

    // table colums
        private static final String KEY_ID = "id";
        private static final String KEY_NAME = "name";
        private static final String KEY_OWNER = "owner";
        private static final String KEY_DOMAIN = "domain";
        private static final String KEY_PORT = "port";

    // create table query
    private static final String CREATE_TABLE_SERVER_QUERY = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_OWNER + " TEXT," + KEY_DOMAIN
            + " TEXT," + KEY_PORT + " INTEGER" + ");";

    public DataBaseServer(Context context) {
        this.mContext = context;
    }

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

        String ownerKadcon = mContext.getString(R.string.serverstatus_owner_kadcon);
        String ownerMojang = mContext.getString(R.string.serverstatus_owner_mojang);

        addServer(db, new Server("Server 1", ownerKadcon, "kadcon.de", 51332));
        addServer(db, new Server("Server 2", ownerKadcon, "kadcon.de", 41332));
        addServer(db, new Server("Server 3", ownerKadcon, "kadcon.de", 31332));

        addServer(db, new Server("Website", ownerMojang, "minecraft.net", 80));
        addServer(db, new Server("Skinserver", ownerMojang, "skins.minecraft.net", 80));
        addServer(db, new Server("Accountserver", ownerMojang, "account.mojang.com", 80));
        addServer(db, new Server("Authentifikationsserver", ownerMojang, "authserver.mojang.com", 80));
        addServer(db, new Server("Sessionserver", ownerMojang, "sessionserver.mojang.com", 80));
    }

    @Override
    public SQLiteDatabase getReadableDatabase(Context context) {
        return (new DataBaseHelper(context)).getReadableDatabase();
    }

    @Override
    public SQLiteDatabase getWritableDatabase(Context context) {
        return (new DataBaseHelper(context)).getWritableDatabase();
    }


    // server functions

    private void addServer(SQLiteDatabase db, Server mServer) {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(KEY_NAME, mServer.getName());
        mContentValues.put(KEY_OWNER, mServer.getOwner());
        mContentValues.put(KEY_DOMAIN, mServer.getDomain());
        mContentValues.put(KEY_PORT, mServer.getPort());

        db.insert(TABLE_NAME, null, mContentValues);
    }

    private void addServer(Context context, Server mServer) {

        SQLiteDatabase db = getWritableDatabase(context);
        addServer(db, mServer);
        db.close();
    }

    public List<Server> getAllServer()
    {
        SQLiteDatabase db = getReadableDatabase(mContext);
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
                mServer.setDomain(cursor.getString(cursor.getColumnIndex(KEY_DOMAIN)));
                mServer.setPort(cursor.getInt(cursor.getColumnIndex(KEY_PORT)));

                // Adding workout to list
                mServerList.add(mServer);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mServerList;
    }

    public List<Server> getAllServerByOwner(String owner)
    {
        SQLiteDatabase db = getReadableDatabase(mContext);
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
                mServer.setDomain(cursor.getString(cursor.getColumnIndex(KEY_DOMAIN)));
                mServer.setPort(cursor.getInt(cursor.getColumnIndex(KEY_PORT)));

                // Adding workout to list
                mServerList.add(mServer);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mServerList;
    }

    public List<String> getOwners()
    {
        SQLiteDatabase db = getReadableDatabase(mContext);
        List<String> mOwnerList = new ArrayList<String>();
        String query = "SELECT DISTINCT " + KEY_OWNER + " FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                mOwnerList.add(cursor.getString(cursor.getColumnIndex(KEY_OWNER)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mOwnerList;
    }

    public int getServerCount(Context context)
    {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase(context);
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
}
