package view;

import model.Difficulty;
import model.PlayableMinesweeper;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import java.text.DecimalFormat;

import notifier.IGameStateNotifier;

public class MinesweeperView implements IGameStateNotifier {
    public static final int MAX_TIME = 1;//in minutes
    public static final int TILE_SIZE = 40;

    public static final class AssetPath {
        public static final String CLOCK_ICON = "./assets/icons/clock.png";
        public static final String FLAG_ICON = "./assets/icons/flag.png";
        public static final String BOMB_ICON = "./assets/icons/bomb.png";
    }

    private PlayableMinesweeper gameModel;
    private JFrame window;
    private JOptionPane winScreen, lossScreen;
    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JMenuItem easyGame, mediumGame, hardGame;
    private TileView[][] tiles;
    private JPanel world = new JPanel();
    private JPanel timerPanel = new JPanel();
    private JPanel flagPanel = new JPanel();
    private JLabel timerView = new JLabel();
    private JLabel flagCountView = new JLabel();
    private JLabel minesOnMap = new JLabel();
    private int openedTiles;

    private int seconds;
    private int minutes;
    private Timer timer;
    private String doubleSeconds;
    private String doubleMinutes;
    private DecimalFormat doubleFormat;

    public MinesweeperView() {
        timer();

        this.window = new JFrame("Minesweeper");
        timerPanel.setLayout(new FlowLayout());
        this.menuBar = new JMenuBar();
        this.gameMenu = new JMenu("New Game");
        this.menuBar.add(gameMenu);
        this.lossScreen = new JOptionPane();
        this.winScreen = new JOptionPane();
        this.easyGame = new JMenuItem("Easy");
        this.gameMenu.add(this.easyGame);
        this.easyGame.addActionListener((ActionEvent e) -> {
            if (gameModel != null)
                gameModel.startNewGame(Difficulty.EASY);
                notifyNewGame(gameModel.getWidth(), gameModel.getHeight());
        });
        this.mediumGame = new JMenuItem("Medium");
        this.gameMenu.add(this.mediumGame);
        this.mediumGame.addActionListener((ActionEvent e) -> {
            if (gameModel != null)
                gameModel.startNewGame(Difficulty.MEDIUM);
                notifyNewGame(gameModel.getWidth(), gameModel.getHeight());
        });
        this.hardGame = new JMenuItem("Hard");
        this.gameMenu.add(this.hardGame);
        this.hardGame.addActionListener((ActionEvent e) -> {
            if (gameModel != null)
                gameModel.startNewGame(Difficulty.HARD);
                notifyNewGame(gameModel.getHeight(), gameModel.getWidth());
        });

        this.window.setJMenuBar(this.menuBar);

        try {
            JLabel clockIcon = new JLabel(new ImageIcon(ImageIO.read(new File(AssetPath.CLOCK_ICON))));
            clockIcon.setSize(new DimensionUIResource(10, 10));
            timerPanel.add(clockIcon);
            timerPanel.add(new JLabel("TIME ELAPSED: "));
            timerPanel.add(this.timerView);
            timerView.setText("00:00");
        } catch (IOException e) {
            System.out.println("Unable to locate clock resource");
        }
        flagPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        try {
            JLabel clockIcon = new JLabel(new ImageIcon(ImageIO.read(new File(AssetPath.FLAG_ICON))));
            clockIcon.setSize(new DimensionUIResource(10, 10));
            flagPanel.add(clockIcon);
            flagPanel.add(new JLabel("FLAG: "));
            flagPanel.add(this.flagCountView);
        } catch (IOException e) {
            System.out.println("Unable to locate flag resource");
        }

        this.window.setLayout(new GridBagLayout());
        GridBagConstraints layoutConstraints = new GridBagConstraints();
        layoutConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 0;
        this.window.add(timerPanel, layoutConstraints);
        layoutConstraints.gridx = 1;
        layoutConstraints.gridy = 0;
        this.window.add(flagPanel, layoutConstraints);
        layoutConstraints.fill = GridBagConstraints.BOTH;
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 1;
        layoutConstraints.gridwidth = 2;
        layoutConstraints.weightx = 1.0;
        layoutConstraints.weighty = 1.0;
        this.window.add(world, layoutConstraints);
        this.window.setSize(500, 200);
        this.window.setVisible(true);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.world.setVisible(true);
    }

    public MinesweeperView(PlayableMinesweeper gameModel) {
        this();
        this.setGameModel(gameModel);
    }

    public void setGameModel(PlayableMinesweeper newGameModel) {
        this.gameModel = newGameModel;
        this.gameModel.setGameStateNotifier(this);
    }

    public void timer(){
        seconds=0;
        minutes=0;
        DecimalFormat doubleFormat = new DecimalFormat("00");
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                if (seconds==60){
                    seconds=0;
                    minutes++;
                }
                doubleSeconds=doubleFormat.format(seconds);
                doubleMinutes=doubleFormat.format(minutes);
                timerView.setText(doubleMinutes+":"+doubleSeconds);
                //timerView.setText(minutes+":"+seconds);
            }
        });
    }


    @Override
    public void notifyNewGame(int row, int col) {
        this.flagCountView.setText("0");
        this.minesOnMap.setText(String.valueOf(gameModel.getMines()));
        this.window.setSize(col * TILE_SIZE, row * TILE_SIZE + 30);
        this.world.removeAll();

        timer.start();
        this.tiles = new TileView[row][col];
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                TileView temp = new TileView(j, i);
                temp.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent arg0) {
                        if (arg0.getButton() == MouseEvent.BUTTON1) {
                            if (gameModel != null) {
                                if (!gameModel.getTile(temp.getPositionX(), temp.getPositionY()).isExplosive()) {
                                        gameModel.open(temp.getPositionX(), temp.getPositionY());
                                        gameModel.click();
                                        openedTiles++;
                                        if(openedTiles==((gameModel.getHeight()*gameModel.getWidth())-gameModel.getMines())){
                                            JOptionPane.showMessageDialog(winScreen, "congrats, you won");
                                        }
                                        //notifyOpened(temp.getPositionX(), temp.getPositionY(), gameModel.getTile(temp.getPositionX(), temp.getPositionY()).getExplosiveCount());
                                } else {
                                    if (!gameModel.getFirstClick()) {
                                        gameModel.open(temp.getPositionX(), temp.getPositionY());
                                        notifyExploded(temp.getPositionX(), temp.getPositionY());
                                        notifyGameLost();
                                        JOptionPane.showMessageDialog(lossScreen, "you lost");
                                    }
                                    else{
                                        gameModel.getTile(temp.getPositionX(), temp.getPositionY()).setSafe();
                                        gameModel.deactivateFirstTileRule();
                                        gameModel.click();
                                        gameModel.open(temp.getPositionX(), temp.getPositionY());
                                        //notifyOpened(temp.getPositionX(), temp.getPositionY(), gameModel.getTile(temp.getPositionX(), temp.getPositionY()).getExplosiveCount());
                                    }
                                }
                            }
                        } else if (arg0.getButton() == MouseEvent.BUTTON3) {
                            if (gameModel != null && !gameModel.getTile(temp.getPositionX(), temp.getPositionY()).isOpened()) {
                                if (gameModel.getTile(temp.getPositionX(), temp.getPositionY()).isFlagged()) {
                                    gameModel.toggleFlag(temp.getPositionX(), temp.getPositionY());
                                    notifyUnflagged(temp.getPositionX(), temp.getPositionY());
                                    notifyFlagCountChanged(gameModel.getFlags());
                                } else if (gameModel.getFlags() != gameModel.getMines()) {
                                    gameModel.toggleFlag(temp.getPositionX(), temp.getPositionY());
                                    notifyFlagged(temp.getPositionX(), temp.getPositionY());
                                    notifyFlagCountChanged(gameModel.getFlags());

                                }
                            }
                        }
                    }
                });
                this.tiles[i][j] = temp;
                this.world.add(temp);
            }
        }
        this.world.setLayout(new GridLayout(row, col));
        this.world.setVisible(false);
        this.world.setVisible(true);
        this.world.repaint();
    }

    @Override
    public void notifyGameLost() {
        this.removeAllTileEvents();
        //throw new UnsupportedOperationException();
    }

    @Override
    public void notifyGameWon() {
        this.removeAllTileEvents();
        throw new UnsupportedOperationException();
    }

    private void removeAllTileEvents() {
        for (int i = 0; i < this.tiles.length; ++i)
            for (int j = 0; j < this.tiles[i].length; ++j)
                this.tiles[i][j].removalAllMouseListeners();
    }

    @Override
    public void notifyFlagCountChanged(int newFlagCount) {
        this.flagCountView.setText(Integer.toString(newFlagCount));
    }

    @Override
    public void notifyTimeElapsedChanged(Duration newTimeElapsed) {
        timerView.setText(
                String.format("%d:%02d", newTimeElapsed.toMinutesPart(), newTimeElapsed.toSecondsPart()));

    }

    @Override
    public void notifyOpened(int x, int y, int explosiveNeighbourCount) {
        this.tiles[y][x].notifyOpened(explosiveNeighbourCount);
    }

    @Override
    public void notifyFlagged(int x, int y) {
        this.tiles[y][x].notifyFlagged();
    }

    @Override
    public void notifyUnflagged(int x, int y) {
        this.tiles[y][x].notifyUnflagged();
    }

    @Override
    public void notifyExploded(int x, int y) {
        this.tiles[y][x].notifyExplode();
    }

}