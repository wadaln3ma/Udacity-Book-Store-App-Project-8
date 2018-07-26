package com.android.udacitybookstoreappproject8.storedata;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class StoreProvider extends ContentProvider {
    public static final String TAG = StoreProvider.class.getSimpleName();

    private static final int PRODUCTS = 200;

    private static final int PRODUCTS_ID = 202;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_PRODUCTS, PRODUCTS);

        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_PRODUCTS + "/#", PRODUCTS_ID);

    }

    private StoreDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new StoreDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(StoreContract.StoreEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PRODUCTS_ID:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(StoreContract.StoreEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query Unkonwn uri " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType( Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                return StoreContract.StoreEntry.CONTENT_LIST_TYPE;
            case PRODUCTS_ID:
                return StoreContract.StoreEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " With match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String name = values.getAsString(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("product requires a name");
        }

        Integer quantity = values.getAsInteger(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null && quantity < 0) {
            throw new IllegalArgumentException("product require a valid quantity");
        }

        Integer price = values.getAsInteger(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE);
        if (price == null && price < 0) {
            throw new IllegalArgumentException("product require a valid price");
        }

        String supplierName = values.getAsString(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        if (supplierName == null || TextUtils.isEmpty(supplierName)) {
            throw new IllegalArgumentException("product require a valid supplier number");
        }

        String supplierPhoneNumber = values.getAsString(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        if (TextUtils.isEmpty(supplierPhoneNumber)) {
            throw new IllegalArgumentException("product require a valid supplier phone number");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long insertedRow = database.insert(StoreContract.StoreEntry.TABLE_NAME, null, values);

        if (insertedRow == -1) {
            Log.e(TAG, "failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, insertedRow);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int deletedRows;
        switch (match) {
            case PRODUCTS:
                deletedRows = database.delete(StoreContract.StoreEntry.TABLE_NAME,
                        selection,
                        selectionArs);
                break;
            case PRODUCTS_ID:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = database.delete(StoreContract.StoreEntry.TABLE_NAME,
                        selection,
                        selectionArs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCTS_ID:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("product require a name");
            }
        }

        if (values.containsKey(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("product require a valid quantity");
            }
        }

        if (values.containsKey(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("product require a valid price");
            }
        }

        if (values.containsKey(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (supplierName == null || TextUtils.isEmpty(supplierName)) {
                throw new IllegalArgumentException("product require a valid supplier number");
            }
        }

        if (values.containsKey(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumber = values.getAsString(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            if (TextUtils.isEmpty(supplierPhoneNumber)) {
                throw new IllegalArgumentException("product require a valid supplier phone number");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowUpdated = database.update(StoreContract.StoreEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowUpdated;
    }
}
