package com.example.maxapp1.Logic;

import java.util.Random;

public class GameManager {

    private final int life;
    private int dmg, round;
    private int shipIndex;
    private final int[][] logicMat;
    private int score;

    private static final int ROWS = 8;
    private static final int COLS = 5;

    public int getRound() {
        return round;
    }

    private static final int[][] onePosOpt = {{0}, {1}, {2}, {3}, {4}};
    private static final int[][] twoPosOpt = {{0,1}, {0, 2}, {0, 3},
            {0, 4}, {1, 2}, {1, 3}, {1, 4}, {2, 3}, {2, 4}, {3, 4}};
    private static final int[][] threePosOpt = {{0, 1, 2}, {0, 1, 3},
            {0, 1, 4}, {0, 2, 3}, {0, 2, 4}, {0, 3, 4},
            {1, 2, 3}, {1, 2, 4}, {1, 3, 4}, {2, 3, 4}};
    private static final int[][][] allPosOpt = {onePosOpt, twoPosOpt, threePosOpt};
    private final Random rand;

    public GameManager() {this(3);}

    public int getDmg() {
        return dmg;
    }

    public int getCols() { return COLS; }

    public int getRows() { return ROWS; }

    public GameManager(int life) {
        this.life = life;
        logicMat = new int[ROWS][COLS];
        shipIndex = 2;
        rand = new Random();
    }

    public int getScore() {
        return score;
    }

    public int[][] getLogicMat() {
        int[][] copyMat = new int[ROWS][COLS];
        for (int i = 0; i < ROWS; i++)
        {
            System.arraycopy(logicMat[i], 0, copyMat[i], 0, COLS);
        }
        return copyMat;
    }

    public boolean isGameLost() {
        return dmg == life;
    }

    public void moveNext() {
        for (int i = ROWS - 1; i > -1; i--)
        {
            for (int j = 0; j < COLS; j++)
            {
                if (logicMat[i][j] != 0)
                {
                    int prevLine = logicMat[i][j];
                    logicMat[i][j] = 0;
                    if (i != (ROWS - 1))
                        logicMat[i + 1][j] = prevLine;
                }
            }
        }
        if (round % 2 == 0)
            chooseRandomAlien();
        if (round % 7 == 5)
            chooseRandomMetal();
        round++;
        score+=10;
    }

    private void chooseRandomAlien() {
        int amount = rand.nextInt(3);
        int opt = rand.nextInt(allPosOpt[amount].length);
        for (int i = 0; i < amount + 1; i++)
        {
            logicMat[0][allPosOpt[amount][opt][i]] = 1;
        }
    }

    private void chooseRandomMetal() {
        int opt = rand.nextInt(COLS);
        logicMat[0][opt] = 2;
    }

    public int getShipIndex() { return shipIndex; }

    public int isHit() {
        if (logicMat[ROWS - 1][shipIndex] == 0)
            return 0;
        if (logicMat[ROWS - 1][shipIndex] == 1)
        {
            dmg++;
            return 1;
        }
        score+=50;
        return 2;
    }

    public boolean moveShip(int ans) {
        if (ans == 0 && shipIndex != 0) {
            shipIndex--;
            return true;
        }
        else if (ans == 1 && shipIndex != (COLS - 1))
        {
            shipIndex++;
            return true;
        }
        return false;
    }
}
