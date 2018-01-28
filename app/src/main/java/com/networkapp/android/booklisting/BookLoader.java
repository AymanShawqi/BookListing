package com.networkapp.android.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

public class BookLoader extends AsyncTaskLoader<ArrayList<Book>> {
    private String mURL;

    public BookLoader(Context context, String url) {
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Book> loadInBackground() {
        if (mURL == null)
            return null;
        ArrayList<Book> books = QueryUtils.fetchBooksData(mURL);
        return books;
    }
}
