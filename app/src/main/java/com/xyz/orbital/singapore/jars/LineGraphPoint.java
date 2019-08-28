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

    public LineGraphPoint(int quantity, Calendar calendar) {
        this.calendar = calendar;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public String getDate() {
        // MONTH
        int index = this.calendar.get(Calendar.MONTH);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String monthString = months[index];

        // DAY OF MONTH
        int day = this.calendar.get(Calendar.DAY_OF_MONTH);
        String dayString = String.format("%02d", day);

        return dayString + " " + monthString;
    }

    public static String getDateFromCalendar(Calendar calendar) {
        // MONTH
        int index = calendar.get(Calendar.MONTH);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String monthString = months[index];

        // DAY OF MONTH
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dayString = String.format("%02d", day);

        return dayString + " " + monthString;
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }
}
