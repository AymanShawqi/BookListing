package com.networkapp.android.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private TextView mEmptyStateTextView;
    private View mLoadingIndicator;
    private ConnectivityManager connectivityMgr;
    private NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator=findViewById(R.id.loading_indicator);
        ListView list = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        list.setEmptyView(mEmptyStateTextView);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        list.setAdapter(mAdapter);

        connectivityMgr=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        loaderManager = getLoaderManager();
    }

    @Override
    public Loader<ArrayList<Book>> onCreateLoader(int i, Bundle bundle) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        return new BookLoader(this, urlRequest);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Book>> loader, ArrayList<Book> books) {
        mLoadingIndicator.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.no_result);
        mAdapter.clear();
        if (books != null && !books.isEmpty())
            mAdapter.addAll(books);

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
                 mEmptyStateTextView.setText("");
                 mAdapter.clear();
                 networkInfo=connectivityMgr.getActiveNetworkInfo();
                if (networkInfo !=null && networkInfo.isConnected()){
                    try {
                        urlRequest = "https://www.googleapis.com/books/v1/volumes?maxResults=10&q=" + URLEncoder.encode(query, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    loaderManager.restartLoader(0, null, MainActivity.this);
                }
                else {
                    mEmptyStateTextView.setText(R.string.no_internet);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
