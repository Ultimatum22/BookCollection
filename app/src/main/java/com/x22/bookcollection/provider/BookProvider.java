package com.x22.bookcollection.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.x22.bookcollection.provider.BookDatabase.*;
import com.x22.bookcollection.util.LogUtils;
import com.x22.bookcollection.util.SelectionBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class BookProvider extends ContentProvider {
    private static final String TAG = LogUtils.makeLogTag(BookProvider.class);

    private BookDatabase openHelper;

    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;
    private static final int BOOKS_ID_AUTHORS = 102;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BookContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "books", BOOKS);
        matcher.addURI(authority, "books/*", BOOKS_ID);
        matcher.addURI(authority, "books/*/authors", BOOKS_ID_AUTHORS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        openHelper = new BookDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = openHelper.getReadableDatabase();

        final int match = uriMatcher.match(uri);

        Log.d("BookProvider", "ScheduleProvider > query: uri=" + uri + " match=" + match + " proj=" + Arrays.toString(projection) +" selection=" + selection + " args=" + Arrays.toString(selectionArgs) + ")");

        final SelectionBuilder builder = buildExpandedSelection(uri, match);
        Cursor cursor = builder.where(selection, selectionArgs).query(db, false, projection, sortOrder, null);
        Context context = getContext();
        if(context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        Log.i(TAG, "BookProvider > getType > " + uriMatcher.match(uri));

        switch(uriMatcher.match(uri)) {
            case BOOKS_ID:
                return BookContract.Books.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.d(TAG, String.format("insert(uri=%s, contentValues=%s", uri, contentValues.toString()));

        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch(match) {
            case BOOKS:
                db.insertOrThrow(Tables.BOOKS, null, contentValues);
                notifyChange(uri);
                return BookContract.Books.buildBookUri(contentValues.getAsString(BookContract.Books.BOOK_ID));

            default:
                throw new UnsupportedOperationException("Unknown inset uri" + uri);
        }
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        Log.i(TAG, "Bookprovider > applyBatch");
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numberOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numberOperations];
            for(int i= 0; i < numberOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        Log.i(TAG, "Uri: "+ uri + " - Match: "+ match);
        final SelectionBuilder builder = new SelectionBuilder();
        switch(match) {
            case BOOKS:
                return builder.table(Tables.BOOKS);

            case BOOKS_ID:
                final String bookId = BookContract.Books.getBookId(uri);
                Log.i(TAG, "buildExpandedSelection: "+ bookId);

                SelectionBuilder b = builder.table(Tables.BOOKS)
                        .where(BookContract.BooksColumns.BOOK_ID +"=?", bookId);
                Log.i(TAG, "Builder: "+ b.toString());
                return b;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private void notifyChange(Uri uri) {
        Context context = getContext();
        context.getContentResolver().notifyChange(uri, null);
        //context.sendBroadcast();
    }

    private interface Qualified {
        String BOOKS_BOOK_ID = Tables.BOOKS + "." + BookContract.Books.BOOK_ID;
    }
}
