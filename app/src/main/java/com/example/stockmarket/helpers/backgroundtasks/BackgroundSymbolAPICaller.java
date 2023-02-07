package com.example.stockmarket.helpers.backgroundtasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.stockmarket.helpers.DTOs.AlphavantageSymbol;
import com.example.stockmarket.helpers.DTOs.Symbol;
import com.example.stockmarket.helpers.RestAPIClient;

import java.util.ArrayList;
import java.util.List;

public class BackgroundSymbolAPICaller extends AsyncTask<String, Void, List<Symbol>> {
    private static final String TAG = "BackgroundSymbolAPICaller";

    @Override
    protected List<Symbol> doInBackground(String... params) {
        try {
            // Execute the RestAPI call in this method
            String apiToQuery = params[0];
            if (apiToQuery.equals("fetchSymbols")) {
                return new RestAPIClient().fetchSymbols(params[1]);
            }
        }
        catch (Exception e) {
            Log.e(TAG, "\"Error retrieving the symbols\"", e);
        }
        return null;
    }

    public List<Symbol> fetchSymbolsInBackground(String query) {

        try  {
            if (this.getStatus() == AsyncTask.Status.RUNNING) {
                this.cancel(true);
            }
            this.execute("fetchSymbols", query);
            return this.get();
        }
        catch (Exception e) {
            Log.e(TAG, "\"Error retrieving the symbols\"", e);
        }
        return new ArrayList<>();
    }
}
