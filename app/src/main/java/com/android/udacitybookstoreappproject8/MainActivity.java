package com.android.udacitybookstoreappproject8;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.udacitybookstoreappproject8.storedata.StoreContract;
import com.android.udacitybookstoreappproject8.storedata.StoreDbHelper;

public class MainActivity extends AppCompatActivity {
    private StoreDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new StoreDbHelper(this);
        showDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showDatabaseInfo();
    }

    private void showDatabaseInfo(){
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        String[] projection = {
                StoreContract.StoreEntry._ID,
                StoreContract.StoreEntry.COLUMN_PRODUCT_NAME,
                StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE,
                StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY,
                StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NUMBER,
                StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };

        Cursor cursor = database.query(StoreContract.StoreEntry.TABLE_NAME,
                projection, null, null, null, null , null);

        TextView textView = findViewById(R.id.text_view);
        textView.setText(StoreContract.StoreEntry._ID
                + "--" + StoreContract.StoreEntry.COLUMN_PRODUCT_NAME
                + "--" + StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE
                + "--" + StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY
                + "--" + StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NUMBER
                + "--" + StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        try {

            int idColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY);
            int productSupplierNumberColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NUMBER);
            int productSupplierPhoneColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            while (cursor.moveToNext()){
                int id = cursor.getInt(idColumnIndex);
                String productName = cursor.getString(productNameColumnIndex);
                int productPrice = cursor.getInt(productPriceColumnIndex);
                int productQuantity = cursor.getInt(productQuantityColumnIndex);
                String productSupplierNumber = cursor.getString(productSupplierNumberColumnIndex);
                String productSupplierPhoneNumber = cursor.getString(productSupplierPhoneColumnIndex);

                textView.append("\n" + id + "--" + productName + "--"
                        + productPrice + "--" + productQuantity + "--"
                        + productSupplierNumber + "--" + productSupplierPhoneNumber);
            }

        }  finally {
            cursor.close();
        }
    }

    private void insertData(){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME, "Some Name");
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE, 200);
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY, 60);
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NUMBER, "5135860");
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, "+1 379739473974");

        long insertedRowId = database.insert(StoreContract.StoreEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedId = item.getItemId();
        switch (selectedId) {
            case R.id.inset_dummy_data:
                insertData();
                showDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
