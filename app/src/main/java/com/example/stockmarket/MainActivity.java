package com.example.stockmarket;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.ViewModelStore;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;


import com.example.stockmarket.helpers.DTOs.StockData;
import com.example.stockmarket.helpers.viewmodels.StockViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements ViewModelStoreOwner{
    private static final String TAG = "MainActivity";
    private final List<StockData> currentGlobalPriceQuoteList = new ArrayList<>();
    private List<String> currentGlobalSymbolList = new ArrayList<>();
    private ArrayAdapter<StockData> currentGlobalPriceQuoteLArrayAdapter;
    public static final int REQUEST_CODE_SEARCH_SYMBOL = 1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String SHARED_PREFS_KEY = "stock_information";
    private ListView listStockInformationListView;
    private MainActivity mainActivity;
    private ImageView loadingImage;
    private StockViewModel stockViewModel;

    @Override
    public ViewModelStore getViewModelStore() {
        return super.getViewModelStore();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the app to use dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);

        loadingImage = (ImageView)findViewById(R.id.imageView);

        if (loadingImage != null) {
            Glide.with(this)
                    .asGif()
                    .load(R.drawable.loader_loading)
                    .listener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                            Log.e(TAG, "GIF loading failed: ", e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.i(TAG, "GIF loaded successfully");
                            return false;
                        }
                    })
                    .into(loadingImage);
        } else {
            Log.e(TAG, "imageView is null");
        }

        mainActivity = this;
        listStockInformationListView = (ListView) findViewById(R.id.list_StockInformationListView);

        SetLoading(View.GONE, View.VISIBLE);

        listStockInformationListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getResources().getString(R.string.DeleteItemTitle))
                        .setMessage(getResources().getString(R.string.DeleteItemConfirmation))
                        .setPositiveButton(getResources().getString(R.string.DeleteItemYes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currentGlobalSymbolList.remove(position);
                                currentGlobalPriceQuoteList.remove(position);
                                currentGlobalPriceQuoteLArrayAdapter.notifyDataSetChanged();
                                saveDataListToStorage(currentGlobalSymbolList);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.DeleteItemNo), null)
                        .show();
                return true;
            }
        });

        stockViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(StockViewModel.class)) {
                    return (T) new StockViewModel(MainActivity.this.getResources().getString(R.string.Finnhub_API_KEY));
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(StockViewModel.class);


        getSharedPreferencesInstance();
        currentGlobalSymbolList = retrieveDataListFromStorage();

        Log.d("Symbol fetch", "fetching symbols");
        LoadStockInformation(currentGlobalSymbolList, currentGlobalPriceQuoteList, false);
        currentGlobalPriceQuoteLArrayAdapter = new com.example.stockmarket.StockDataAdapter(mainActivity, R.layout.custom_list_item, currentGlobalPriceQuoteList);
        listStockInformationListView.setAdapter(currentGlobalPriceQuoteLArrayAdapter);
        Button searchButton = findViewById(R.id.btnAdd);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SymbolSearchActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SEARCH_SYMBOL);
            }
        });

        ImageButton refreshButton = findViewById(R.id.btnRefresh);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadStockInformation(currentGlobalSymbolList, currentGlobalPriceQuoteList, true);
            }
        });
    }

    private void SetLoading(int gone, int visible) {
        loadingImage.setVisibility(gone);
        listStockInformationListView.setVisibility(visible);
    }


    private void LoadStockInformation(List<String> symbols, List<StockData> existingPriceQuotes, Boolean notifyChange) {
        SetLoading(View.VISIBLE, View.GONE);

        stockViewModel.fetchGlobalPriceQuotes(symbols).observe(this, stockDataList -> {
            List<StockData> updatedGlobalPriceQuoteList = new ArrayList<>();
            boolean couldLoadAll = true;

            for (int i = 0; i < symbols.size(); i++) {
                Log.d("Symbol fetch", "fetching " + symbols.get(i) + stockDataList.get(i));
                StockData stockData = stockDataList.get(i);
                if (stockData != null && !stockData.getLastPrice().equals("")) {
                    updatedGlobalPriceQuoteList.add(stockData);
                } else {
                    couldLoadAll = false;
                    StockData emptyStockData = new StockData(symbols.get(i), "N/A", "", "");
                    updatedGlobalPriceQuoteList.add(emptyStockData);
                }
            }

            if (!couldLoadAll) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.CouldNotRetrieveStockInformation), Toast.LENGTH_SHORT).show();
            }

            existingPriceQuotes.clear();
            existingPriceQuotes.addAll(updatedGlobalPriceQuoteList);
            if (notifyChange) {
                currentGlobalPriceQuoteLArrayAdapter.notifyDataSetChanged();
            }

            SetLoading(View.GONE, View.VISIBLE);
        });
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SetLoading(View.VISIBLE, View.GONE);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEARCH_SYMBOL && resultCode == RESULT_OK) {
            String symbol = data.getStringExtra(SymbolSearchActivity.EXTRA_SYMBOL);
            if (!currentGlobalSymbolList.contains(symbol)) {
                stockViewModel.fetchGlobalPriceQuote(symbol).observe((LifecycleOwner) MainActivity.this, stockData -> {
                    if (stockData != null && !stockData.getLastPrice().equals("")) {
                        currentGlobalPriceQuoteList.add(stockData);

                        currentGlobalSymbolList.add(symbol);
                        currentGlobalPriceQuoteLArrayAdapter.notifyDataSetChanged();
                        saveDataListToStorage(currentGlobalSymbolList);
                    } else {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.CouldNotRetrieveStockInformation), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        SetLoading(View.GONE, View.VISIBLE);
    }

    private void getSharedPreferencesInstance() {
        sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void saveDataListToStorage(List<String> stockInformation) {
        Set<String> stockInformationHashSet = new HashSet<>(stockInformation);
        editor.putStringSet("stock_information_list", stockInformationHashSet);
        editor.apply();
    }

    private List<String> retrieveDataListFromStorage() {
        Set<String> stockInformationHashSet = sharedPreferences.getStringSet("stock_information_list", new HashSet<>());
        return new ArrayList<>(stockInformationHashSet);
    }
}