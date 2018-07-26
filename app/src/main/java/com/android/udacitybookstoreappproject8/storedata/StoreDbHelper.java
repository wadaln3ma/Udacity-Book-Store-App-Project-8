package com.android.udacitybookstoreappproject8.storedata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StoreDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "store.db";

    private static final int DATABASE_VERSION = 1;

    public StoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_PRODUCT_TABLE = "CREATE TABLE " + StoreContract.StoreEntry.TABLE_NAME + " ("
                + StoreContract.StoreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StoreContract.StoreEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL, "
                + StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER + " TEXT);";
        db.execSQL(CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
