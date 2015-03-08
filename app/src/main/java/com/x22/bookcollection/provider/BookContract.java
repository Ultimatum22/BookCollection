package com.x22.bookcollection.provider;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

public class BookContract {

    public interface BooksColumns {
        String BOOK_ID = "book_id";
        String ISBN = "isbn";
        String TITLE = "title";
        String AUTHORS = "authors";
        String SERIES = "series";
        String PLACES = "places";
        String CHARACTERS = "characters";
        String PUBLISHER = "publisher";
        String PUBLICATION_DATE = "publication_date";
        String PAGES = "pages";
        String DESCRIPTION = "description";
        String BINDING = "binding";
        String LANGUAGE = "language";
        String AVARAGE_RATING = "avarage_rating";
        String RATING_COUNT = "rating_count";
        String UUID = "uuid";
    }

    public interface AuthorsColumns {
        String AUTHOR_ID = "author_id";
        String NAME = "name";
    }

    public interface SeriesColumns {
        String SERIE_ID = "serie_id";
        String TITLE = "title";
        String POSITION = "position";
        String DESCRIPTION = "description";
    }

    public interface BooksAuthors {
        String BOOK_ID = "book_id";
        String AUTHOR_ID = "author_id";
    }

    public interface BooksSeries {
        String BOOK_ID = "book_id";
        String SERIE_ID = "serie_id";
    }

    public static final String CONTENT_AUTHORITY = "com.x22.bookcollection";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_BOOKS = "books";
    private static final String PATH_SEARCH = "search";

    public static class Books implements BooksColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.bookcollection.book";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.bookcollection.book";

        public static final String SORT_BY_BOOK_ID = "book_id ASC";

        public static Uri buildBookUri(String bookId) {
            return CONTENT_URI.buildUpon().appendPath(bookId).build();
        }

        public static Uri buildSearchUri(String query) {
            if(query == null) {
                query = "";
            }

            // convert "lorem ipsum dolor sit" to "lorem* ipsum* dolor* sit*"
            query = query.replaceAll(" +", " *") + "*";
            return CONTENT_URI.buildUpon().appendPath(PATH_SEARCH).appendPath(query).build();
        }

        public static String getBookId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class Authors implements AuthorsColumns, BaseColumns {

    }

    public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
    }
}
