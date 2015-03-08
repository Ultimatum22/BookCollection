package com.x22.bookcollection.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.x22.bookcollection.R;
import com.x22.bookcollection.provider.BookContract;
import com.x22.bookcollection.util.LogUtils;

import java.io.File;

public class BooksFragment  extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = LogUtils.makeLogTag(BooksFragment.class);

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private GridView booksGridView;

    private ListAdapter adapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BooksFragment newInstance() {
        BooksFragment fragment = new BooksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public BooksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_books, container, false);
        booksGridView = (GridView) rootView.findViewById(R.id.books_grid_view);

        getLoaderManager().initLoader(BooksQuery.NORMAL_TOKEN, null, this);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached("All books");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        /*
     * Takes action based on the ID of the Loader that's being created
     */
        switch (loaderId) {
            case BooksQuery.NORMAL_TOKEN:
                // Returns a new CursorLoader
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        BookContract.Books.CONTENT_URI,        // Table to query
                        BooksQuery.NORMAL_PROJECTION,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter = new BooksAdapter(getActivity(), cursor);
        booksGridView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    public class BooksAdapter extends CursorAdapter {
        private Cursor cursor;

        public BooksAdapter(Context context, Cursor cur) {
            super(context, cur, false);
            cursor = cur;
            // TODO Auto-generated constructor stub
        }

        public int scaleColor(int color, float factor, boolean scaleAlpha) {
            return Color.argb(scaleAlpha ? (Math.round(Color.alpha(color) * factor)) : Color.alpha(color),
                    Math.round(Color.red(color) * factor), Math.round(Color.green(color) * factor),
                    Math.round(Color.blue(color) * factor));
        }

        public int scaleSessionColorToDefaultBG(int color) {
            return scaleColor(color, 0.75f, false);
        }

        public ColorFilter makeSessionImageScrimColorFilter(int sessionColor) {
            float a = 0.25f;
            float sat = 0.25f; // saturation (0=gray, 1=color)
            return new ColorMatrixColorFilter(new float[]{
                    ((1 - 0.213f) * sat + 0.213f) * a, ((0 - 0.715f) * sat + 0.715f) * a, ((0 - 0.072f) * sat + 0.072f) * a, 0, Color.red(sessionColor) * (1 - a),
                    ((0 - 0.213f) * sat + 0.213f) * a, ((1 - 0.715f) * sat + 0.715f) * a, ((0 - 0.072f) * sat + 0.072f) * a, 0, Color.green(sessionColor) * (1 - a),
                    ((0 - 0.213f) * sat + 0.213f) * a, ((0 - 0.715f) * sat + 0.715f) * a, ((1 - 0.072f) * sat + 0.072f) * a, 0, Color.blue(sessionColor) * (1 - a),
                    0, 0, 0, 0, 255
            });
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // TODO Auto-generated method stub

            final String bookId = cursor.getString(BooksQuery.BOOK_ID);
            if(bookId == null) {
                return;
            }

            Log.i(TAG, "cursor.getString(BooksQuery.TITLE): "+ cursor.getString(BooksQuery.TITLE));
            TextView titleView = (TextView) view.findViewById(R.id.book_title);
            titleView.setText(cursor.getString(BooksQuery.TITLE));

            ImageView photoView = (ImageView) view.findViewById(R.id.book_photo_colored);
            //photoView.setColorFilter(makeSessionImageScrimColorFilter(-16611119));
            //new PorterDuffColorFilter(getResources().getColor(R.color.no_track_branding_session_tile_overlay), PorterDuff.Mode.SRC_ATOP));

            File coverFile = new File(String.format("%s/bookcollection/%s.jpg", Environment.getExternalStorageDirectory(), cursor.getString(BooksQuery.UUID)));
            Log.i(TAG, "cursor.getString(BooksQuery.TITLE): " + cursor.getString(BooksQuery.UUID) + " - coverFile: " + coverFile.getAbsolutePath());
            if(coverFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(coverFile.getAbsolutePath());
                photoView.setImageBitmap(myBitmap);
            } else {
                photoView.setVisibility(View.INVISIBLE);
                photoView.setMinimumHeight(0);
            }

            FrameLayout bookTargetView = (FrameLayout) view.findViewById(R.id.book_target);
            /*bookTargetView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallbacks.onSessionSelected(sessionId, finalPhotoView);
                }
            });*/
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.list_item_book_summarized, parent, false);
            bindView(view, context, cursor);
            return view;
        }


        public int getCount() {
            // TODO Auto-generated method stub
            //return cursor.getCount();
            return 5;
        }


        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        /* or used this getView()
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			// TODO Auto-generated method stub
			CountryHolder holder = null;

			if (convertView == null) {

				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.activity_column, null);

				holder = new CountryHolder();
				holder.ID = (TextView) convertView.findViewById(R.id.ColID);
				holder.Code = (TextView) convertView.findViewById(R.id.ColCode);
				holder.Country = (TextView) convertView
						.findViewById(R.id.ColCountry);

				convertView.setTag(holder);
			} else {
				holder = (CountryHolder) convertView.getTag();
			}

			cursor.moveToPosition(position);
			holder.ID.setText(cursor.getString(cursor.getColumnIndex("ID")));
			holder.Code.setText(cursor.getString(cursor.getColumnIndex("Code")));
			holder.Country.setText(cursor.getString(cursor.getColumnIndex("Country")));

			return convertView;

		}
		*/


    }

    public class CountryHolder {
        TextView ID;
        TextView Code;
        TextView Country;
    }

    private interface BooksQuery {
        int NORMAL_TOKEN = 0x1;

        String[] NORMAL_PROJECTION = {
                BaseColumns._ID,
                BookContract.Books.BOOK_ID,
                BookContract.Books.ISBN,
                BookContract.Books.TITLE,
                BookContract.Books.PLACES,
                BookContract.Books.CHARACTERS,
                BookContract.Books.PUBLISHER,
                BookContract.Books.PUBLICATION_DATE,
                BookContract.Books.PAGES,
                BookContract.Books.DESCRIPTION,
                BookContract.Books.BINDING,
                BookContract.Books.LANGUAGE,
                BookContract.Books.AVARAGE_RATING,
                BookContract.Books.RATING_COUNT,
                BookContract.Books.UUID,
        };

        int _ID = 0;
        int BOOK_ID = 1;
        int TITLE = 3;
        int UUID = 14;
        int PHOTO_URL = 3;
    }
}