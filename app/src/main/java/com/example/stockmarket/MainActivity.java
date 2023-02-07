package com.example.stockmarket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stockmarket.helpers.RestAPIClient;
import com.example.stockmarket.helpers.backgroundtasks.BackgroundGlobalPriceAPICaller;
import com.example.stockmarket.helpers.backgroundtasks.BackgroundGlobalPricesAPICaller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private List<String> currentGlobalPriceQuoteList = new ArrayList<>();
    private List<String> currentGlobalSymbolList = new ArrayList<>();
    private ArrayAdapter<String> currentGlobalPriceQuoteLArrayAdapter;
    public static final int REQUEST_CODE_SEARCH_SYMBOL = 1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String SHARED_PREFS_KEY = "stock_information";
    private ListView listStockInformationListView;
    private MainActivity mainActivity;
    private ImageView loadingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        listStockInformationListView = (ListView) findViewById(R.id.list_StockInformationListView);
        loadingImage = (ImageView)findViewById(R.id.imageView);

        loadingImage.setVisibility(View.GONE);
        listStockInformationListView.setVisibility(View.VISIBLE);

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


        getSharedPreferencesInstance();
        currentGlobalSymbolList = retrieveDataListFromStorage();

        Log.d("Symbol fetch", "fetching symbols");
        LoadStockInformation(currentGlobalSymbolList, currentGlobalPriceQuoteList, false);
        currentGlobalPriceQuoteLArrayAdapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, currentGlobalPriceQuoteList);
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


    private void LoadStockInformation(List<String> symbols, List<String> existingPriceQuote, Boolean notifyChange) {
        loadingImage.setVisibility(View.VISIBLE);
        listStockInformationListView.setVisibility(View.GONE);

        List<String> updatedGlobalPriceQuoteList = new ArrayList<>();
        Boolean couldLoadAll = true;
        try {

            BackgroundGlobalPricesAPICaller backgroundGlobalPricesAPICaller = new BackgroundGlobalPricesAPICaller();
            List<String> globalPriceQuotes = backgroundGlobalPricesAPICaller.fetchGlobalPriceQuotesInBackground(symbols);
            for(int i=0;i<symbols.size();i++){
                Log.d("Symbol fetch", "fetching " + symbols.get(i) + globalPriceQuotes.get(i));
                if (!globalPriceQuotes.get(i).equals("")) {
                    BigDecimal bd = new BigDecimal(globalPriceQuotes.get(i)).setScale(2, RoundingMode.HALF_DOWN);
                    updatedGlobalPriceQuoteList.add(symbols.get(i) + " : " + bd.doubleValue());
                } else {
                    couldLoadAll = false;

                    updatedGlobalPriceQuoteList.add(symbols.get(i) + " : N/A");
                }
            }
        }
        catch (Exception exception) {
            Log.e(TAG, "\"Error retrieving the stock quote under MainActivity\"", exception);
        }

        if (!couldLoadAll) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.CouldNotRetrieveStockInformation), Toast.LENGTH_SHORT).show();
        }

        existingPriceQuote.clear();
        existingPriceQuote.addAll(updatedGlobalPriceQuoteList);
        if (notifyChange) {
            currentGlobalPriceQuoteLArrayAdapter.notifyDataSetChanged();
        }

        loadingImage.setVisibility(View.GONE);
        listStockInformationListView.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEARCH_SYMBOL && resultCode == RESULT_OK) {
            String symbol = data.getStringExtra(SymbolSearchActivity.EXTRA_SYMBOL);
            if (!currentGlobalSymbolList.contains(symbol)) {
                String globalPriceQuote = new BackgroundGlobalPriceAPICaller().fetchGlobalPriceQuoteInBackground(symbol);
                if (!globalPriceQuote.equals("")) {
                    BigDecimal bd=new BigDecimal(globalPriceQuote).setScale(2, RoundingMode.HALF_DOWN);
                    currentGlobalPriceQuoteList.add(symbol + " : " + bd.doubleValue());

                    currentGlobalSymbolList.add(symbol);
                    currentGlobalPriceQuoteLArrayAdapter.notifyDataSetChanged();
                    saveDataListToStorage(currentGlobalSymbolList);
                }
                else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.CouldNotRetrieveStockInformation), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }



    private void getSharedPreferencesInstance() {
        sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void saveDataListToStorage(List<String> stockInformation) {
        Set<String> yourDataSet = new HashSet<>(stockInformation);
        editor.putStringSet("stock_information_list", yourDataSet);
        editor.apply();
    }

    private List<String> retrieveDataListFromStorage() {
        Set<String> yourDataSet = sharedPreferences.getStringSet("stock_information_list", new HashSet<String>());
        return new ArrayList<>(yourDataSet);
    }
}