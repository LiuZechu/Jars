package com.gmail.liuzechu2013.singapore.jars;

public class Achievement {
    private String name;
    private boolean isUnclocked;

    public Achievement(String name) {
        this.name = name;
        isUnclocked = false;
    }

    public String getName() {
        return name;
    }

    public boolean getIsUnlocked() {
        return isUnclocked;
    }

    public void unlock() {
        isUnclocked = true;
    }

}
