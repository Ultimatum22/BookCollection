package com.x22.bookcollection.model;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.x22.bookcollection.provider.BookContract;
import com.x22.bookcollection.util.Utils;

import java.util.ArrayList;

public class BookModel {
    private long id;
    private String isbn;
    private String title;
    private ArrayList<AuthorModel> authors;
    private ArrayList<SerieModel> series;
    private String places;
    private String characters;
    private String publisher;
    private String publicationDate;
    private String pages;
    private String description;
    private String binding;
    private String language;
    private String avarageRating;
    private String ratingCount;
    private String uuid;

    public BookModel() {

    }

    public BookModel(long bookId, Bundle bookData) {
        Log.i("BookCollection", "bookData.toString(): " + bookData.toString());
        Log.i("BookCollection", "bookData.getString(BookContract.BooksColumns.PLACES): " + bookData.getString(BookContract.BooksColumns.PLACES));

        id = bookId;
        title = bookData.getString(BookContract.BooksColumns.TITLE);
        isbn = bookData.getString(BookContract.BooksColumns.ISBN);
        authors = Utils.decodeListAuthors(bookData.getString(BookContract.BooksColumns.AUTHORS), '|', false);
        places = bookData.getString(BookContract.BooksColumns.PLACES);
        series = Utils.decodeListSeries(bookData.getString(BookContract.BooksColumns.SERIES), '|', false);
        characters = bookData.getString(BookContract.BooksColumns.CHARACTERS);
        publicationDate = bookData.getString(BookContract.BooksColumns.PUBLICATION_DATE);
        publisher = bookData.getString(BookContract.BooksColumns.PUBLISHER);
        pages = bookData.getString(BookContract.BooksColumns.PAGES);
        description = bookData.getString(BookContract.BooksColumns.DESCRIPTION);
        binding = bookData.getString(BookContract.BooksColumns.BINDING);
        language = bookData.getString(BookContract.BooksColumns.LANGUAGE);
        avarageRating = bookData.getString(BookContract.BooksColumns.AVARAGE_RATING);
        ratingCount = bookData.getString(BookContract.BooksColumns.RATING_COUNT);
        uuid = bookData.getString(BookContract.BooksColumns.UUID);

        //Bundle serieBundle = bookData.getBundle(BookContract.BooksColumns.SERIES);
        //serieData = new SerieData(bookId, serieBundle);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<AuthorModel> getAuthors() {
        return authors;
    }

    public String getAuthorsDisplay() {
        if(authors.size() == 0) {
            return "[[ No author found ]]";
        } else if(authors.size() == 1) {
            return this.authors.get(0).toString();
        } else {
            return TextUtils.join(", ", authors);
        }
    }

    public void setAuthors(String authors) {
        this.authors = Utils.decodeListAuthors(authors, '|', false);
    }

    public void setAuthors(ArrayList<AuthorModel> authors) {
        this.authors = authors;
    }

    public ArrayList<SerieModel> getSeries() {
        return series;
    }

    public String getSeriesDisplay() {
        if(series.size() == 0) {
            return "[[ No serie found ]]";
        } else if(series.size() == 1) {
            return this.series.get(0).toString();
        } else {
            return TextUtils.join(", ", series);
        }
    }

    public void setSeries(String series) {
        this.series = Utils.decodeListSeries(series, '|', false);
    }

    public void setSeries(ArrayList<SerieModel> series) {
        this.series = series;
    }

    public String getPlaces() {
        return places;
    }

    public void setPlaces(String places) {
        this.places = places;
    }

    public String getCharacters() {
        return characters;
    }

    public void setCharacters(String characters) {
        this.characters = characters;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAvarageRating() {
        return avarageRating;
    }

    public void setAvarageRating(String avarageRating) {
        this.avarageRating = avarageRating;
    }

    public String getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(String ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return title;
    }
}