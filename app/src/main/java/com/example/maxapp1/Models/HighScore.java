package com.example.maxapp1.Models;

public class HighScore {
    private final double lat;
    private final double lon;
    private final int score;

    public HighScore(double lat, double lon, int score)
    {
        this.lat = lat;
        this.lon = lon;
        this.score = score;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getScore() {
        return score;
    }
}
