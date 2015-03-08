package com.x22.bookcollection.bookapi;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GoogleBooksHandler extends DefaultHandler {

    private static final String ID = "id";
    private static final String TOTAL_RESULTS = "totalResults";
    private static final String ENTRY = "entry";

    private int count = 0;
    private String id = "";

    private boolean entry = false;
    private boolean done = false;

    private StringBuilder builder = new StringBuilder();

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        builder.setLength(0);

        if(!done && localName.equalsIgnoreCase(ENTRY)) {
            entry = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if(localName.equalsIgnoreCase(TOTAL_RESULTS)) {
            count = Integer.parseInt(builder.toString());
        }

        if(localName.equalsIgnoreCase(ENTRY)) {
            entry = false;
            done = true;
        }

        if(entry && id.equals("")) {
            if(localName.equalsIgnoreCase(ID)) {
                id = builder.toString();
            }
        }

        builder.setLength(0);
    }

    public String getId() {
        return id;
    }

    public int getCount() {
        return count;
    }
}
