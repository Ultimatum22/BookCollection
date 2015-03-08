package com.x22.bookcollection.util;

import android.util.Log;

import com.x22.bookcollection.BuildConfig;

public class LogUtils {
    private static final String LOG_PREFIX = "BookCollection_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LEGNTH = 23;

    public static String makeLogTag(String tag) {
        if(tag.length() > MAX_LOG_TAG_LEGNTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + tag.substring(0, MAX_LOG_TAG_LEGNTH - LOG_PREFIX_LENGTH);
        }

        return LOG_PREFIX + tag;
    }

    public static String makeLogTag(Class cls) {
        return "BookCollection";
        //return makeLogTag(cls.getSimpleName());
    }
}
