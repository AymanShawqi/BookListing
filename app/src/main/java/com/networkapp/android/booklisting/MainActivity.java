package com.networkapp.android.booklisting;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Book>> {
    private BookAdapter mAdapter;
    private LoaderManager loaderManager;
    private String urlRequest;
    private TextView noResultTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noResultTxt = (TextView) findViewById(R.id.no_result);
        ArrayList<Book> books = new ArrayList<>();
        ListView list = (ListView) findViewById(R.id.list);
        mAdapter = new BookAdapter(this, books);
        list.setAdapter(mAdapter);
        loaderManager = getLoaderManager();
    }

    @Override
    public Loader<ArrayList<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, urlRequest);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Book>> loader, ArrayList<Book> books) {
        mAdapter.clear();
        if (books != null || !books.isEmpty())
            mAdapter.addAll(books);
        else
            noResultTxt.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.content.Loader<ArrayList<Book>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    urlRequest = "https://www.googleapis.com/books/v1/volumes?maxResults=10&q=" + URLEncoder.encode(query, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                loaderManager.restartLoader(0, null, MainActivity.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noResultTxt.setVisibility(View.GONE);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
