package com.networkapp.android.booklisting;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getName();

    private static URL createURl(String requestURL) {
        URL url = null;
        try {
            url = new URL(requestURL);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error in creating url", exception);
        }
        return url;
    }

    private static String readFromStream(InputStream inputStream) {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            try {
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            } catch (IOException exception) {
                Log.e(LOG_TAG, "Error in reading stream", exception);
            }
        }

        return output.toString();
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Response code Error" + urlConnection.getResponseCode());
            }
        } catch (IOException exception) {
            Log.e(LOG_TAG, "Error in make Http Connection", exception);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();

            if (inputStream != null)
                inputStream.close();
        }
        return jsonResponse;
    }

    private static ArrayList<Book> extractFeatureFromJson(String jsonResponse) {

        if (TextUtils.isEmpty(jsonResponse))
            return null;
        ArrayList<Book> books = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray items = root.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                String authors = getAuthorsAsString(authorsArray);
                books.add(new Book(title, authors));
            }
        } catch (JSONException exception) {
            Log.e(LOG_TAG, "Error in json extraction");
        }
        return books;
    }

    private static String getAuthorsAsString(JSONArray authorsArray) {

        StringBuilder output = new StringBuilder();
        try {
            for (int i = 0; i < authorsArray.length() - 1; i++)
                output.append(authorsArray.get(i)).append(" , ");

            output.append(authorsArray.get(authorsArray.length() - 1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public static ArrayList<Book> fetchBooksData(String requestURL) {
        URL url = createURl(requestURL);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<Book> books = extractFeatureFromJson(jsonResponse);
        return books;
    }

}
