package com.example.stockmarket.helpers.DTOs;

public class Quote {
    private double c; // c is for current price

    public Quote(double c) {

        this.c = c;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }
}