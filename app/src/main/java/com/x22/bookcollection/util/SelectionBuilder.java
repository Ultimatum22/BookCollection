package com.x22.bookcollection.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SelectionBuilder {
    private static final String TAG = LogUtils.makeLogTag(SelectionBuilder.class);

    private String table = null;
    private Map<String, String> projectionMap = new HashMap<>();
    private StringBuilder selection = new StringBuilder();
    private ArrayList<String> selectionArgs = new ArrayList<>();
    private String groupBy = null;
    private String having = null;

    public SelectionBuilder where(String selection, String... selectionArgs) {
        if(TextUtils.isEmpty(selection)) {
            if (selectionArgs != null && selectionArgs.length > 0) {
                throw new IllegalArgumentException("Valid selection required when including arguments");
            }

            return this;
        }

        if(this.selection.length() > 0) {
            this.selection.append(" AND ");
        }

        this.selection.append("(").append(selection).append(")");
        if(selectionArgs != null) {
            Collections.addAll(this.selectionArgs, selectionArgs);
        }

        return this;
    }

    public Cursor query(SQLiteDatabase db, String[] columns, String orderBy) {
        return query(db, false, columns, orderBy, null);
    }

    public Cursor query(SQLiteDatabase db, boolean distinct, String[] columns, String orderBy, String limit) {
        if(table == null) {
            throw new IllegalStateException("Table not sepecified");
        }

        if(columns != null) {
            mapColumns(columns);
        }

        Log.d(TAG, "query(columns=" + Arrays.toString(columns) + ", distinct=" + distinct + ") " + this);
        return db.query(distinct, table, columns, getSelection(), getSelectionArgs(), groupBy, having, orderBy, limit);
    }

    public SelectionBuilder groupBy(String groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public String getSelection() {
        return selection.toString();
    }

    public String[] getSelectionArgs() {
        return selectionArgs.toArray(new String[selectionArgs.size()]);
    }

    private void mapColumns(String[] columns) {
        for(int i = 0; i < columns.length; i++) {
            final String target = projectionMap.get(columns[i]);
            if(target != null) {
                columns[i] = target;
            }
        }
    }

    public SelectionBuilder table(String table) {
        this.table = table;
        return this;
    }

    @Override
    public String toString() {
        return "SelectionBuilder[table=" + table + ", selection=" + getSelection() + ", selectionArgs=" + Arrays.toString(getSelectionArgs()) + ", projectionMap=" + projectionMap + "]";
    }
}
