package de.dhbw.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Mark on 18.11.13.
 */

public interface DataBaseTable{

    public String getTableName();
    public void dropTable(SQLiteDatabase db);
    public void createTable(SQLiteDatabase db);
    public void initTable(SQLiteDatabase db);
    public SQLiteDatabase getReadableDatabase(Context context);
    public SQLiteDatabase getWritableDatabase(Context context);
}
