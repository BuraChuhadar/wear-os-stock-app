package com.example.stockmarket.interfaces;

import com.example.stockmarket.helpers.Stock;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IAlphaVantageAPI {
    @GET("query")
    Call<Stock> getStock(@Query("function") String function,
                         @Query("symbol") String symbol,
                         @Query("apikey") String apikey);
}

