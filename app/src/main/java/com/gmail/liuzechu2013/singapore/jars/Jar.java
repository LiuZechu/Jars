package com.gmail.liuzechu2013.singapore.jars;

import android.app.Activity;

import java.util.ArrayList;

public class Jar {
    private int ID; // unique ID for each jar; Problem: useless? also training process may screw this up
    private String title;
    private long lastAcessTime; // timestamp of the last access time by user
    private ArrayList<Candy> candies; // all candies in the jar

    private static int totalJars = 0; // problem: will totalJars become zero again on exiting the app?

    public Jar(String title) {
        ID = totalJars;
        this.title = title;
        totalJars++;
        lastAcessTime = System.currentTimeMillis();
        candies = new ArrayList<>();
    }

    public void addCandy(Candy candy) {
        candies.add(candy);
    }

    public void addAllCandies(ArrayList<Candy> listOfCandies) {
        candies.addAll(listOfCandies);
    }

    public void deleteCandy(Candy candy) {
        candies.remove(candy);
    }

    // may be inefficient for large numbers
    public void graduateCandy(Candy candy) {
        candies.remove(candy);
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Candy> getCandies() {
        return candies;
    }
}
