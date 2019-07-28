package com.xyz.orbital.singapore.jars;

import java.util.ArrayList;
import java.util.Random;

public class Jar {
    // private int ID; // unique ID for each jar; Problem: useless? also training process may screw this up
    private String title;
    private long lastAcessTime; // timestamp of the last access time by user
    private ArrayList<Candy> candies; // all candies in the jar
    private int jarType;
    private String jarColor;

    // private static int totalJars = 0; // problem: will totalJars become zero again on exiting the app?

    public Jar(String title) {
        // ID = totalJars;
        this.title = title;
        // totalJars++;
        lastAcessTime = System.currentTimeMillis();
        candies = new ArrayList<>();
        jarType = new Random().nextInt(9);
        String[] arr = {"#ef6256", "#f99c1c", "#fec41b", "#47b585", "#5bc4bf", "#825ca4", "#e96ca9"};
        jarColor = arr[new Random().nextInt(7)];
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

    public int getJarType() { return jarType; }

    public String getJarColor() {return jarColor; }
}
