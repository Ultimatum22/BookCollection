package com.x22.bookcollection.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.x22.bookcollection.R;
import com.x22.bookcollection.model.BookModel;

import java.util.Calendar;

public class EditBookFragment extends Fragment {

    private static final String ARG_BOOK_ID = "bookId";
    private static final String ARG_BOOK_BUNDLE = "bookBundle";

    private static TextView datePublished;

    private long bookId;
    private BookModel bookData;
    private Bundle bookDataBundle;

    public static EditBookFragment newInstance(long bookId, Bundle bookBundle) {
        EditBookFragment fragment = new EditBookFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_BOOK_ID, bookId);
        args.putBundle(ARG_BOOK_BUNDLE, bookBundle);
        fragment.setArguments(args);
        return fragment;
    }

    public EditBookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookId = getArguments().getLong(ARG_BOOK_ID);
            bookDataBundle = getArguments().getBundle(ARG_BOOK_BUNDLE);

            if(bookDataBundle != null) {
                bookData = new BookModel(bookId, bookDataBundle);
            } else {
                //bookData = databaseHandler.getBook(bookId);
            }

            Log.i("BookCollection", "bookId: " + bookId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_book, container, false);

        TextView titleText = (TextView) view.findViewById(R.id.title);
        titleText.setText(bookData.getTitle());

        TextView subtitleText = (TextView) view.findViewById(R.id.subtitle);
        //subtitleText.setText(bookData.getSubtitle());

        TextView authorsText = (TextView) view.findViewById(R.id.authors);
        authorsText.setText(bookData.getAuthorsDisplay());

        TextView seriesText = (TextView) view.findViewById(R.id.series);
        seriesText.setText(bookData.getSeriesDisplay());

        TextView publisherText = (TextView) view.findViewById(R.id.publisher);
        publisherText.setText(bookData.getPublisher());

        TextView isbnText = (TextView) view.findViewById(R.id.isbn);
        isbnText.setText(bookData.getIsbn());

        datePublished = (TextView) view.findViewById(R.id.date_published);
        datePublished.setText(bookData.getPublicationDate());

        TextView descriptionText = (TextView) view.findViewById(R.id.description);
        descriptionText.setText(bookData.getDescription());

        ImageView authorsButton = (ImageView) view.findViewById(R.id.authors_button);
        authorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Authors button", Toast.LENGTH_LONG).show();
                //Utils.replaceFragment(getFragmentManager(), AuthorsOverviewFragment.newInstance(bookId, bookData.getAuthors()));
            }
        });

        ImageView seriessButton = (ImageView) view.findViewById(R.id.series_button);
        seriessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Series button", Toast.LENGTH_LONG).show();
            }
        });

        ImageView datePublishedButton = (ImageView) view.findViewById(R.id.date_published_button);
        datePublishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), "datePicker");
            }
        });

        Button cancelButton = (Button) view.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        Button addButton = (Button) view.findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //saveBook();
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached("Edit book");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            //datePublished.setText(dateFormat.format(String.format("%s-%s-%s", day, month, year)));
            datePublished.setText(String.format("%s/%s/%s", day, month + 1, year));
        }
    }
}