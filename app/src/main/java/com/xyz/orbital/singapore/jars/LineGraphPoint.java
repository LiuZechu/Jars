package com.xyz.orbital.singapore.jars;

import java.util.Calendar;

public class LineGraphPoint {
    // X-axis
    private Calendar calendar;
    // Y-axis
    private int quantity;

    public LineGraphPoint(int quantity) {
        this.calendar = Calendar.getInstance();
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
}
