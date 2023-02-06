package com.example.stockmarket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.stockmarket.helpers.Stock;
import com.example.stockmarket.helpers.SymbolFetcher;
import com.example.stockmarket.interfaces.IAlphaVantageAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SymbolSearchActivity extends Activity {
    private SearchView searchView;
    private ListView listView;
    private ArrayList<String> symbols;
    private ArrayAdapter<String> adapter;
    private SymbolFetcher symbolFetcher;
    public static final String EXTRA_SYMBOL = "EXTRA_SYMBOL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symbol_search);

        searchView = (SearchView) findViewById(R.id.search_view);
        listView = (ListView) findViewById(R.id.list_view);

        symbols = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, symbols);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // make a request to the API with the query as the symbol
                // update the symbols list with the results
                // update the adapter
                List<String> symbols = new SymbolFetcher().fetchSymbols(query);
                displaySymbols(symbols);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void displaySymbols(List<String> symbols) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, symbols);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String symbol = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra(EXTRA_SYMBOL, symbol);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


}
