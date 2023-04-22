package com.example.stockmarket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelStore;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.stockmarket.helpers.DTOs.Symbol;
import com.example.stockmarket.helpers.arrayadapters.SymbolArrayAdapter;
import com.example.stockmarket.helpers.viewmodels.StockViewModel;

import java.util.ArrayList;
import java.util.List;

public class SymbolSearchActivity extends AppCompatActivity implements ViewModelStoreOwner {
    private SearchView searchView;
    private ListView listView;
    private ArrayList<String> symbols;
    private ArrayAdapter<String> adapter;
    public static final String EXTRA_SYMBOL = "EXTRA_SYMBOL";
    private StockViewModel stockViewModel;

    private ImageView loadingImage;

    private ListView listStockInformationListView;

    private final ViewModelStore viewModelStore = new ViewModelStore();

    @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symbol_search);

        loadingImage = (ImageView)findViewById(R.id.imageView);
        listStockInformationListView = (ListView) findViewById(R.id.list_view);

        if (loadingImage != null) {
            Glide.with(this)
                    .asGif()
                    .load(R.drawable.loader_loading)
                    .listener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                            Log.e(EXTRA_SYMBOL, "GIF loading failed: ", e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.i(EXTRA_SYMBOL, "GIF loaded successfully");
                            return false;
                        }
                    })
                    .into(loadingImage);
        } else {
            Log.e(EXTRA_SYMBOL, "imageView is null");
        }

        searchView = (SearchView) findViewById(R.id.search_view);
        listView = (ListView) findViewById(R.id.list_view);

        symbols = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, symbols);
        listView.setAdapter(adapter);


        stockViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(StockViewModel.class)) {
                    return (T) new StockViewModel(SymbolSearchActivity.this.getResources().getString(R.string.Finnhub_API_KEY));
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(StockViewModel.class);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SetLoading(View.VISIBLE, View.GONE);
                // make a request to the API with the query as the symbol
                // update the symbols list with the results
                // update the adapter
                stockViewModel.fetchSymbols(query).observe(SymbolSearchActivity.this, new Observer<List<com.example.stockmarket.helpers.DTOs.Symbol>>() {
                    @Override
                    public void onChanged(List<com.example.stockmarket.helpers.DTOs.Symbol> symbols) {
                        SetLoading(View.GONE, View.VISIBLE);
                        // Handle the list of symbols here
                        displaySymbols(symbols);
                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        SetLoading(View.GONE, View.VISIBLE);
    }

    private void SetLoading(int gone, int visible) {
        loadingImage.setVisibility(gone);
        listStockInformationListView.setVisibility(visible);
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
