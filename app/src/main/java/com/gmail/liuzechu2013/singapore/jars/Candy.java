package com.gmail.liuzechu2013.singapore.jars;

import android.net.Uri;

public class Candy {
    private int level;
    // private int ID; // each candy has a unique ID
    // TODO: attribute for location in a PDF document
    private int countDown; // number of days to the next training for this candy
    private String prompt;
    private String answer;
    private String imageUri; // to access screenshot; saved as a string

    // private static int totalCandies = 0; // problem: will totalCandies become zero again on exiting the app?

    public Candy(String prompt, String answer) {
        level = 1; // initial level defaults to 1
//        ID = totalCandies;
//        totalCandies++;
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

    public int getLevel() {
        return level;
    }

    public Uri getImageUri() {
        if (imageUri != null && !imageUri.equals("")) {
            return Uri.parse(imageUri);
        } else {
            return null;
        }
    }

    public void setImageUri(Uri imageUri) {
        if (imageUri != null) {
            this.imageUri = imageUri.toString();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Candy)) {
            return false;
        } else {

            // check whether imageUri is equal
            boolean imageUriEqual;
            if (((Candy) obj).imageUri == null) {
                if (this.imageUri == null) {
                    imageUriEqual = true;
                } else {
                    imageUriEqual = false;
                }
            } else {
                if (this.imageUri == null) {
                    imageUriEqual = false;
                } else {
                    imageUriEqual = this.imageUri.equals(((Candy) obj).imageUri);
                }
            }

            // check the other fields
            boolean result = ((Candy) obj).level == this.level &&
                    ((Candy) obj).countDown == this.countDown &&
                    ((Candy) obj).prompt.equals(this.prompt) &&
                    ((Candy) obj).answer.equals(this.answer) &&
                    imageUriEqual;

            return result;
        }
    }
}
