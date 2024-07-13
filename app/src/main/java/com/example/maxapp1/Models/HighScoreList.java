package com.example.maxapp1.Models;

import java.util.ArrayList;

public class HighScoreList {

    private static final int MAX_LEN = 10;

    private String name = "";
    private ArrayList<HighScore> highScores = new ArrayList<>();

    public HighScoreList() {
    }

    public String getName() {
        return name;
    }

    public HighScoreList setName(String name) {
        this.name = name;
        return this;
    }

    public ArrayList<HighScore> getHighScores() {
        return highScores;
    }

    public HighScoreList setHighScores(ArrayList<HighScore> highScores) {
        this.highScores = highScores;
        return this;
    }

    public HighScoreList addHighScore(HighScore highScore) {
        int len = highScores.size();
        for (int i = 0; i < len; i++)
        {
            if (highScores.get(i).getScore() < highScore.getScore()) {
                highScores.add(i, highScore);
                break;
            }
        }
        if (!highScores.contains(highScore))
            highScores.add(len, highScore);
        if (highScores.size() > MAX_LEN)
            highScores.remove(10);
        return this;
    }

    @Override
    public String toString() {
        return "HighScoreList{" +
                "name='" + name + '\'' +
                ", highscores=" + highScores +
                '}';
    }
}
