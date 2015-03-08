package com.x22.bookcollection.model;

import android.os.Bundle;

import com.x22.bookcollection.provider.BookContract;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SerieModel implements Cloneable, Comparable<SerieModel> {

    public long id;
    public long bookId;
    public String title;
    public String position;

    private java.util.regex.Pattern mPattern = java.util.regex.Pattern.compile("^(.*)\\s*\\((.*)\\)\\s*$");

    public SerieModel(String name) {
        java.util.regex.Matcher m = mPattern.matcher(name);
        if (m.find()) {
            this.title = m.group(1).trim();
            this.position = cleanupSeriesPosition(m.group(2));
        } else {
            this.title = name.trim();
            this.position = "";
        }
        this.id = 0L;
    }

    public SerieModel(Bundle serieData) {
        this(0L, serieData);
    }

    public SerieModel(long bookId, Bundle serieData) {
        this.bookId = bookId;
        title = serieData.getString(BookContract.SeriesColumns.TITLE);
        position = serieData.getString(BookContract.SeriesColumns.POSITION);
    }

    public String getDisplayTitle() {
        if(!position.equals("") || position.equals("-1")) {
            return String.format("%s (%s)", title, position);
        } else {
           return title;
        }
    }

    private static Pattern mSeriesPosCleanupPat = null;
    private static Pattern mSeriesIntegerPat = null;
    private static final String mSeriesNumberPrefixes = "(#|number|num|num.|no|no.|nr|nr.|book|bk|bk.|volume|vol|vol.|tome|part|pt.|)";

    public static String cleanupSeriesPosition(String position) {
        if (position == null)
            return "";
        position = position.trim();

        if (mSeriesPosCleanupPat == null) {
            // NOTE: Changes to this pattern should be mirrored in findSeries().
            String seriesExp = "^\\s*" + mSeriesNumberPrefixes + "\\s*([0-9\\.\\-]+|[ivxlcm\\.\\-]+)\\s*$";
            // Compile and get a reference to a Pattern object. <br>
            mSeriesPosCleanupPat = Pattern.compile(seriesExp, Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE);
        }
        if (mSeriesIntegerPat == null) {
            String numericExp = "^[0-9]+$";
            mSeriesIntegerPat = Pattern.compile(numericExp);
        }

        Matcher matcher = mSeriesPosCleanupPat.matcher(position);

        if (matcher.find()) {
            // Try to remove leading zeros.
            String pos = matcher.group(2);
            Matcher intMatch = mSeriesIntegerPat.matcher(pos);
            if (intMatch.find()) {
                return Long.parseLong(pos) + "";
            } else {
                return pos;
            }
        } else {
            return position;
        }
    }

    @Override
    public int compareTo(SerieModel another) {
        return 0;
    }

    @Override
    public String toString() {
        return getDisplayTitle();
    }
}
