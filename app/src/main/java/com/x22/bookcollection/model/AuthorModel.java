package com.x22.bookcollection.model;

public class AuthorModel implements Cloneable, Comparable<AuthorModel> {

    public long id;
    public long bookId;
    public String name;

    public AuthorModel() {

    }

    public AuthorModel(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(AuthorModel another) {
        return 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
