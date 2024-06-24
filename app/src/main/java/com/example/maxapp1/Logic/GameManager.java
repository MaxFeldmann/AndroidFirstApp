package com.example.maxapp1.Logic;

public class GameManager {

    private final int life;
    private int dmg, round;
    private final boolean[][] logicMat;
    private final boolean[] shipArr;

    public GameManager() {
        this(3);
    }

    public int getDmg() {
        return dmg;
    }

    public GameManager(int life) {
        this.life = life;
        logicMat = new boolean[5][3];
        shipArr = new boolean[3];
        shipArr[1] = true;
    }

    public boolean[][] getLogicMat() {
        boolean[][] copyMat = new boolean[logicMat.length][logicMat[0].length];
        for (int i = 0; i < copyMat.length; i++)
        {
            System.arraycopy(logicMat[i], 0, copyMat[i], 0, copyMat[i].length);
        }
        return copyMat;
    }

    public boolean isGameLost() {
        return dmg == life;
    }

    public void moveNext(int randInt) {
        for (int i = logicMat.length - 1; i > -1; i--)
        {
            for (int j = 0; j < logicMat[0].length; j++)
            {
                if (logicMat[i][j])
                {
                    logicMat[i][j] = false;
                    if (i != (logicMat.length - 1))
                        logicMat[i + 1][j] = true;
                }
            }
        }
        if (round%2 == 0)
            logicMat[0][randInt] = true;
        round++;
    }

    public int getShipIndex() {
        for (int j = 0; j < shipArr.length; j++) {
            if (shipArr[j])
                return j;
        }
        return -1;
    }

    public boolean isHit() {
        for (int i = 0; i < logicMat[0].length; i++)
        {
            if (logicMat[logicMat.length - 1][i] && shipArr[i]) {
                dmg++;
                return true;
            }
        }
        return false;
    }

    public boolean moveShip(int ans) {
        int shipIndex = getShipIndex();
        if (ans == 0 && shipIndex != 0) {
            shipArr[shipIndex] = false;
            shipArr[shipIndex - 1] = true;
            return true;
        }
        else if (ans == 1 && shipIndex != (shipArr.length - 1))
        {
            shipArr[shipIndex] = false;
            shipArr[shipIndex + 1] = true;
            return true;
        }
        return false;
    }
}
