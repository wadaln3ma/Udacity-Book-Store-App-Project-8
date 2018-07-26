package com.android.udacitybookstoreappproject8;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import static com.android.udacitybookstoreappproject8.storedata.StoreContract.*;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_STORE_LOADER = 0;
    Uri currentUri;
    private boolean inventoryHasChanged = false;

    EditText productNameTeEditText;
    EditText productQuantityEditText;
    EditText productPriceEditText;
    EditText productSupplierNameEditText;
    EditText productSupplierPhoneEditText;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            inventoryHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        productNameTeEditText = findViewById(R.id.product_name_et);
        productPriceEditText = findViewById(R.id.product_price_et);
        productQuantityEditText = findViewById(R.id.product_quantity_et);
        productSupplierNameEditText = findViewById(R.id.product_supplier_name_et);
        productSupplierPhoneEditText = findViewById(R.id.product_supplier_phone_et);

        productNameTeEditText.setOnTouchListener(mTouchListener);
        productPriceEditText.setOnTouchListener(mTouchListener);
        productQuantityEditText.setOnTouchListener(mTouchListener);
        productSupplierNameEditText.setOnTouchListener(mTouchListener);
        productSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        Intent intent = getIntent();
        currentUri = intent.getData();
        if (currentUri != null){
            setTitle("Edit Inventory");
            getLoaderManager().initLoader(EXISTING_STORE_LOADER, null, this);
        }else{
            setTitle("Add Inventory");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        switch (selectedItemId){
            case R.id.save_inventory:
                saveInventory();
                finish();
                return true;
            case android.R.id.home:
                if (!inventoryHasChanged){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (inventoryHasChanged){
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    };
            showUnsavedChangesDialog(discardButtonClickListener);
            return;
        }
        super.onBackPressed();
    }

    private void saveInventory(){
        String productName = productNameTeEditText.getText().toString().trim();
        String stringProductQuantity = productQuantityEditText.getText().toString().trim();
        String stringProductPrice = productPriceEditText.getText().toString().trim();
        String supplierName = productSupplierNameEditText.getText().toString().trim();
        String supplierPhone = productSupplierPhoneEditText.getText().toString().trim();

        if (currentUri == null && TextUtils.isEmpty(productName) && TextUtils.isEmpty(stringProductQuantity)
                && TextUtils.isEmpty(stringProductPrice) &&
                TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierPhone)){
            Toast.makeText(EditorActivity.this, "You have to fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(StoreEntry.COLUMN_PRODUCT_NAME,productName);
        values.put(StoreEntry.COLUMN_PRODUCT_PRICE, stringProductPrice);
        values.put(StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        values.put(StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhone);
        int productQuantity = 0;
         if (!TextUtils.isEmpty(stringProductQuantity)){
             productQuantity = Integer.valueOf(stringProductQuantity);
         }
        values.put(StoreEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
         int productPrice = 0;
         if (!TextUtils.isEmpty(stringProductPrice)){
             productPrice = Integer.valueOf(stringProductPrice);
         }
         values.put(StoreEntry.COLUMN_PRODUCT_PRICE,productPrice);

        if (currentUri == null){
            Uri newUri = getContentResolver().insert(StoreEntry.CONTENT_URI, values);
            if (newUri == null){
                Toast.makeText(EditorActivity.this, "Insert Inventory failed", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(EditorActivity.this, "Insert Inventory is successful", Toast.LENGTH_SHORT).show();
            }
        }else {
            int effectedRows = getContentResolver().update(currentUri, values, null, null);
            if (effectedRows == 0){
                Toast.makeText(EditorActivity.this, "Update Inventory failed", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(EditorActivity.this, "Update Inventory is successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                currentUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int productNameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_NAME);
        int productQuantityColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_QUANTITY);
        int productPriceColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_PRICE);
        int productSupplierNameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        int productSupplierPhoneColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

        while (cursor.moveToNext()) {
            String productName = cursor.getString(productNameColumnIndex);
            int productQuantity = cursor.getInt(productQuantityColumnIndex);
            int productPrice = cursor.getInt(productPriceColumnIndex);
            String supplierName = cursor.getString(productSupplierNameColumnIndex);
            String supplierPhone = cursor.getString(productSupplierPhoneColumnIndex);

            productNameTeEditText.setText(productName);
            productPriceEditText.setText(Integer.toString(productPrice));
            productQuantityEditText.setText(Integer.toString(productQuantity));
            productSupplierNameEditText.setText(supplierName);
            productSupplierPhoneEditText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discarrd", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
