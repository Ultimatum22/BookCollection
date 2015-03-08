package com.x22.bookcollection.ui;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.x22.bookcollection.R;
import com.x22.bookcollection.provider.BookContract;
import com.x22.bookcollection.util.LogUtils;
import com.x22.bookcollection.util.Utils;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initData();

        Utils.replaceFragment(getFragmentManager(), BooksFragment.newInstance());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        switch(position) {
            case 0:
                Utils.replaceFragment(fragmentManager, BooksFragment.newInstance());
                break;
        }
    }

    public void onSectionAttached(String title) {
        mTitle = title;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_scan:
                //Utils.replaceFragment(getFragmentManager(), SearchFragment.newInstance());
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i("BookCollection", "BarcodeScannerActivity > onActivityResult > resultCode: "+ resultCode + " | requestCode: " + requestCode + " | intent: " + intent);

        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        Log.i("BookCollection", "BarcodeScannerActivity > onActivityResult > resultCode: "+ resultCode);
        if (result != null && resultCode == -1) {
            String contents = result.getContents();
            Log.i("BookCollection", "BarcodeScannerActivity > onActivityResult > contents: " + contents);

            Utils.replaceFragment(getFragmentManager(), SearchFragment.newInstance(contents));

        } else if(resultCode == 0) {
            Log.i("BookCollection", "BarcodeScannerActivity > onActivityResult > resultCode: 0");

//            if(requestCode == 22) {
//                IntentIntegrator integrator = new IntentIntegrator(BarcodeScannerActivity.this);
//                integrator.initiateScan();
//            } else {
//                finish();
//            }
        }
    }

    @Override
    public boolean onNavigateUp() {
        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }

        return true;
    }

    private void initData() {
        Log.d(TAG, "****************** Init data");
        ArrayList<ContentProviderOperation> list = new ArrayList<>();
        ContentProviderOperation.Builder builder;
        Uri allBooksUri = BookContract.addCallerIsSyncAdapterParameter(BookContract.Books.CONTENT_URI);
        Uri thisBookUri = BookContract.addCallerIsSyncAdapterParameter(BookContract.Books.buildBookUri("123"));

        builder = ContentProviderOperation.newInsert(allBooksUri);

        builder.withValue(BookContract.Books.BOOK_ID, "123")
                .withValue(BookContract.Books.TITLE, "Test title 1") ;
        list.add(builder.build());

        builder = ContentProviderOperation.newInsert(allBooksUri);
        builder.withValue(BookContract.Books.BOOK_ID, "124")
                .withValue(BookContract.Books.TITLE, "Test title 2") ;
        list.add(builder.build());

        builder = ContentProviderOperation.newInsert(allBooksUri);
        builder.withValue(BookContract.Books.BOOK_ID, "125")
                .withValue(BookContract.Books.TITLE, "Test title 3") ;
        list.add(builder.build());

        builder = ContentProviderOperation.newInsert(allBooksUri);
        builder.withValue(BookContract.Books.BOOK_ID, "126")
                .withValue(BookContract.Books.TITLE, "Test title 4") ;
        list.add(builder.build());

        builder = ContentProviderOperation.newInsert(allBooksUri);
        builder.withValue(BookContract.Books.BOOK_ID, "127")
                .withValue(BookContract.Books.TITLE, "Test title 5") ;
        list.add(builder.build());

        try {
            getApplicationContext().getContentResolver().applyBatch(BookContract.CONTENT_AUTHORITY, list);
        } catch (RemoteException ex) {
            Log.e(TAG, "RemoteException while applying content provider operations.");
            throw new RuntimeException("Error executing content provider batch operation", ex);
        } catch (OperationApplicationException ex) {
            Log.e(TAG, "OperationApplicationException while applying content provider operations.");
            throw new RuntimeException("Error executing content provider batch operation", ex);
        }
    }
}