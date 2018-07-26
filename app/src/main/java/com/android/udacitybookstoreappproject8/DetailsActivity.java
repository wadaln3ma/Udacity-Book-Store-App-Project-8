package com.android.udacitybookstoreappproject8;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.udacitybookstoreappproject8.storedata.StoreContract;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_STORE_LOADER = 0;
    private final int REQUEST_CALL_CODE = 1;
    TextView productNameTextView;
    TextView productPriceTextView;
    TextView productQuantityTextView;
    TextView productSupplierNameTextView;
    TextView productSupplierPhoneTextView;
    Button sellProductButton;
    Button orderProductButton;
    Button callSupplierPhoneButton;
    private Uri currentUri;
    private int productQuantity;
    private String supplierPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Details");

        Intent intent = getIntent();
        currentUri = intent.getData();

        productNameTextView = findViewById(R.id.product_name_tv);
        productPriceTextView = findViewById(R.id.product_price_tv);
        productQuantityTextView = findViewById(R.id.product_quantity_tv);
        productSupplierNameTextView = findViewById(R.id.supplier_name_tv);
        productSupplierPhoneTextView = findViewById(R.id.supplier_phone_number_tv);
        sellProductButton = findViewById(R.id.sell_product_btn);
        orderProductButton = findViewById(R.id.order_product_btn);
        callSupplierPhoneButton = findViewById(R.id.call_supplier_btn);

        sellProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productQuantity > 0) {
                    productQuantity--;
                    ContentValues values = new ContentValues();
                    values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
                    getContentResolver().update(currentUri, values, null, null);
                    return;
                }
                Toast.makeText(DetailsActivity.this, R.string.quantity_less_than_zero_msg, Toast.LENGTH_SHORT).show();
            }
        });

        orderProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productQuantity++;
                ContentValues values = new ContentValues();
                values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
                getContentResolver().update(currentUri, values, null, null);
            }
        });

        callSupplierPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + supplierPhone));
                if (callIntent.resolveActivity(getPackageManager()) != null) {
                    if (ActivityCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(DetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_CODE);
                    } else {
                        startActivity(callIntent);
                    }
                }
            }
        });

        if (currentUri != null) {
            getLoaderManager().initLoader(EXISTING_STORE_LOADER, null, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + supplierPhone));
                    startActivity(callIntent);
                } else {
                    return;
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        switch (selectedItemId) {
            case R.id.delete_inventory:
                showDeleteAlertDialogue();
                break;
            case R.id.edit_inventory:
                Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);
                intent.setData(currentUri);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {StoreContract.StoreEntry._ID,
                StoreContract.StoreEntry.COLUMN_PRODUCT_NAME,
                StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY,
                StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE,
                StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this,
                currentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int productNameColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME);
        int productQuantityColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY);
        int productPriceColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE);
        int productSupplierNameColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        int productSupplierPhoneColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

        while (cursor.moveToNext()) {
            String productName = cursor.getString(productNameColumnIndex);
            productQuantity = cursor.getInt(productQuantityColumnIndex);
            int productPrice = cursor.getInt(productPriceColumnIndex);
            String supplierName = cursor.getString(productSupplierNameColumnIndex);
            supplierPhone = cursor.getString(productSupplierPhoneColumnIndex);

            productNameTextView.setText(productName);
            productPriceTextView.setText(productPrice + getString(R.string.dolar_sign));
            productQuantityTextView.setText(String.valueOf(productQuantity));
            productSupplierNameTextView.setText(supplierName);
            productSupplierPhoneTextView.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productNameTextView.setText("");
        productPriceTextView.setText("");
        productQuantityTextView.setText("");
        productSupplierNameTextView.setText("");
        productSupplierPhoneTextView.setText("");
    }

    private void showDeleteAlertDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_dialog_title_delete);
        builder.setMessage(R.string.alert_dialog_delete_msg);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getContentResolver().delete(currentUri, null, null);
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Do Nothing
            }
        });
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.show();
    }
}
