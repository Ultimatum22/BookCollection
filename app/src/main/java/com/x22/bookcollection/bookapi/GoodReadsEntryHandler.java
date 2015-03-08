package com.x22.bookcollection.bookapi;

import android.os.Bundle;

import com.x22.bookcollection.provider.BookContract;
import com.x22.bookcollection.util.Utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GoodReadsEntryHandler extends DefaultHandler {

    private static final String RESPONSE = "book";
    private static final String ITEM = "work";
    private static final String FACT = "fact";
    private static final String FIELD = "field";
    private static final String AUTHORS = "authors";
    private static final String TITLE = "title";
    private final static String DESCRIPTION = "description";
    private final static String PUBLISHER = "publisher";
    private static final String SERIES = "series";
    private static final String PLACES = "places";
    private static final String CHARACTERS = "characters";
    private static final String NAME = "name";

    private StringBuilder builder = new StringBuilder();

    private boolean done = false;
    private boolean isEntry = false;

    private boolean inAuthors = false;
    private boolean inSeries = false;

    private Bundle bookData = null;

    public GoodReadsEntryHandler(Bundle bookData) {
        this.bookData = bookData;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if(localName.equalsIgnoreCase(ITEM)) {
            isEntry = true;
        } else if(localName.equalsIgnoreCase(AUTHORS)){
            inAuthors = true;
        }

        builder.setLength(0);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if(localName.equalsIgnoreCase(ITEM)) {
            //done = true;
            isEntry = false;
        } else if(isEntry) {
            if(localName.equalsIgnoreCase(TITLE)) {
                Utils.appendOrAdd(bookData, BookContract.BooksColumns.TITLE, builder.toString().trim());
            } else if(localName.equalsIgnoreCase(DESCRIPTION)) {
                Utils.addIfNotPresent(bookData, BookContract.BooksColumns.DESCRIPTION, builder.toString().trim());
            } else if(localName.equalsIgnoreCase(PUBLISHER)) {
                Utils.addIfNotPresent(bookData, BookContract.BooksColumns.PUBLISHER, builder.toString().trim());
            /*} else if(localName.equalsIgnoreCase(LINK)) {
                if(userdata.tempBooks.get(lastBookPos).bookLink == null) {
                    userdata.tempBooks.get(lastBookPos).setBookLink(builder.toString().trim());
                }*/
            } else if(localName.equalsIgnoreCase(NAME)) {
                if(inAuthors) {
                    Utils.appendOrAdd(bookData, BookContract.BooksColumns.AUTHORS, builder.toString());
                }
            /*} else if(localName.equalsIgnoreCase(AVERAGE_RATING)) {

            } else if(localName.equalsIgnoreCase(SMALL_IMAGE_URL)) {
                String url = builder.toString().trim();
                if(!pastSmallImgUrl &&
                        url.substring(0, GoodReadsApp.GOODREADS_IMG_URL_LENGTH).compareTo(
                                GoodReadsApp.GOODREADS_IMG_URL) == 0) {
                    userdata.tempBooks.get(lastBookPos).imgUrl =
                            url.substring(GoodReadsApp.GOODREADS_IMG_URL_LENGTH);
                }
                pastSmallImgUrl = true;*/
            } else if(localName.equalsIgnoreCase(AUTHORS)) {
                inAuthors = false;
            } else {
    //            	Log.d(TAG, "tag: " + localName);
    //            	Log.d(TAG, "value: " + builder.toString().trim());
            }
        }

        /*if(localName.equalsIgnoreCase(ITEM)) {
            done = true;
            entry = false;
        } else if(entry) {
            //if (localName.equalsIgnoreCase(AUTHOR)) {
            //    Utils.appendOrAdd(bookData, BookContract.BooksColumns.AUTHORS, builder.toString());
            if (localName.equalsIgnoreCase(TITLE)) {
                Utils.addIfNotPresent(bookData, BookContract.BooksColumns.TITLE, builder.toString());
            }
        }*/

        builder.setLength(0);
    }
}
