package com.example.stockmarket.helpers;

import com.google.gson.annotations.SerializedName;

public class Stock {
    @SerializedName("Global Quote")
    private GlobalQuote globalQuote;

    public GlobalQuote getGlobalQuote() {
        return globalQuote;
    }

    public static class GlobalQuote {
        @SerializedName("01. symbol")
        private String symbol;

        @SerializedName("02. open")
        private String open;

        @SerializedName("03. high")
        private String high;

        @SerializedName("04. low")
        private String low;

        @SerializedName("05. price")
        private String price;

        @SerializedName("06. volume")
        private String volume;

        @SerializedName("07. latest trading day")
        private String latestTradingDay;

        @SerializedName("08. previous close")
        private String previousClose;

        @SerializedName("09. change")
        private String change;

        @SerializedName("10. change percent")
        private String changePercent;

        public String getSymbol() {
            return symbol;
        }

        public String getOpen() {
            return open;
        }

        public String getHigh() {
            return high;
        }

        public String getLow() {
            return low;
        }

        public String getPrice() {
            return price;
        }

        public String getVolume() {
            return volume;
        }

        public String getLatestTradingDay() {
            return latestTradingDay;
        }

        public String getPreviousClose() {
            return previousClose;
        }

        public String getChange() {
            return change;
        }

        public String getChangePercent() {
            return changePercent;
        }
    }
}
