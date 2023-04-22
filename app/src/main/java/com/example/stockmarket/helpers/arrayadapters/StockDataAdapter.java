package com.example.stockmarket;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.stockmarket.helpers.DTOs.StockData;

import java.util.List;

public class StockDataAdapter extends ArrayAdapter<StockData> {

    public StockDataAdapter(@NonNull Context context, int resource, @NonNull List<StockData> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        StockData stockData = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item, parent, false);
        }

        TextView stockSymbolTextView = convertView.findViewById(R.id.stock_symbol);
        TextView stockPriceTextView = convertView.findViewById(R.id.stock_price);
        TextView stockChangeTextView = convertView.findViewById(R.id.stock_change);

        stockSymbolTextView.setText(stockData.getSymbol());
        stockPriceTextView.setText(stockData.getLastPrice());
        String changeText = stockData.getChangeValue() + " " + stockData.getChangePercentage();
        stockChangeTextView.setText(changeText);

        if (stockData.getChangeValue().contains("-")) {
            stockChangeTextView.setTextColor(Color.RED);
        } else {
            stockChangeTextView.setTextColor(Color.GREEN);
        }

        return convertView;
    }
}
