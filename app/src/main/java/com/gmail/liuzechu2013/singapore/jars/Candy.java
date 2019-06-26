package com.gmail.liuzechu2013.singapore.jars;

public class Candy {
    private int level;
    private int ID; // each candy has a unique ID
    // TODO: attribute for location in a PDF document
    private int countDown; // number of days to the next training for this candy
    private String prompt;
    private String answer;

    private static int totalCandies = 0; // problem: will totalCandies become zero again on exiting the app?

    public Candy(String prompt, String answer) {
        level = 1; // initial level defaults to 1
        ID = totalCandies;
        totalCandies++;
        countDown = 1;
        this.prompt = prompt;
        this.answer = answer;
    }

    public void levelUp() {
        level++;
        countDown = (int) Math.pow(2, level - 1);
    }

    public void dropToLevelOne() {
        level = 1;
        countDown = 1;
    }

    public boolean shouldTrain() {
        return countDown <= 0;
    }

    public void decrementCountDown() {
        countDown--;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getAnswer() {
        return answer;
    }
}
