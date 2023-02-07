package com.example.stockmarket.helpers.arrayadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.stockmarket.helpers.DTOs.AlphavantageSymbol;
import com.example.stockmarket.helpers.DTOs.Symbol;

import java.util.List;

public class SymbolArrayAdapter<S> extends ArrayAdapter<Symbol> {
    private final List<Symbol> symbols;

    public SymbolArrayAdapter(Context context, List<Symbol> symbols) {
        super(context, 0, symbols);
        this.symbols = symbols;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Symbol symbol = symbols.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView symbolTextView = (TextView) convertView.findViewById(android.R.id.text1);
        symbolTextView.setText(symbol.getSymbol());

        return convertView;
    }
}
