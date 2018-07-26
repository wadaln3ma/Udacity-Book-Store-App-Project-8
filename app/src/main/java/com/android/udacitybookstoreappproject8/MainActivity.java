package com.android.udacitybookstoreappproject8;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.udacitybookstoreappproject8.storedata.StoreContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 0;
    StoreCursorAdapter mCursorAdapter;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = findViewById(R.id.list_view);

        View emptyStateView = findViewById(R.id.empty_state_view);

        listView.setEmptyView(emptyStateView);

        mCursorAdapter = new StoreCursorAdapter(this, null);

        listView.setAdapter(mCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.setData(ContentUris.withAppendedId(StoreContract.StoreEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {


                return false;
            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    private void insertProduct() {
        ContentValues values = new ContentValues();

        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME, "Udacity");
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY, 12);
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE, 1000);
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "AI");
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, "+1-300-11111110");

        getContentResolver().insert(StoreContract.StoreEntry.CONTENT_URI, values);
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
                insertProduct();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                StoreContract.StoreEntry._ID,
                StoreContract.StoreEntry.COLUMN_PRODUCT_NAME,
                StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY,
                StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE
        };
        return new CursorLoader(this,
                StoreContract.StoreEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
