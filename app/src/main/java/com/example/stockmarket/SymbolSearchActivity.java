package com.example.stockmarket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.stockmarket.helpers.DTOs.AlphavantageSymbol;
import com.example.stockmarket.helpers.DTOs.Symbol;
import com.example.stockmarket.helpers.SymbolFetcher;
import com.example.stockmarket.helpers.arrayadapters.SymbolArrayAdapter;
import com.example.stockmarket.helpers.backgroundtasks.BackgroundSymbolAPICaller;

import java.util.ArrayList;
import java.util.List;

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
                List<Symbol> symbols = new BackgroundSymbolAPICaller().fetchSymbolsInBackground(query);
                displaySymbols(symbols);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void displaySymbols(List<Symbol> symbols) {
        SymbolArrayAdapter<Symbol> adapter = new SymbolArrayAdapter<>(this, symbols);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Symbol symbol = (Symbol) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra(EXTRA_SYMBOL, symbol.getSymbol());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


}
