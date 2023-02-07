package com.example.stockmarket.helpers.backgroundtasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.stockmarket.helpers.RestAPIClient;

public class BackgroundGlobalPriceAPICaller extends AsyncTask<String, Void, String> {
    private static final String TAG = "BackgroundSymbolAPICaller";

    @Override
    protected String doInBackground(String... params) {
        try {
            // Execute the RestAPI call in this method
            String apiToQuery = params[0];
            if (apiToQuery.equals("fetchGlobalPriceQuote")) {
                return new RestAPIClient().fetchGlobalPriceQuote(params[1]);
            }
        }
        catch (Exception e) {
            Log.e(TAG, "\"Error retrieving the symbols\"", e);
        }

        return null;
    }

    public String fetchGlobalPriceQuoteInBackground(String symbol) {
        try   {
            if (this.getStatus() == AsyncTask.Status.RUNNING) {
                this.cancel(true);
            }
            this.execute("fetchGlobalPriceQuote", symbol);
            return this.get();
        }
        catch (Exception e) {
            Log.e(TAG, "\"Error retrieving the symbols\"", e);
        }
        return "";
    }
}
