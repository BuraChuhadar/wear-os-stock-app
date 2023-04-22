package com.example.stockmarket.helpers.DTOs;

public class StockData {
    private final String lastPrice;
    private final String changeValue;
    private final String changePercentage;
    private final String symbol;

    public StockData(String symbol, String lastPrice, String changeValue, String changePercentage) {
        this.lastPrice = lastPrice;
        this.changeValue = changeValue;
        this.changePercentage = changePercentage;
        this.symbol = symbol;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public String getChangeValue() {
        return changeValue;
    }

    public String getChangePercentage() {
        return changePercentage;
    }

    public String getSymbol() {
        return symbol;
    }
}


