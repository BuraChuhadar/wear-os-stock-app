package com.example.stockmarket;

import static com.example.stockmarket.SymbolSearchActivity.EXTRA_SYMBOL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.stockmarket.databinding.ActivityMainBinding;
import com.example.stockmarket.helpers.Stock;
import com.example.stockmarket.interfaces.IAlphaVantageAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity {
    private TextView stockSymbolTextView;
    public static final int REQUEST_CODE_SEARCH_SYMBOL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stockSymbolTextView = (TextView) findViewById(R.id.txt_StockInformation);

        Button searchButton = findViewById(R.id.btnAdd);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SymbolSearchActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SEARCH_SYMBOL);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEARCH_SYMBOL && resultCode == RESULT_OK) {
            String symbol = data.getStringExtra(SymbolSearchActivity.EXTRA_SYMBOL);
            fetchSymbols(symbol);
        }
    }

    private void fetchSymbols(String symbol) {
        // Use a library like Retrofit to make the API call
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.alphavantage.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IAlphaVantageAPI api = retrofit.create(IAlphaVantageAPI.class);

        Call<Stock> call = api.getStock("GLOBAL_QUOTE", symbol, "S6TY517EE2WWB8H0");

        call.enqueue(new Callback<Stock>() {
            @Override
            public void onResponse(Call<Stock> call, Response<Stock> response) {
                if (response.isSuccessful()) {
                    Stock stock = response.body();
                    stockSymbolTextView.setText(stock.getGlobalQuote().getSymbol() + ":" + stock.getGlobalQuote().getPrice());
                } else {
                    // Handle error case
                    stockSymbolTextView.setText("Didn't work");
                }
            }

            @Override
            public void onFailure(Call<Stock> call, Throwable t) {
                // Handle failure case
            }
        });
    }

}