package com.x22.bookcollection.bookapi;

import android.os.Bundle;

import com.x22.bookcollection.provider.BookContract;
import com.x22.bookcollection.util.Utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

// http://www.librarything.com/services/rest/1.1/?method=librarything.ck.getwork&apikey=c02718fd2135920d0cbc88b7fb8b75eb&isbn=9789044340303
public class LibraryThingEntryHandler extends DefaultHandler {

    private static final String RESPONSE = "response";
    private static final String ITEM = "item";
    private static final String FACT = "fact";
    private static final String FIELD = "field";
    private static final String AUTHOR = "author";
    private static final String TITLE = "canonicalTitle";
    private static final String SERIES = "series";
    private static final String PLACES = "places";
    private static final String CHARACTERS = "characters";

    private enum FieldTypes { NONE, AUTHOR, TITLE, SERIES, PLACES, CHARACTERS, OTHER }

    private StringBuilder builder = new StringBuilder();

    private FieldTypes fieldType = FieldTypes.OTHER;
    private Bundle bookData = null;

    public LibraryThingEntryHandler(Bundle bookData) {
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

        builder.setLength(0);

        if(localName.equalsIgnoreCase(RESPONSE)) {
            //
        } else if(localName.equalsIgnoreCase(ITEM)) {
            //
        } else if(localName.equalsIgnoreCase(FIELD)) {
            String fieldName = attributes.getValue("", "name");
            if(fieldName != null) {
                if(fieldName.equalsIgnoreCase(TITLE)) {
                    fieldType = FieldTypes.TITLE;
                //} else if(fieldName.equalsIgnoreCase(SERIES)) {
                //    fieldType = FieldTypes.SERIES;
                } else if(fieldName.equalsIgnoreCase(PLACES)) {
                    fieldType = FieldTypes.PLACES;
                } else if(fieldName.equalsIgnoreCase(CHARACTERS)) {
                    fieldType = FieldTypes.CHARACTERS;
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if(localName.equalsIgnoreCase(FIELD)) {
            fieldType = FieldTypes.NONE;
        } else if(localName.equalsIgnoreCase(AUTHOR)) {
            Utils.appendOrAdd(bookData, BookContract.BooksColumns.AUTHORS, builder.toString());
        } else if(localName.equalsIgnoreCase(FACT)) {
            switch(fieldType) {
                case TITLE:
                    Utils.addIfNotPresent(bookData, BookContract.BooksColumns.TITLE, builder.toString());
                    break;

                case SERIES:
                    Utils.appendOrAdd(bookData, BookContract.BooksColumns.SERIES, builder.toString());

                case PLACES:
                    Utils.appendOrAdd(bookData, BookContract.BooksColumns.PLACES, builder.toString());

                case CHARACTERS:
                    Utils.appendOrAdd(bookData, BookContract.BooksColumns.CHARACTERS, builder.toString());
            }
        }

        builder.setLength(0);
    }


}