package com.example.stockmarket.helpers.fetchers;

import android.content.Context;
import android.util.Log;

import com.example.stockmarket.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//Symbol fetcher using alphavantage.com not being used in the code
public class SymbolFetcher {

    private static final String BASE_URL = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=";

    private final WeakReference<Context> contextRef;

    public SymbolFetcher(Context context) {
        this.contextRef = new WeakReference<>(context);
    }

    public ArrayList<String> fetchSymbols(String query) {
        Executor executor = Executors.newSingleThreadExecutor();
        SymbolFetcherRunnable runnable = new SymbolFetcherRunnable(query);
        executor.execute(runnable);
        return runnable.getSymbols();
    }

    private class SymbolFetcherRunnable implements Runnable {
        private final String query;
        private ArrayList<String> symbols;

        public SymbolFetcherRunnable(String query) {
            this.query = query;
        }

        public ArrayList<String> getSymbols() {
            return symbols;
        }

        @Override
        public void run() {
            StringBuilder jsonResult = new StringBuilder();
            Context context = contextRef.get();

            if (context == null) {
                return;
            }

            HttpURLConnection connection = null;

            try {
                URL url = new URL(BASE_URL + query + "&apikey=" + context.getResources().getString(R.string.Alphavantage_API_KEY));
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

            try {
                JSONObject json = new JSONObject(jsonResult.toString());
                JSONArray results = json.getJSONArray("bestMatches");

                symbols = new ArrayList<>();

                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    symbols.add(result.getString("1. symbol"));
                }
            } catch (JSONException e) {
                Log.e("SymbolFetcher", "Error processing JSON response", e);
            }
        }
    }
}
