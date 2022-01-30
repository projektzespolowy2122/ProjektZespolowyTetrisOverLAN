import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class TetrisPanel extends Panel implements KeyListener {

    // variables for double buffered display
    private BufferedImage bi;
    private Graphics gi;

    // dimensions of the frame
    private Dimension dim;

    // constants for panel
    private final Color background = Color.BLACK;

    // the left and right portions of the panel
    Tetris[] screens;

    //keys  on the keyboard
    private int[][] key = {{65,68,87,83,70,71},{37,39,38,40,75,76}};

    TetrisPanel () {
        screens = new Tetris[2];

        addKeyListener(this);
        for (int i = 0; i < 2; i++)
            screens[i] = new Tetris(400*i, 0, this, i);
    }
    public void paint (Graphics g) {
        dim = getSize();
        bi = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
        gi = bi.getGraphics();
        update(g);
    }
    public void update (Graphics g) {
        gi.setColor(background);
        gi.fillRect(0, 0, dim.width, dim.height);
        for (int i = 0; i < 2; i++) {
            if (screens[i] == null)
                continue;
            screens[i].displayGrid(gi);
            screens[i].displayPieces(gi);
            screens[i].displayUI(gi);
        }
        g.drawImage(bi, 0, 0, this);
    }

    @Override
    public void keyTyped (KeyEvent e) {}
    @Override
    public void keyReleased (KeyEvent e) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                if (e.getKeyCode() == key[i][j]) {
                    if (screens[i].currentFigure == null)
                        break;
                    if (j == 3)
                        screens[i].delay = (screens[i].level >= 20 ? Tetris.GLOBAL_DELAY[19] : Tetris.GLOBAL_DELAY[screens[i].level]);
                }
            }
        }
    }
    @Override
    public void keyPressed (KeyEvent e) {
        // user input
        // three cases that handle when the user adjusts the game states (ACTIVE, PAUSED, CLOSED)
        if (e.getKeyCode() == KeyEvent.VK_P) {
            boolean currentState = screens[0].isPaused;
            for (int i = 0; i < 2; i++)
                screens[i].isPaused = !currentState;
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_Q) {
            System.exit(0);
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            for (int i = 0; i < 2; i++)
                screens[i].restart();
            repaint();
            return;
        }
        if (screens[0].isPaused || screens[0].isGameOver)
            return;
        int keyCode = e.getKeyCode();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                if (keyCode == key[i][j]) {
                    if (screens[i].currentFigure == null)
                        break;
                    switch (j) {
                        case 0:
                            screens[i].movePiece(0, -1);
                            repaint();
                            break;
                        case 1:
                            screens[i].movePiece(0, 1);
                            repaint();
                            break;
                        case 2:
                            screens[i].rotateRight();
                            break;
                        case 3:
                            screens[i].delay = (screens[i].level >= 20 ? Tetris.GLOBAL_DELAY[19] : Tetris.GLOBAL_DELAY[screens[i].level])/8;
                            break;
                        case 4:
                            screens[i].time = 1 << 30;
                            screens[i].lockTime = 1 << 30;
                            while(screens[i].movePiece(1, 0));
                            break;
                    }
                }
            }
        }
        repaint();
    }
    protected void setGameOver () {
        for (int i = 0; i < 2; i++)
            screens[i].isGameOver = true;
    }
    protected void sendGarbage (int id, int send) {
        if (2 == 1)
            return;
        int rand = (int)(Math.random()*(2-1));
        if (rand >= id)
            rand++;
        screens[rand].addGarbage(send);
    }


}
