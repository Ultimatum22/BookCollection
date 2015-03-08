package com.x22.bookcollection.bookapi;

import android.os.Bundle;

import com.x22.bookcollection.provider.BookContract;
import com.x22.bookcollection.util.Utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GoogleBooksEntryHandler extends DefaultHandler {

    private static final String ENTRY = "entry";
    private static final String AUTHOR = "creator";
    private static final String TITLE = "title";
    private static final String DATE_PUBLISHED = "date";
    private static final String PUBLISHER = "publisher";
    private static final String THUMBNAIL = "link";
    private static final String ISBN = "identifier";

    private StringBuilder builder = new StringBuilder();

    private Bundle bookData = null;

    public GoogleBooksEntryHandler(Bundle bookData) {
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

        boolean fetchThumbnail = false;
        if(fetchThumbnail) {
            if(localName.equalsIgnoreCase(THUMBNAIL)) {
                if (attributes.getValue("", "rel").equals("http://schemas.google.com/books/2008/thumbnail")) {
                    String thumbnail = attributes.getValue("", "href");
                    String filename = Utils.saveCoverFromUrl(thumbnail, "_GB");
                    if(filename.length() > 0 && bookData != null) {
                        Utils.appendOrAdd(bookData, "__thumbnail", filename);
                    }
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if(localName.equalsIgnoreCase(TITLE)) {
            Utils.addIfNotPresent(bookData, BookContract.BooksColumns.TITLE, builder.toString());
        } else if(localName.equalsIgnoreCase(AUTHOR)) {
            Utils.appendOrAdd(bookData, BookContract.BooksColumns.AUTHORS, builder.toString());
        } else if(localName.equalsIgnoreCase(DATE_PUBLISHED)) {
            Utils.appendOrAdd(bookData, BookContract.BooksColumns.PUBLICATION_DATE, builder.toString());
        } else if(localName.equalsIgnoreCase(PUBLISHER)) {
            Utils.appendOrAdd(bookData, BookContract.BooksColumns.PUBLISHER, builder.toString());
        }

        builder.setLength(0);
    }
}
