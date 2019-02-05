package com.example.hughkim.stocktracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;


public class Database {
    static final String KEY_ROWID = "_id";
    static final String KEY_COMPANY = "company";
    static final String KEY_TICKER = "ticker";
    static final String TAG = "DBAdapter";
    static final String DATABASE_NAME = "StockDB";
    static final String DATABASE_TABLE = "Stocks";
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_CREATE = "create table " + DATABASE_TABLE +
            " (" + KEY_ROWID + " integer primary key autoincrement, "
            + KEY_COMPANY + " text not null, " + KEY_TICKER + " text not null);";

    final Context context;
    BookSQLiteHelper DBHelper;
    SQLiteDatabase db;

    public Database(Context ctx) {
        this.context = ctx;
        DBHelper = new BookSQLiteHelper(context);
    }

    public static class BookSQLiteHelper extends SQLiteOpenHelper {
        BookSQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    //---opens the database---
    public Database open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close() {
        DBHelper.close();
    }

    public long addStock(String company,String ticker) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_COMPANY, company);
        initialValues.put(KEY_TICKER, ticker);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteByID(long rowId) {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
        //db.delete() returns the number of deleted rows
    }

    //--- deletes contacts by using the name ---
    public boolean deleteByCompName(String company) {
        String where = KEY_COMPANY + " LIKE ?";
        String[] whereArgs = {company};
        return db.delete(DATABASE_TABLE, where, whereArgs) > 0;
    }

    //--- deletes contacts by using the name ---
    public boolean deleteByTicker(String ticker) {
        String where = KEY_TICKER + " LIKE ?";
        String[] whereArgs = {ticker};
        return db.delete(DATABASE_TABLE, where, whereArgs) > 0;
    }

    //---retrieves all the contacts---
    public Cursor getAllStocks() {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_COMPANY,
                KEY_TICKER}, null, null, null, null, null);
    }
    //---retrieves a particular contact---
    public Cursor getStock(long rowId) throws SQLException {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                                KEY_COMPANY, KEY_TICKER}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a contact---
    public boolean updateStock(long rowId, String company, String ticker) {
        ContentValues args = new ContentValues();
        args.put(KEY_COMPANY, company);
        args.put(KEY_TICKER, ticker);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
        //db.update() returns the number of updated rows
    }

}