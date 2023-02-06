package com.example.stockmarket.helpers;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SymbolFetcher extends AsyncTask<String, Void, String> {

    private static final String BASE_URL = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=";
    private static final String API_KEY = "S6TY517EE2WWB8H0";

    @Override
    protected String doInBackground(String... params) {
        StringBuilder jsonResult = new StringBuilder();
        try {


            HttpURLConnection connection = null;

            try {
                URL url = new URL(BASE_URL + params[0] + "&apikey=" + API_KEY);
                connection = (HttpURLConnection) url.openConnection();
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());

                int read;
                char[] buffer = new char[1024];
                while ((read = reader.read(buffer)) != -1) {
                    jsonResult.append(buffer, 0, read);
                }
            } catch (MalformedURLException e) {
                Log.e("SymbolFetcher", "Error processing API URL", e);
            } catch (IOException e) {
                Log.e("SymbolFetcher", "Error connecting to API", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResult.toString();
    }

    public ArrayList<String> fetchSymbols(String query) {

        this.execute(query);
        ArrayList<String> symbols = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(this.get());
            JSONArray results = json.getJSONArray("bestMatches");

            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                symbols.add(result.getString("1. symbol"));
            }
        } catch (JSONException e) {
            Log.e("SymbolFetcher", "Error processing JSON response", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return symbols;
    }
}