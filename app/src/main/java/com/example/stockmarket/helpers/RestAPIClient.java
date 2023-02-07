package com.example.stockmarket.helpers;

import android.util.Log;

import com.example.stockmarket.helpers.DTOs.ListSymbols;
import com.example.stockmarket.helpers.DTOs.Quote;
import com.example.stockmarket.helpers.DTOs.AlphavantageSymbol;
import com.example.stockmarket.helpers.DTOs.Symbol;
import com.example.stockmarket.interfaces.IStockAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestAPIClient {

    private static final String TAG = "RestAPICLient";

    public String fetchGlobalPriceQuote(String symbol) {
        Gson gson = new GsonBuilder().setLenient().create();
        // Use a library like Retrofit to make the API call
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://finnhub.io/api/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        IStockAPI api = retrofit.create(IStockAPI.class);

        Call<Quote> call = api.getQuote(symbol, "YOUR_API_KEY");

        try {
            Quote quote = call.execute().body();
            if (quote != null) {
                return String.valueOf(quote.getC());
            }

        } catch (Exception exception) {
            Log.e(TAG, "\"Error retrieving the stock quote\"", exception);
        }
        return "";
    }

    public List<Symbol> fetchSymbols(String query) {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://finnhub.io/api/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        IStockAPI api = retrofit.create(IStockAPI.class);

      //  Call<ListSymbols> call = api.searchSymbol(query, "cfhtk6hr01qmbsr4tu70cfhtk6hr01qmbsr4tu7g");

        try {
            Call<ListSymbols> call = api.searchSymbol(query, "cfhtk6hr01qmbsr4tu70cfhtk6hr01qmbsr4tu7g");
            Response<ListSymbols> response = call.execute();
            if (response.isSuccessful()) {
                ListSymbols symbols = response.body();
                String json = new Gson().toJson(symbols);
                Log.d("JSON", json);
                return symbols.getResult();
            }


        }
        catch (IOException exception) {
            Log.e(TAG, "\"Error retrieving the symbols\"", exception);
        }
        return new ArrayList<>();
    }
}
