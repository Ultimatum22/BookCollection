package com.x22.bookcollection.ui;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.x22.bookcollection.R;
import com.x22.bookcollection.bookapi.BookSearchCallback;
import com.x22.bookcollection.bookapi.BookSearchTask;
import com.x22.bookcollection.model.BookModel;
import com.x22.bookcollection.provider.BookContract;
import com.x22.bookcollection.util.LogUtils;
import com.x22.bookcollection.util.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private String TAG = LogUtils.makeLogTag(SearchFragment.class);

    private OnFragmentInteractionListener mListener;

    private static final String ARG_ISBN = "isbn";

    private String isbn;

    public static SearchFragment newInstance(String isbn) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ISBN, isbn);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            isbn = getArguments().getString(ARG_ISBN);
        }

        searchBook(isbn);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached("Search fragment");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private BookSearchCallback bookSearchCallback = new BookSearchCallback() {
        @Override
        public boolean onSearchFinished(Bundle bookData) {
            return SearchFragment.this.onSearchFinished(bookData);
        }
    };

    private boolean onSearchFinished(Bundle bookData) {
        Log.i("BookCollection", "+++++++++++++++++ BookData >> " + bookData.toString());

        long bookId = 0;

        Utils.replaceFragment(getActivity().getFragmentManager(), EditBookFragment.newInstance(bookId, bookData));

//        Intent editBookIntent = new Intent(this, EditBookActivity.class);
//        editBookIntent.putExtra("bookData", bookData);
//
//        startActivityForResult(editBookIntent, 22);

        return true;
    }

    public void searchBook(final String isbn) {
        if(isbn != null && !isbn.equals("")) {
//            DatabaseHelper databaseHelper = new DatabaseHelper(this);
//            final long bookId = databaseHelper.getIdFromIsbn(isbn);
//            if(bookId > 0) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle(R.string.book_already_exists_title);
//                builder.setMessage(R.string.book_already_exists_message);
//                builder.setNegativeButton(getString(R.string.add_book), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        try {
//                            new BookSearchTask(BarcodeScannerActivity.this, bookSearchCallback).execute(isbn);
//                        } catch (Exception e) {
//                            finish();
//                        }
//                    }
//                });
//                builder.setNeutralButton(getString(R.string.edit_book), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent editBookIntent = new Intent(BarcodeScannerActivity.this, EditBookActivity.class);
//                        editBookIntent.putExtra("bookId", bookId);
//                        startActivity(editBookIntent);
//                    }
//                });
//                builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        IntentIntegrator integrator = new IntentIntegrator(BarcodeScannerActivity.this);
//                        integrator.initiateScan();
//                    }
//                });
//                builder.show();
//            } else {
                try {
                    new BookSearchTask(getActivity(), bookSearchCallback).execute(isbn);
                } catch (Exception e) {
//                    finish();
                }
//            }
        } else {
//            IntentIntegrator integrator = new IntentIntegrator(SearchFragment.this);
//            integrator.initiateScan();
        }
    }
}
