package com.android.udacitybookstoreappproject8;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.udacitybookstoreappproject8.storedata.StoreContract;

public class StoreCursorAdapter extends CursorAdapter {

    public StoreCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView productNameTextView = view.findViewById(R.id.product_name);
        TextView productQuantityTextView = view.findViewById(R.id.product_quantity);
        TextView productPriceTextView = view.findViewById(R.id.product_price);
        Button productSellingButton = view.findViewById(R.id.sell_product_button);

        int idColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry._ID);
        int productNameColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME);
        int productQuantityColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY);
        int productPriceColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE);

        final long id = (long)cursor.getInt(idColumnIndex);
        final String productName = cursor.getString(productNameColumnIndex);
        final int productQuantity = cursor.getInt(productQuantityColumnIndex);
        int productPrice = cursor.getInt(productPriceColumnIndex);

        productNameTextView.setText(productName);
        productQuantityTextView.setText(String.valueOf(productQuantity));
        productPriceTextView.setText(productPrice + "$");
        productSellingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productQuantity == 0){
                    Toast.makeText(context, "There's no mre " + productName + " in the inventory", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    int newQuantity = productQuantity - 1;
                    ContentValues values = new ContentValues();
                    values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                    context.getContentResolver().update(ContentUris.withAppendedId(StoreContract.StoreEntry.CONTENT_URI, id) , values,null,null);
                }
            }
        });
    }

}
