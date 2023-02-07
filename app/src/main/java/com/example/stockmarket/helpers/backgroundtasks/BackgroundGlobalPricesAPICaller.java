package com.example.stockmarket.helpers.backgroundtasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.stockmarket.helpers.RestAPIClient;

import java.util.ArrayList;
import java.util.List;

public class BackgroundGlobalPricesAPICaller extends AsyncTask< List<String>, Void, List<String>> {
    private static final String TAG = "BackgroundSymbolAPICaller";

    @Override
    protected List<String> doInBackground(List<String>... params) {
        try {
            // Execute the RestAPI call in this method
            List<String> symbolList = params[0];
            RestAPIClient restAPIClient = new RestAPIClient();
            List<String> priceList = new ArrayList<>();
            for(int i=0;i<symbolList.size();i++) {
                priceList.add(restAPIClient.fetchGlobalPriceQuote(symbolList.get(i)));
            }
            return priceList;
        }
        catch (Exception e) {
            Log.e(TAG, "\"Error retrieving the symbols\"", e);
        }

        return null;
    }
    
    public List<String> fetchGlobalPriceQuotesInBackground(List<String> symbols) {
        try   {
            if (this.getStatus() == Status.RUNNING) {
                this.cancel(true);
            }
            this.execute(symbols);
            return this.get();
        }
        catch (Exception e) {
            Log.e(TAG, "\"Error retrieving the symbols\"", e);
        }
        return new ArrayList<>();
    }
}
