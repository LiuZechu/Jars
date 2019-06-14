package com.gmail.liuzechu2013.singapore.jars;

public class ShopItem {
    private int type; // its value takes on one of the following constants
    private String name;

    // Constants
    public final static int SUPERPOWER = 1;
    public final static int EXTENSION = 2;
    public final static int JAR = 3;
    public final static int CANDY = 4;
    public final static int CANDY_EXPRESSION = 5;

    public ShopItem(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
