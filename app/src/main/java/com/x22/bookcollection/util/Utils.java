package com.x22.bookcollection.util;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.x22.bookcollection.R;
import com.x22.bookcollection.model.AuthorModel;
import com.x22.bookcollection.model.SerieModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {
    public static void replaceFragment (FragmentManager fragmentManager, Fragment fragment) {
        replaceFragment (fragmentManager, fragment, R.id.fragment_container);
    }

    public static void replaceFragment (FragmentManager fragmentManager, Fragment fragment, int containerViewId) {
        replaceFragment (fragmentManager, fragment, containerViewId, true);
    }

    public static void replaceFragment (FragmentManager fragmentManager, Fragment fragment, boolean addToBackStack) {
        replaceFragment (fragmentManager, fragment, R.id.fragment_container, addToBackStack);
    }

    public static void replaceFragment (FragmentManager fragmentManager, Fragment fragment, int containerViewId, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction ()
                .replace (containerViewId, fragment);

        if (addToBackStack) {
            fragmentTransaction.addToBackStack (fragment.toString ());
        }

        fragmentTransaction.commit ();
    }

    public static InputStream getInputStream(URL url) throws UnknownHostException {
        return getInputStream(url, 3);
    }

    public static InputStream getInputStream(URL url, int retries) throws UnknownHostException {
        synchronized (url) {
            int retriesLeft = 3;

            // TODO: Fix that this works instread of while(true)
            //while(retriesLeft > 0) {
            while(true) {
                try {
                    URLConnection connection = url.openConnection();
                    connection.setConnectTimeout(30000);
                    return connection.getInputStream();
                } catch(UnknownHostException e) {
                    Log.e("BookCollection", e.getMessage());
                    --retriesLeft;
                    if(retriesLeft == 0) {
                        throw e;
                    }

                    try {
                        Thread.sleep(500);
                    } catch(Exception f) {
                        Log.e("BookCollection", f.getMessage());
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void appendOrAdd(Bundle values, String key, String value) {
        appendOrAdd(values, key, value, '|');
    }

    public static void appendOrAdd(Bundle values, String key, String value, char delim) {
        String listItem = Utils.encodeListItem(value, delim);
        if(!values.containsKey(key) || values.getString(key).length() == 0) {
            values.putString(key, listItem);
        } else {
            if(!decodeList(values.getString(key), delim).contains(value)) {
                values.putString(key, values.getString(key) + delim + listItem);
            }
        }
    }

    public static void addIfNotPresent(Bundle values, String key, String value) {
        if(!values.containsKey(key) || values.getString(key).length() == 0) {
            values.putString(key, value);
        }
    }

    public static void addIfNotPresent(Bundle values, String key, int value) {
        if(!values.containsKey(key)) {
            values.putInt(key, value);
        }
    }

    public static void addIfNotPresentOrEqual(Bundle values, String key, String value) {
        if(!values.containsKey(key) || values.getString(key).length() == 0 || values.getString(key).equals(value)) {
            values.putString(key, value);
        }
    }

    public static String encodeListItem(String value, char delim) {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch(c) {
                case '\\':
                    builder.append("\\\\");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                default:
                    if (c == delim) {
                        builder.append("\\");
                    }
                    builder.append(c);
            }
        }
        return builder.toString();
    }

    public static ArrayList<String> decodeList(String value, char delim) {
        StringBuilder builder = new StringBuilder();
        ArrayList<String> list = new ArrayList<String>();
        boolean isEscapeChar = false;

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (isEscapeChar) {
                switch (c) {
                    case '\\':
                        builder.append(c);
                        break;
                    case 'r':
                        builder.append('\r');
                        break;
                    case 't':
                        builder.append('\t');
                        break;
                    case 'n':
                        builder.append('\n');
                        break;
                    default:
                        builder.append(c);
                        break;
                }
                isEscapeChar = false;
            } else {
                switch (c) {
                    case '\\':
                        isEscapeChar = true;
                        break;
                    default:
                        if (c == delim) {
                            list.add(builder.toString());
                            builder.setLength(0);
                            break;
                        } else {
                            builder.append(c);
                            break;
                        }
                }
            }
        }

        list.add(builder.toString());
        return list;
    }

    public static String encodeList(List<SerieModel> list) {
        return encodeList(list, '|');
    }

    public static String encodeList(List<SerieModel> list, char delim) {
        return encodeList(list.iterator(), delim);
    }

    public static String encodeList(Iterator<SerieModel> iterator, char delim) {
        StringBuilder builder = new StringBuilder();
        if(iterator.hasNext()) {
            builder.append(encodeListItem(iterator.next().toString(), delim));
            while(iterator.hasNext()) {
                builder.append(delim);
                builder.append(encodeListItem(iterator.next().toString(), delim));
            }
        }

        return builder.toString();
    }

    // TODO: Make generic
    public static ArrayList<AuthorModel> decodeListAuthors(String s, char delim, boolean allowBlank) {
        StringBuilder ns = new StringBuilder();
        ArrayList<AuthorModel> list = new ArrayList<AuthorModel>();
        if (s == null)
            return list;

        boolean inEsc = false;
        for (int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if (inEsc) {
                switch(c) {
                    case '\\':
                        ns.append(c);
                        break;
                    case 'r':
                        ns.append('\r');
                        break;
                    case 't':
                        ns.append('\t');
                        break;
                    case 'n':
                        ns.append('\n');
                        break;
                    default:
                        ns.append(c);
                        break;
                }
                inEsc = false;
            } else {
                switch (c) {
                    case '\\':
                        inEsc = true;
                        break;
                    default:
                        if (c == delim) {
                            String source = ns.toString();
                            if (allowBlank || source.length() > 0)
                                list.add(new AuthorModel(source));
                            ns.setLength(0);
                            break;
                        } else {
                            ns.append(c);
                            break;
                        }
                }
            }
        }
        // It's important to send back even an empty item.
        String source = ns.toString();
        if (allowBlank || source.length() > 0)
            list.add(new AuthorModel(source));
        return list;
    }

    // TODO: Make generic
    public static ArrayList<SerieModel> decodeListSeries(String s, char delim, boolean allowBlank) {
        StringBuilder ns = new StringBuilder();
        ArrayList<SerieModel> list = new ArrayList<SerieModel>();
        if (s == null)
            return list;

        boolean inEsc = false;
        for (int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if (inEsc) {
                switch(c) {
                    case '\\':
                        ns.append(c);
                        break;
                    case 'r':
                        ns.append('\r');
                        break;
                    case 't':
                        ns.append('\t');
                        break;
                    case 'n':
                        ns.append('\n');
                        break;
                    default:
                        ns.append(c);
                        break;
                }
                inEsc = false;
            } else {
                switch (c) {
                    case '\\':
                        inEsc = true;
                        break;
                    default:
                        if (c == delim) {
                            String source = ns.toString();
                            if (allowBlank || source.length() > 0)
                                list.add(new SerieModel(source));
                            ns.setLength(0);
                            break;
                        } else {
                            ns.append(c);
                            break;
                        }
                }
            }
        }
        // It's important to send back even an empty item.
        String source = ns.toString();
        if (allowBlank || source.length() > 0)
            list.add(new SerieModel(source));
        return list;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null) {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if(networkInfos != null) {
                for(NetworkInfo networkInfo : networkInfos) {
                    if(networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean validateIsbn10(String isbn) {
        if(isbn == null) {
            return false;
        }

        isbn = isbn.replaceAll("-", "");
        if(isbn.length() != 10) {
            return false;
        }

        try {
            int total = 0;
            for(int i = 0; i < 9; i++) {
                int digit = Integer.parseInt(isbn.substring(i, i + 1));
                total += ((10 - i) * digit);
            }

            String checksum = Integer.toString((11 - (total % 11)) % 11);
            if(checksum.equals("10")) {
                checksum = "X";
            }

            return checksum.equals(isbn.substring(9));
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public static boolean validateIsbn13(String isbn) {
        if(isbn == null) {
            return false;
        }

        isbn = isbn.replaceAll("-", "");
        if(isbn.length() != 13) {
            return false;
        }

        try {
            int total = 0;
            for(int i = 0; i < 12; i++) {
                int digit = Integer.parseInt(isbn.substring(i, i + 1));
                total += (i % 2 == 0) ? digit : digit * 3;
            }

            int checksum = 10 - (total % 10);
            if(checksum == 10) {
                checksum = 0;
            }

            return checksum == Integer.parseInt(isbn.substring(12));
        } catch(NumberFormatException e) {
            return false;
        }
    }

    /*public String downloadCover(String isbn, Bundle bookData, ImageSizes imageSize) {
        String imageUrl = "";
        switch(imageSize) {
            case SMALL:
                //
                break;

            case MEDIUM:
                imageUrl = COVER_URL_MEDIUM;
                break;

            case LARGE:
                //
                break;
        }

        waitUntilRequestAllowed();
        String filename = saveCoverFromUrl(String.format(imageUrl, API_KEY, isbn), String.format("_LT_%s_%s", imageSize, isbn));
        if(filename.length() > 0 && bookData != null) {
            Utils.appendOrAdd(bookData, "__cover", filename);
        }

        Log.i("BookCollection", "FN: "+ filename);

        return filename;
    }*/

    public static String saveCoverFromUrl(String coverUrl, String filename) {
        URL url;
        try {
            url = new URL(coverUrl);
        } catch(MalformedURLException e) {
            Log.e("BookCollection", e.getMessage());
            return "";
        }

        File coverFile;

        try {
            URLConnection connection = url.openConnection();
            connection.connect();

            String path = Environment.getExternalStorageDirectory() + "/bookcollection/";
            File folder = new File(path);
            if(!folder.exists()){
                folder.mkdir();
            }
            Log.i("BookCollection", "ImageDownloader > Path > "+ path + " | Folder > "+ folder);


            coverFile = Utils.getCoverFile(filename);
            InputStream inputStream = new BufferedInputStream(url.openStream());
            FileOutputStream outputStream = new FileOutputStream(coverFile);

            int count;
            byte data[] = new byte[1024];
            while ((count = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, count);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (UnknownHostException e) {
            Log.e("BookCollection", e.getMessage());
            return "";
        } catch (IOException e) {
            Log.e("BookCollection", e.getMessage());
            return "";
        }

        return coverFile.getAbsolutePath();
    }

    public static File getCoverFile(String filename) {
        return new File(String.format("%s/bookcollection/%s.jpg", Environment.getExternalStorageDirectory(), filename));
    }

   /*static public String saveThumbnailFromUrl(String urlText, String filenameSuffix) {
        // Get the URL
        URL u;
        try {
            u = new URL(urlText);
        } catch (MalformedURLException e) {
            Log.e("BookCollection", e.getMessage());
            e.printStackTrace();
            return "";
        }
        // Turn the URL into an InputStream
        InputStream in = null;
        try {
            HttpGet httpRequest = null;

            httpRequest = new HttpGet(u.toURI());

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);

            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            in = bufHttpEntity.getContent();

            // The defaut URL fetcher does not cope well with pages that have not content
            // header (including goodreads images!). So use the more advanced one.
            //c = (HttpURLConnection) u.openConnection();
            //c.setConnectTimeout(30000);
            //c.setRequestMethod("GET");
            //c.setDoOutput(true);
            //c.connect();
            //in = c.getInputStream();
        } catch (IOException e) {
            Log.e("BookCollection", e.getMessage());
            e.printStackTrace();
            return "";
        } catch (URISyntaxException e) {
            Log.e("BookCollection", e.getMessage());
            e.printStackTrace();
            return "";
        }

        // Get the output file
        File file = new File(getSharedStoragePath() + "/tmp" + filenameSuffix + ".jpg");
        // Save to file
        saveInputToFile(in, file);
        // Return new file path
        return file.getAbsolutePath();
    }

    /**
     * Given a InputStream, save it to a file.
     *
     * @param in		InputStream to read
     * @param out		File to save
     * @return			true if successful
     *
    static public boolean saveInputToFile(InputStream in, File out) {
        File temp = null;
        boolean isOk = false;

        try {
            // Get a temp file to avoid overwriting output unless copy works
            temp = File.createTempFile("temp_", null, getSharedStorage());
            FileOutputStream f = new FileOutputStream(temp);

            // Copy from input to temp file
            byte[] buffer = new byte[65536];
            int len1 = 0;
            while ( (len1 = in.read(buffer)) > 0 ) {
                f.write(buffer,0, len1);
            }
            f.close();
            // All OK, so rename to real output file
            temp.renameTo(out);
            isOk = true;
        } catch (FileNotFoundException e) {
            Log.e("BookCollection", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("BookCollection", e.getMessage());
            e.printStackTrace();
        } finally {
            // Delete temp file if it still exists
            if (temp != null && temp.exists())
                try { temp.delete(); } catch (Exception e) {};
        }
        return isOk;
    }

    private static final String LOCATION = "BookCollection";

    private static final String EXTERNAL_FILE_PATH = Environment.getExternalStorageDirectory() + "/" + LOCATION;

    public static final File getSharedStorage() {
        File dir = new File(EXTERNAL_FILE_PATH);
        dir.mkdir();
        return dir;
    }

    public static final String getSharedStoragePath() {
        File dir = new File(EXTERNAL_FILE_PATH);
        dir.mkdir();
        return dir.getAbsolutePath();
    }*/
}
