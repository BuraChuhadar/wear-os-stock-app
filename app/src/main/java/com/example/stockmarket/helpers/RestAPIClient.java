package com.example.stockmarket.helpers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.stockmarket.helpers.DTOs.ListSymbols;
import com.example.stockmarket.helpers.DTOs.Quote;
import com.example.stockmarket.helpers.DTOs.StockData;
import com.example.stockmarket.helpers.DTOs.Symbol;
import com.example.stockmarket.interfaces.IStockAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class RestAPIClient {
    private static final String TAG = "RestAPICLient";

    private static final String CNBC_BASE_URL = "https://www.cnbc.com/quotes/";
    private OkHttpClient httpClient;

    private final IStockAPI api;
    private final String apiKey;
    public RestAPIClient(String apiKey) {
        this.apiKey = apiKey;
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://finnhub.io/api/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        this.api = retrofit.create(IStockAPI.class);
        httpClient = new OkHttpClient();
    }

    public LiveData<StockData> fetchGlobalPriceQuote(String symbol, QuoteCallback callback) {
        MutableLiveData<StockData> quoteLiveData = new MutableLiveData<>();
        OkHttpClient client = new OkHttpClient();

        // Build the request URL
        String url = CNBC_BASE_URL + symbol;

        // Create the request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Enqueue the request asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, IOException e) {
                Log.e(TAG, "Error retrieving the stock quote", e);
                quoteLiveData.postValue(null);
                if(callback != null) {
                    callback.onError("Error fetching stock quote");
                }
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Parse the HTML content to retrieve the actual stock price
                    // Use your parsing logic here
                    assert response.body() != null;
                    StockData stockData = parseStockPriceFromHtml(symbol, response.body().string());

                    quoteLiveData.postValue(stockData);
                    if(callback != null) {
                        callback.onSuccess(stockData);
                    }
                } else {
                    quoteLiveData.postValue(null);
                    if(callback != null) {
                        callback.onError("Error fetching stock quote");
                    }
                }
            }
        });

        return quoteLiveData;
    }

    private StockData parseStockPriceFromHtml(String symbol, String html) {
        Document document = Jsoup.parse(html);

        String lastPrice = document.select(".QuoteStrip-lastPrice").first().text();
        String changeValue = "";
        String changePercentage = "";

        // Check if the stock price went up
        if (!document.select(".QuoteStrip-changeUp").isEmpty()) {
            Elements changeContainer = document.select(".QuoteStrip-changeUp");
            changeValue = changeContainer.select("span").get(1).text();
            changePercentage = changeContainer.select("span").get(2).text();
        }
        // Check if the stock price went down
        else if (!document.select(".QuoteStrip-changeDown").isEmpty()) {
            Elements changeContainer = document.select(".QuoteStrip-changeDown");
            changeValue = changeContainer.select("span").get(1).text();
            changePercentage = changeContainer.select("span").get(2).text();
        }

        return new StockData(symbol, lastPrice, changeValue, changePercentage);
    }



    public LiveData<List<StockData>> fetchGlobalPriceQuotes(List<String> symbols) {
        MutableLiveData<List<StockData>> quotesLiveData = new MutableLiveData<>();
        List<StockData> quotesList = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(symbols.size());

        QuoteCallback callback = new QuoteCallback() {
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error retrieving the stock quote: " + errorMessage);
                latch.countDown();
            }

            @Override
            public void onSuccess(StockData stockData) {
                quotesList.add(stockData);
                latch.countDown();
            }
        };

        for (String symbol : symbols) {
            fetchGlobalPriceQuote(symbol, callback);
        }

        new Thread(() -> {
            try {
                latch.await();
                quotesLiveData.postValue(quotesList);
            } catch (InterruptedException e) {
                Log.e(TAG, "Error waiting for all quotes to be fetched", e);
            }
        }).start();

        return quotesLiveData;
    }


    public LiveData<List<Symbol>> fetchSymbols(String query) {
        MutableLiveData<List<Symbol>> symbolsLiveData = new MutableLiveData<>();

        Call<ListSymbols> call = api.searchSymbol(query, apiKey);
        call.enqueue(new Callback<ListSymbols>() {
            @Override
            public void onResponse(@NonNull Call<ListSymbols> call, @NonNull Response<ListSymbols> response) {
                if (response.isSuccessful()) {
                    ListSymbols symbols = response.body();
                    assert symbols != null;
                    symbolsLiveData.postValue(symbols.getResult());
                } else {
                    symbolsLiveData.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListSymbols> call, Throwable t) {
                Log.e(TAG, "Error retrieving the symbols", t);
                symbolsLiveData.postValue(new ArrayList<>());
            }
        });

        return symbolsLiveData;
    }

    public interface QuoteCallback {
        void onSuccess(StockData stockData);
        void onError(String error);
    }

    public interface SymbolsCallback {
        void onSuccess(List<Symbol> symbols);
        void onError(String error);
    }
}