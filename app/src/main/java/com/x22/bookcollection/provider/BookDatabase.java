package com.x22.bookcollection.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.x22.bookcollection.util.LogUtils;

public class BookDatabase extends SQLiteOpenHelper {
    private static final String TAG = LogUtils.makeLogTag(BookDatabase.class);

    private static final String DATABASE_NAME = "BookCollection.db";
    private static final int DATABASE_VERSION = 1;

    public BookDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ Tables.BOOKS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BookContract.BooksColumns.BOOK_ID + " TEXT NOT NULL,"
                + BookContract.BooksColumns.ISBN + " TEXT,"
                + BookContract.BooksColumns.TITLE + " TEXT,"
                + BookContract.BooksColumns.PLACES + " TEXT,"
                + BookContract.BooksColumns.CHARACTERS + " TEXT,"
                + BookContract.BooksColumns.PUBLISHER + " TEXT,"
                + BookContract.BooksColumns.PUBLICATION_DATE + " TEXT,"
                + BookContract.BooksColumns.PAGES + " TEXT,"
                + BookContract.BooksColumns.DESCRIPTION + " TEXT,"
                + BookContract.BooksColumns.BINDING + " TEXT,"
                + BookContract.BooksColumns.LANGUAGE + " TEXT,"
                + BookContract.BooksColumns.AVARAGE_RATING + " TEXT,"
                + BookContract.BooksColumns.RATING_COUNT + " TEXT,"
                + BookContract.BooksColumns.UUID + " TEXT NOT NULL DEFAULT (lower(hex(randomblob(16)))),"
                + " UNIQUE (" + BookContract.BooksColumns.BOOK_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.AUTHORS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BookContract.AuthorsColumns.AUTHOR_ID + " TEXT NOT NULL,"
                + BookContract.AuthorsColumns.NAME + " TEXT,"
                + " UNIQUE (" + BookContract.AuthorsColumns.AUTHOR_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.SERIES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BookContract.SeriesColumns.SERIE_ID + " TEXT NOT NULL,"
                + BookContract.SeriesColumns.TITLE + " TEXT,"
                + " UNIQUE (" + BookContract.SeriesColumns.SERIE_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.BOOKS_AUTHORS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BookContract.BooksAuthors.BOOK_ID + " TEXT NOT NULL "+ References.BOOK_ID + ","
                + BookContract.BooksAuthors.AUTHOR_ID + " TEXT NOT NULL "+ References.AUTHOR_ID + ","
                + " UNIQUE (" + BookContract.BooksAuthors.BOOK_ID + "," + BookContract.BooksAuthors.AUTHOR_ID + ") ON CONFLICT REPLACE)"


        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    interface Tables {
        String BOOKS = "books";
        String AUTHORS = "authors";
        String BOOKS_AUTHORS = "books_authors";
        String SERIES = "series";
        String BOOKS_SERIES = "books_series";

        String BOOKS_AUTHORS_JOIN_BOOKS = "books_authors "
                + " LEFT OUTER JOIN books ON books_authors.book_id = books._id";
        String BOOK_SERIES_JOIN_SERIES = "book_series "
                + " LEFT OUTER JOIN series ON book_series.serie_id = series._id";
    }

    private interface References {
        String BOOK_ID = "REFERENCES "+ Tables.BOOKS + "(" + BookContract.Books.BOOK_ID + ")";
        String AUTHOR_ID = "REFERENCES "+ Tables.AUTHORS + "(" + BookContract.Authors.AUTHOR_ID + ")";
    }
}
