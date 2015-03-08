package com.x22.bookcollection.bookapi;

import android.os.Bundle;
import android.util.Log;

import com.x22.bookcollection.provider.BookContract;
import com.x22.bookcollection.util.Utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

// http://theagiledirector.com/getRest_v3.php?isbn=9789044340303
public class AmazonEntryHandler extends DefaultHandler {

    private static final String ITEM = "Item";
    private static final String AUTHOR = "Author";
    private static final String ISBN = "isbn";
    private static final String ISBN13 = "isbn13";
    private static final String TITLE = "Title";
    private static final String PUBLICATION_DATE = "PublicationDate";
    private static final String PUBLISHER = "Publisher";
    private static final String PAGES = "NumberOfPages";
    private static final String DESCRIPTION = "Content";
    private static final String BINDING = "Binding";
    private static final String LANGUAGE = "Language";
    private static final String NAME = "Name";
    public static String THUMBNAIL = "URL";
    public static String SMALL_IMAGE = "SmallImage";
    public static String MEDIUM_IMAGE = "MediumImage";
    public static String LARGE_IMAGE = "LargeImage";

    private StringBuilder builder = new StringBuilder();

    private boolean done = false;
    private boolean entry = false;
    private boolean inLanguage = false;

    private boolean hasThumbnail = false;
    private int thumbnailSize = -1;
    private String thumbnailUrl = "";

    private Bundle bookData = null;

    public AmazonEntryHandler(Bundle bookData) {
        this.bookData = bookData;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();


        boolean fetchThumbnail = true;
        if(fetchThumbnail) {

            Log.i("BookCollection", "****************************: thumbnailUrl: " + thumbnailUrl);

            if(thumbnailUrl.length() > 0) {
                String filename = Utils.saveCoverFromUrl(thumbnailUrl, "_AM");
                if(filename.length() > 0 && bookData != null) {
                    Utils.appendOrAdd(bookData, "__thumbnail", filename);
                }
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        builder.setLength(0);

        if(!done && localName.equalsIgnoreCase(ITEM)) {
            entry = true;
        } else if(localName.equalsIgnoreCase(SMALL_IMAGE)) {
            if(thumbnailSize < 1) {
                hasThumbnail = true;
                thumbnailSize = 1;
            }
        } else if(localName.equalsIgnoreCase(MEDIUM_IMAGE)) {
            if(thumbnailSize < 2) {
                hasThumbnail = true;
                thumbnailSize = 2;
            }
        } else if(localName.equalsIgnoreCase(LARGE_IMAGE)) {
            if(thumbnailSize < 3) {
                hasThumbnail = true;
                thumbnailSize = 3;
            }
        } else if(localName.equalsIgnoreCase(LANGUAGE)) {
            inLanguage = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if(localName.equalsIgnoreCase(ITEM)) {
            done = true;
            entry = false;
        } else if(localName.equalsIgnoreCase(THUMBNAIL)) {
            if(hasThumbnail) {
                thumbnailUrl = builder.toString();
                hasThumbnail = false;
            }
        } else if(localName.equalsIgnoreCase(LANGUAGE)) {
            inLanguage = false;
        } else if(entry) {
            if(localName.equalsIgnoreCase(AUTHOR)) {
                Utils.appendOrAdd(bookData, BookContract.BooksColumns.AUTHORS, builder.toString());
            } else if(localName.equalsIgnoreCase(TITLE)) {
                Utils.addIfNotPresent(bookData, BookContract.BooksColumns.TITLE, builder.toString());
            } else if(localName.equalsIgnoreCase(ISBN)) {
                Utils.appendOrAdd(bookData, BookContract.BooksColumns.ISBN, builder.toString());
            } else if(localName.equalsIgnoreCase(ISBN13)) {
                Utils.appendOrAdd(bookData, BookContract.BooksColumns.ISBN, builder.toString());
            } else if(localName.equalsIgnoreCase(PUBLISHER)) {
                Utils.addIfNotPresent(bookData, BookContract.BooksColumns.PUBLISHER, builder.toString());
            } else if(localName.equalsIgnoreCase(PUBLICATION_DATE)) {
                Utils.addIfNotPresent(bookData, BookContract.BooksColumns.PUBLICATION_DATE, builder.toString());
            } else if(localName.equalsIgnoreCase(PAGES)) {
                Utils.addIfNotPresentOrEqual(bookData, BookContract.BooksColumns.PAGES, builder.toString());
            } else if(localName.equalsIgnoreCase(DESCRIPTION)) {
                Utils.addIfNotPresent(bookData, BookContract.BooksColumns.DESCRIPTION, builder.toString());
            } else if(localName.equalsIgnoreCase(BINDING)) {
                Utils.addIfNotPresent(bookData, BookContract.BooksColumns.BINDING, builder.toString());
            } else if(inLanguage && localName.equalsIgnoreCase(NAME)) {
                Utils.addIfNotPresent(bookData, BookContract.BooksColumns.LANGUAGE, builder.toString());
            }
        }

        builder.setLength(0);
    }
}
