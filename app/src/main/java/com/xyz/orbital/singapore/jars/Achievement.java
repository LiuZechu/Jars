package com.xyz.orbital.singapore.jars;

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
