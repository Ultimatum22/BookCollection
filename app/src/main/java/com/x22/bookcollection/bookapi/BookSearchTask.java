package com.x22.bookcollection.bookapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.x22.bookcollection.model.AuthorModel;
import com.x22.bookcollection.model.SerieModel;

import com.x22.bookcollection.R;
import com.x22.bookcollection.provider.BookContract;
import com.x22.bookcollection.util.Utils;

import org.xml.sax.Attributes;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/////////
//// SEARCH_AMAZON, SEARCH_GOODREADS, SEARCH_GOOGLE, SEARCH_LIBRARY_THING
////////

public class BookSearchTask extends AsyncTask <String, Void, Boolean> {
    private static final String AMAZON_API_KEY = "";
    private static final String AMAZON_ASSOCIATE_TAG = "";
    private static final String AMAZON_SIGNATURE = "";
    private static final String GOODREADS_API_KEY = "vsInQR97jLm4NYQp7LcvBg";
    private static final String LIBRARY_THING_API_KEY = "c02718fd2135920d0cbc88b7fb8b75eb";

    private static final String LIBRARY_THING_COVER_URL_SMALL = "http://covers.librarything.com/devkey/%1$s/small/isbn/%2$s";
    private static final String LIBRARY_THING_COVER_URL_MEDIUM = "http://covers.librarything.com/devkey/%1$s/medium/isbn/%2$s";
    private static final String LIBRARY_THING_COVER_URL_LARGE = "http://covers.librarything.com/devkey/%1$s/large/isbn/%2$s";

    public static String AMAZON_DETAIL_URL = "http://theagiledirector.com/getRest_v3.php?isbn=%1$s";
    public static String GOODREADS_DETAIL_URL = "https://www.goodreads.com/book/isbn?key=%1$s&isbn=%2$s";
    //public static String AMAZON_DETAIL_URL = "http://webservices.amazon.com/onca/xml?Service=AWSECommerceService&Operation=ItemLookup&ResponseGroup=Large&SearchIndex=All&IdType=ISBN&ItemId=%1$s&AWSAccessKeyId=%2$s&AssociateTag=%3$s&Timestamp=%4$s&Signature=%5$s";
    public static String GOOGLE_BOOKS_DETAIL_URL = "http://books.google.com/books/feeds/volumes?q=ISBN:%1$s";
    public static String LIBRARY_THING_DETAIL_URL = "http://www.librarything.com/services/rest/1.1/?method=librarything.ck.getwork&apikey=%1$s&isbn=%2$s";

    private enum ImageSizes { SMALL, MEDIUM, LARGE }

    private Bundle bookData;
    private Bundle serieData;
    private Bundle authorData;

    private ProgressDialog dialog;

    private static Long lastRequestTime = 0L;

    private Activity activity;
    private BookSearchCallback bookSearchCallback;

    private ArrayList<String> searchMessages = new ArrayList<String>();

    public BookSearchTask(Activity activity, BookSearchCallback bookSearchCallback) {
        bookData = new Bundle();
        this.activity = activity;
        this.bookSearchCallback = bookSearchCallback;

        dialog = new ProgressDialog(activity);

        searchMessages.add(activity.getString(R.string.searching_amazon_books));
        searchMessages.add(activity.getString(R.string.searching_good_reads));
        searchMessages.add(activity.getString(R.string.searching_google_books));
        searchMessages.add(activity.getString(R.string.searching_library_thing));
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage(createDialogMessage());
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        if(Utils.validateIsbn10(params[0]) || Utils.validateIsbn13(params[0])) {
            searchAmazon(params[0]);
            searchGoodReads(params[0]);
            searchGoogleBooks(params[0]);
            searchLibraryThing(params[0]);

            return true;
        }

        return false;
    }

    private Runnable changeDialogMessageHandler = new Runnable() {
        @Override
        public void run() {
            dialog.setMessage(createDialogMessage());
        }
    };

    @Override
    protected void onPostExecute(Boolean result) {
        if(result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            bookSearchCallback.onSearchFinished(bookData);
        }
    }

    private void searchAmazon(String isbn) {
        try {
            URL url = new URL(String.format(AMAZON_DETAIL_URL, isbn));
            Log.i("BookCollection", "URL: " + url.toString());

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            //waitUntilRequestAllowed();
            parser.parse(Utils.getInputStream(url), new AmazonEntryHandler(bookData));
        } catch (Exception e) {
            Log.e("BookCollection", e.getMessage());
            searchError(R.string.searching_amazon_books, e);
        }

        activity.runOnUiThread(changeDialogMessageHandler);
        searchMessages.remove(0);
    }

    private void searchGoodReads(String isbn) {
        try {
            final ArrayList<AuthorModel> authors = new ArrayList<>();
            final ArrayList<SerieModel> series = new ArrayList<>();

            URL url = new URL(String.format(GOODREADS_DETAIL_URL, GOODREADS_API_KEY, isbn));
            Log.i("BookCollection", "URL: " + url.toString());

            final Bundle seriesDataBundle = new Bundle();
            
            //SAXParserFactory factory = SAXParserFactory.newInstance();
            //SAXParser parser = factory.newSAXParser();
            ///waitUntilRequestAllowed();
            //parser.parse(Utils.getInputStream(url), new GoodReadsEntryHandler(bookData));
            //SAXParserFactory factory = SAXParserFactory.newInstance();
            //SAXParser parser = factory.newSAXParser();
            ///waitUntilRequestAllowed();
            //File xmlFile = new File("/storage/emulated/0/goodreads.xml");
            //parser.parse(xmlFile, new GoodReadsEntryHandler(bookData));

            RootElement root = new RootElement("GoodreadsResponse");

            Log.i("BookCollection", "root.getChild(\"book\"): "+ root.getChild("book").toString());

            final Element bookElement = root.getChild("book");
            bookElement.getChild("title").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    Utils.addIfNotPresent(bookData, BookContract.BooksColumns.TITLE, body.trim());
                }
            });

            bookElement.getChild("isbn").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    if(body != null && !body.equals("")) {
                        Utils.appendOrAdd(bookData, BookContract.BooksColumns.ISBN, body.trim());
                    }
                }
            });

            bookElement.getChild("isbn13").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    if(body != null && !body.equals("")) {
                        Utils.appendOrAdd(bookData, BookContract.BooksColumns.ISBN, body.trim());
                    }
                }
            });

            bookElement.getChild("num_pages").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    if(body != null && !body.equals("")) {
                        Utils.addIfNotPresent(bookData, BookContract.BooksColumns.PAGES, body.trim());
                    }
                }
            });

            bookElement.getChild("average_rating").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    if(body != null && !body.equals("")) {
                        Utils.addIfNotPresent(bookData, BookContract.BooksColumns.AVARAGE_RATING, body.trim());
                    }
                }
            });

            bookElement.getChild("ratings_count").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    if(body != null && !body.equals("")) {
                        Utils.addIfNotPresent(bookData, BookContract.BooksColumns.RATING_COUNT, body.trim());
                    }
                }
            });

            bookElement.getChild("publisher").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    if(body != null && !body.equals("")) {
                        Utils.addIfNotPresent(bookData, BookContract.BooksColumns.PUBLISHER, body.trim());
                    }
                }
            });

            bookElement.getChild("description").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    Utils.addIfNotPresent(bookData, BookContract.BooksColumns.DESCRIPTION, body.trim());
                }
            });

            Element authorsElement = bookElement.getChild("authors");
            Element authorElement = authorsElement.getChild("author");

            authorElement.getChild("name").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    Utils.appendOrAdd(bookData, BookContract.BooksColumns.AUTHORS, body.trim());
                }
            });

            Element seriesWorksElement = bookElement.getChild("series_works");

            //List<SerieData> serieDataList = new ArrayList<SerieData>();

            Element seriesWorkElement = seriesWorksElement.getChild("series_work");
            seriesWorkElement.setStartElementListener(new StartElementListener() {
                @Override
                public void start(Attributes attributes) {
                    Log.i("BookCollection", "seriesWorkElement.setStartElementListener");
                    seriesDataBundle.clear();
                }
            });

            seriesWorkElement.setEndElementListener(new EndElementListener() {
                @Override
                public void end() {
                    Log.i("BookCollection", "seriesWorkElement.setEndElementListener");

                    SerieModel serieData = new SerieModel(seriesDataBundle);
                    series.add(serieData);

                    bookData.putString(BookContract.BooksColumns.SERIES, Utils.encodeList(series, '|'));
                    Log.i("BookCollection", ">> serieDataList encodeList1: "+ Utils.encodeList(series, '|'));
                }
            });

            seriesWorkElement.getChild("user_position").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    //Log.i("BookCollection", "user_position: "+ body.trim());
                    Utils.appendOrAdd(seriesDataBundle, BookContract.SeriesColumns.POSITION, body.trim());
                    Log.i("BookCollection", " >> user_position: "+ body.trim());
                    //serieData.setPosition(body.trim());
                }
            });

            Element seriesElement = seriesWorkElement.getChild("series");
            seriesElement.getChild("title").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    Utils.appendOrAdd(seriesDataBundle, BookContract.SeriesColumns.TITLE, body.trim());
                    //Log.i("BookCollection", " >> title: "+ body.trim());
                    //serieData.setTitle(body.trim());
                }
            });

            seriesElement.getChild("description").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    //Utils.appendOrAdd(seriesData, SerieContract.SerieEntry.COLUMN_NAME_DESCRIPTION, body.trim());
                }
            });

            //String serie = Utils.encodeListItem(serieData.getString(SerieContract.SerieEntry.COLUMN_NAME_TITLE), ';');// + Utils.encodeListItem(serieData.getString(SerieContract.SerieEntry.COLUMN_NAME_POSITION), ';') + Utils.encodeListItem(serieData.getString(SerieContract.SerieEntry.COLUMN_NAME_DESCRIPTION), ';');
            //bookData.putString(BookContract.BookEntry.COLUMN_NAME_SERIES, serie);

            Xml.parse(Utils.getInputStream(url), Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
            Log.e("BookCollection", e.getMessage());
            searchError(R.string.searching_good_reads, e);
        }

        activity.runOnUiThread(changeDialogMessageHandler);
        searchMessages.remove(0);
    }

    private void searchGoogleBooks(String isbn) {
        try {
            URL url = new URL(String.format(GOOGLE_BOOKS_DETAIL_URL, isbn));
            Log.i("BookCollection", "URL: " + url.toString());

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            //waitUntilRequestAllowed();

            GoogleBooksHandler handler = new GoogleBooksHandler();
            parser.parse(Utils.getInputStream(url), handler);
            if(handler.getCount() > 0) {
                Log.i("BookCollection", "handler.getId(): "+ handler.getId());
                url = new URL(handler.getId());
                parser.parse(Utils.getInputStream(url), new GoogleBooksEntryHandler(bookData));
            }
        } catch (Exception e) {
            Log.e("BookCollection", e.getMessage());
            searchError(R.string.searching_google_books, e);
        }

        activity.runOnUiThread(changeDialogMessageHandler);
        searchMessages.remove(0);
    }

    private void searchLibraryThing(String isbn) {
        try {
            URL url = new URL(String.format(LIBRARY_THING_DETAIL_URL, LIBRARY_THING_API_KEY, isbn));
            Log.i("BookCollection", "URL: " + url.toString());

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            //waitUntilRequestAllowed();
            parser.parse(Utils.getInputStream(url), new LibraryThingEntryHandler(bookData));

            downloadLibraryThingCover(isbn, ImageSizes.LARGE);
        } catch (Exception e) {
            Log.e("BookCollection", e.getMessage());
            searchError(R.string.searching_library_thing, e);
        }

        activity.runOnUiThread(changeDialogMessageHandler);
        searchMessages.remove(0);
    }

    private static void waitUntilRequestAllowed() {
        long wait;
        long now = System.currentTimeMillis();

        synchronized (lastRequestTime) {
            wait = 1000 - (now - lastRequestTime);
            if(wait < 0) {
                wait = 0;
            }
            lastRequestTime = now + wait;
        }
        
        if(wait > 0) {
            try {
                Thread.sleep(wait);
            } catch(InterruptedException e) {

            }
        }
    }

    private void downloadLibraryThingCover(String isbn, ImageSizes size) {
        String path = "";

        switch(size) {
            case SMALL:
                path = LIBRARY_THING_COVER_URL_SMALL;
                break;
            case MEDIUM:
                path = LIBRARY_THING_COVER_URL_MEDIUM;
                break;
            case LARGE:
                path = LIBRARY_THING_COVER_URL_LARGE;
                break;
        }

        if(path.length() > 0) {
            String thumbnailUrl = String.format(path, LIBRARY_THING_API_KEY, isbn);
            Log.i("BookCollection", "LIBRARY_THING thumbnailUrl: "+ thumbnailUrl);
            String filename = Utils.saveCoverFromUrl(thumbnailUrl, "_LT_" + size + "_" + isbn);
            if (filename.length() > 0 && bookData != null) {
                Utils.appendOrAdd(bookData, "__thumbnail", filename);
            }
        }
    }

    private String createDialogMessage() {
        String out = activity.getString(R.string.searching_book_data) + "\n";
        for(String searchMessage : searchMessages) {
            out += searchMessage +"\n";
        }

        return out;
    }

    private void searchError(final int searchId, final Exception e) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String exception;
                try {
                    exception = e.getMessage();
                } catch(Exception ex) {
                    exception = "Unknown Exception";
                }

                String message = String.format(activity.getString(R.string.search_exception), activity.getString(searchId), exception);
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}