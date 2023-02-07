package com.example.stockmarket.interfaces;

import com.example.stockmarket.helpers.DTOs.ListSymbols;
import com.example.stockmarket.helpers.DTOs.Quote;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IStockAPI {

    @GET("quote")
    Call<Quote> getQuote(@Query("symbol") String symbol, @Query("token") String apiKey);

    @GET("search")
    Call<ListSymbols> searchSymbol(@Query("q") String query, @Query("token") String apiKey);
}

