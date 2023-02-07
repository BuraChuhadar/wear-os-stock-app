package com.example.stockmarket.helpers.DTOs;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListSymbols {
    private int count;
    private List<Symbol> result = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Symbol> getResult() {
        return result;
    }

    public void setResult(List<Symbol> result) {
        this.result = result;
    }
}