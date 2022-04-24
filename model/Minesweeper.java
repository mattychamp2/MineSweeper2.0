package model;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Minesweeper extends AbstractMineSweeper {
    private int width;
    private int height;
    private int mines;
    private AbstractTile[][] world;
    private int flagsSet;
    private boolean firstClick;
    private int temp;
    private int tilesOpened;

    int secondsPassed = 0;
    Timer myTimer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            secondsPassed++;
            System.out.print(secondsPassed);
        }
    };

    public void start() {
        myTimer.scheduleAtFixedRate(task, 1000, 1000);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void click() {
        firstClick = false;
    }

    public boolean getFirstClick() {
        return firstClick;
    }

    public int getMines() {
        return mines;
    }

    @Override
    public void startNewGame(Difficulty level) {
        if (level == Difficulty.EASY) {
            width = 8;
            height = 8;
            mines = 10;
            flagsSet = 0;
        } else if (level == Difficulty.MEDIUM) {
            width = 16;
            height = 16;
            mines = 40;
            flagsSet = 0;
        } else if (level == Difficulty.HARD) {
            width = 30;
            height = 16;
            mines = 99;
            flagsSet = 0;
        }
        firstClick = true;
        temp = 0;
        generateWorld(height, width, mines);
    }

    public void generateWorld(int height, int width, int mines) {
        world = new AbstractTile[width][height];
        Random random = new Random();
        int minesPlaced = 0;
        tilesOpened = 0;
        while (minesPlaced < mines) {
            int nextX = random.nextInt(width);
            int nextY = random.nextInt(height);
            if (world[nextX][nextY] == null) {
                world[nextX][nextY] = generateExplosiveTile();
                minesPlaced++;
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (world[i][j] == null) {
                    world[i][j] = generateEmptyTile();
                }
            }
        }
        setNumbers();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                System.out.print(world[i][j].isExplosive() + "\t");
            }
            System.out.println();
        }
    }

    public void setNumbers() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int count = 0;
                if (!world[i][j].isExplosive()) {
                    for (int k = i - 1; k <= i + 1; k++) {
                        for (int l = j - 1; l <= j + 1; l++) {
                            try {
                                if (world[k][l].isExplosive()) {
                                    count++;
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }
                world[i][j].setExplosiveCount(count);
            }
        }
    }


    @Override
    public void startNewGame(int row, int col, int explosionCount) {
        width = col;
        height = row;
        mines = explosionCount;
        flagsSet = 0;
        firstClick = true;
        generateWorld(height, width, mines);
        //myTimer.start();
    }

    @Override
    public void toggleFlag(int x, int y) {
        if (world[x][y].isFlagged()) {
            flagsSet--;
            world[x][y].unflag();
        } else {
            flagsSet++;
            world[x][y].flag();
        }
    }

    @Override
    public AbstractTile getTile(int x, int y) {

        return world[x][y];
    }

    @Override
    public void setWorld(AbstractTile[][] world) {
        this.world = world;

    }

    @Override
    public void open(int x, int y) {
        world[x][y].open();
        this.viewNotifier.notifyOpened(x, y, world[x][y].getExplosiveCount());
        tilesOpened++;
        System.out.println(tilesOpened);
    }

    @Override
    public void flag(int x, int y) {
        world[x][y].flag();
        flagsSet++;
    }

    @Override
    public void unflag(int x, int y) {
        world[x][y].unflag();
        flagsSet--;
    }

    @Override
    public void deactivateFirstTileRule() {
        int count = 0;
        System.out.println("bomb was moved");
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                if (!getTile(i, j).isExplosive() && count == 0) {
                    world[i][j] = generateExplosiveTile();
                    count++;
                }
            }
        }
        setNumbers();
    }

    @Override
    public AbstractTile generateEmptyTile() {

        return new Tile(false);
    }

    @Override
    public AbstractTile generateExplosiveTile() {

        return new Tile(true);
    }

    public int getFlags() {
        return flagsSet;
    }

    public void openAround(int x, int y) {
        for (int z = 0; z < 100; z++) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (world[i][j].isOpened() && world[i][j].getExplosiveCount() == 0) {
                        for (int k = i - 1; k <= i + 1; k++) {
                            for (int l = j - 1; l <= j + 1; l++) {
                                try {
                                    if (!world[k][l].isOpened()) {
                                        open(k, l);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean checkWin() {
        return tilesOpened == (width * height) - mines;
    }
}
