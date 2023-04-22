package com.example.stockmarket.helpers.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.stockmarket.helpers.DTOs.StockData;
import com.example.stockmarket.helpers.DTOs.Symbol;
import com.example.stockmarket.helpers.RestAPIClient;

import java.util.List;

public class StockViewModel extends ViewModel {
    private final RestAPIClient restAPIClient;

    public StockViewModel(String apiKey) {
        this.restAPIClient = new RestAPIClient(apiKey);
    }

    public LiveData<List<Symbol>> fetchSymbols(String query) {
        return restAPIClient.fetchSymbols(query);
    }

    public LiveData<StockData> fetchGlobalPriceQuote(String symbol) {
        return restAPIClient.fetchGlobalPriceQuote(symbol, null);
    }

    public LiveData<List<StockData>> fetchGlobalPriceQuotes(List<String> symbols) {
        return restAPIClient.fetchGlobalPriceQuotes(symbols);
    }
}
