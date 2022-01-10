import java.awt.*;
import java.util.*;

public class Tetris {
    // grid of color ids that stores what kind of block is where
    private int[][] grid = new int[22][10];

    // dimensions of the frame
    private final int panelX, panelY;

    // Big panel
    private final TetrisPanel panel;

    // the delay values for levels: the array index corresponds to the level. After level 10 the delay remains consistent
    protected static final int[] GLOBAL_DELAY = {450, 350, 300, 200, 100, 80, 60, 30, 20, 10};

    // the global delay lock value
    private final int GLOBAL_LOCK = 1000;

    /*
     * Colors representing type of blocks
     * light gray = empty square
     * yellow = O
     * cyan = I
     * blue = L
     * orange = J
     * green = S
     * red = Z
     * Magenta = T
     */

    private static final Color[] c = {Color.LIGHT_GRAY, Color.YELLOW, Color.CYAN,
            Color.BLUE, Color.ORANGE, Color.GREEN, Color.RED, Color.MAGENTA, Color.DARK_GRAY};
    private static final Color ghostColor = Color.DARK_GRAY;
    private static final Color UIColor = Color.LIGHT_GRAY;

    // Kick cases for J L S T Z blocks
    private static final int[][] moveY_1 = {{0, -1, -1, 0, -1},
            {0, +1, +1, 0, +1},
            {0, +1, +1, 0, +1},
            {0, +1, +1, 0, +1},
            {0, +1, +1, 0, +1},
            {0, -1, -1, 0, -1},
            {0, -1, -1, 0, -1},
            {0, -1, -1, 0, -1}};
    private static final int[][] moveX_1 = {{0, 0, +1, 0, -2},
            {0, 0, +1, 0, -2},
            {0, 0, -1, 0, +2},
            {0, 0, -1, 0, +2},
            {0, 0, +1, 0, -2},
            {0, 0, +1, 0, -2},
            {0, 0, -1, 0, +2},
            {0, 0, -1, 0, +2}};

    // Kick cases for I block
    private static final int[][] moveY_2 = {{0, -2, +1, -2, +1},
            {0, -1, +2, -1, +2},
            {0, -1, +2, -1, +2},
            {0, +2, -1, +2, -1},
            {0, +2, -1, +2, -1},
            {0, +1, -2, +1, -2},
            {0, +1, -2, +1, -2},
            {0, -2, +1, -2, +1}};
    private static final int[][] moveX_2 = {{0, 0, 0, -1, +2},
            {0, 0, 0, +2, -1},
            {0, 0, 0, +2, -1},
            {0, 0, 0, +1, -2},
            {0, 0, 0, +1, -2},
            {0, 0, 0, -2, +1},
            {0, 0, 0, -2, +1},
            {0, 0, 0, -1, +2}};

    // Handles the queue for pieces
    private Queue<Integer> figuresQueue = new ArrayDeque<Integer>();
    // Generates the pieces
    protected Figure p = new Figure();
    // Represents the current active piece
    protected Figure.Active currentFigure = null;
    // Represents the ID of the current screen
    private int id;

    // Timing and level variables
    protected int time = 0;
    protected int delay = GLOBAL_DELAY[0];
    protected int level = 0;
    protected int lockTime = 0;
    protected int linesCleared = 0;

    // constants for UI
    private final int[] dy = {50, 100, 150, 200, 300};

    // Game state variables
    protected boolean isPaused = false;
    protected boolean isGameOver = false;

    private int combo = 0;

    // Thread that manages the gravity of the pieces
    private Timer t = new Timer();

    private TimerTask move = new TimerTask() {
        @Override
        public void run () {
            // checking for game states
            if (isPaused || isGameOver)
                return;

            // refill the queue if it is close to empty
            synchronized (figuresQueue) {
                if (figuresQueue.size() < 4)
                    for (int id : p.getPermutation())
                        figuresQueue.offer(id);
            }
            if (time >= delay) {
                // getting a new piece
                if (currentFigure == null)
                    currentFigure = p.getActive(figuresQueue.poll());

                // attempting to move the piece
                if (movePiece(1, 0)) {
                    lockTime = 0;
                    time = 0;
                }
                else if (lockTime >= GLOBAL_LOCK) {
                    // the piece cannot be moved down any further and the lock delay has expired then place the piece and check for gameover
                    isGameOver = true;

                    for (int i = 0; i < 4; i++) {
                        if (currentFigure.coordinates[i].x >= 0)
                            grid[currentFigure.coordinates[i].x][currentFigure.coordinates[i].y] = currentFigure.id;
                        if (currentFigure.coordinates[i].x >= 2)
                            isGameOver = false;
                    }

                    if (isGameOver) {
                        System.out.println("GAMEOVER -- FINAL SCORE " + linesCleared);
                        panel.setGameOver();
                    }

                    // set the piece down and allow the user to hold a piece. The lock time is also reset
                    synchronized (currentFigure) {
                        currentFigure = null;
                        lockTime = 0;
                    }

                    // clear the lines and adjust the level
                    int cleared = clearLines();

                    if (cleared > 0)
                        combo++;
                    else
                        combo = 0;

                    int send = cleared > 0 ? ((1 << (cleared-1))/2 + (combo/2)): 0;
                    panel.sendGarbage(id, send);
                    adjustLevel();

                    // immediately get another piece
                    time = delay;
                }
                panel.repaint();
            }
            time++;
            lockTime++;
        }
    };

    Tetris (int panelY, int panelX, TetrisPanel panel, int id) {
        this.panelY = panelY;
        this.panelX = panelX;
        this.panel = panel;
        this.id = id;
        t.scheduleAtFixedRate(move, 1000, 1);
    }

    // adjust the level based on the number of lines cleared
    private void adjustLevel () {
        level = linesCleared/4;

        if (level >= 20)
            delay = GLOBAL_DELAY[19];
        else
            delay = GLOBAL_DELAY[level];
    }

    // paints the grid based on the color id values in the 2D Array
    public void displayGrid (Graphics gi) {
        for (int i = 2; i < 22; i++) {
            for (int j = 0; j < 10; j++) {
                gi.setColor(c[grid[i][j]]);
                gi.fillRect(panelY + j*25+10, panelX + i*25, 24, 24);
            }
        }
    }

    // paints the current piece
    public void displayPieces (Graphics gi) {
        if (currentFigure == null)
            return;

        synchronized (currentFigure) {
            int d = -1;
            // displaying the ghost piece
            boolean isValid = true;

            while (isValid) {
                d++;
                for (Figure.Point block : currentFigure.coordinates)
                    if (block.x + d >= 0 && (block.x+d >= 22 || grid[block.x+d][block.y] != 0))
                        isValid = false;
            }

            d--;
            // painting the ghost piece and the active piece
            gi.setColor(ghostColor);

            for (Figure.Point block : currentFigure.coordinates)
                if (block.x+d >= 2)
                    gi.fillRect(panelY + block.y*25+10, panelX + (block.x+d)*25, 24, 24);

            gi.setColor(c[currentFigure.id]);

            for (Figure.Point block : currentFigure.coordinates)
                if (block.x >= 2)
                    gi.fillRect(panelY + block.y*25+10, panelX + block.x*25, 24, 24);
        }
    }

    // paints the user interface
    public void displayUI (Graphics gi) {
        gi.setColor(UIColor);
        gi.drawString("LINES CLEARED: " + linesCleared, panelY + 10, panelX + 10);
        gi.drawString("CURRENT LEVEL: " + level, panelY + 10, panelX + 20);

        if (isPaused)
            gi.drawString("PAUSED", panelY + 10, 30);

        if (isGameOver)
            gi.drawString("GAMEOVER -- Q FOR QUIT; R FOR RESTART", panelY + 10, panelX + 40);
        gi.drawString("NEXT", panelY + 300, panelX + 50);

        for (int k = 0; k < 4; k++) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 4; j++) {
                    gi.fillRect(panelY + j*20 + 300, panelX + i*20 + dy[k], 19, 19);
                }
            }
        }

        // paints the queue of blocks
        synchronized (figuresQueue) {
            int i = 0;

            for (int id : figuresQueue) {
                Figure.Active nextPiece = p.getActive(id);
                gi.setColor(c[nextPiece.id]);

                for (Figure.Point block : nextPiece.coordinates) {
                    gi.fillRect(panelY + (block.y-3)*20+300, panelX + block.x*20 + dy[i], 19, 19);
                }

                i++;
                if (i >= 4)
                    break;
            }
        }
    }

    // coordinatest condition: any full lines are cleared and the respective variable is incremented
    private int clearLines () {
        int numCleared = 0;

        while (true) {
            // checking if there is a line that is full
            int index = -1;

            for (int j = 0; j < 22; j++) {
                int cnt = 0;
                for (int i = 0; i < 10; i++) {
                    cnt += grid[j][i] != 0 ? 1 : 0;
                }

                if (cnt == 10) {
                    index = j;
                    break;
                }
            }

            if (index == -1)
                break;

            // removing the full lines one by one
            int[][] temp = new int[22][10];

            for (int i = 0; i < 22; i++)
                for (int j = 0; j < 10; j++)
                    temp[i][j] = grid[i][j];

            for (int i = 0; i < index+1; i++) {
                for (int j = 0; j < 10; j++) {
                    if (i == 0)
                        grid[i][j] = 0;
                    else
                        grid[i][j] = temp[i-1][j];
                }
            }
            linesCleared++;
            numCleared++;
        }

        return numCleared;
    }

    public void restart () {
        currentFigure = null;
        grid = new int[22][10];
        figuresQueue.clear();
        level = 0;
        linesCleared = 0;
        isGameOver = false;
    }

    // attempt to rotate the piece clockwise
    protected void rotateRight () {
        if (currentFigure.id == 1)
            return;

        Figure.Point[] np = new Figure.Point[4];

        for (int i = 0; i < 4; i++) {
            int nr = currentFigure.coordinates[i].y - currentFigure.widthY + currentFigure.widthX;
            int nc = currentFigure.coordinates[i].x - currentFigure.widthX + currentFigure.widthY;
            np[i] = new Figure.Point(nr, nc);
        }

        int widthY = currentFigure.widthY;
        int heightY = currentFigure.heightY;
        for (int i = 0; i < 4; i++) {
            np[i].y = heightY - (np[i].y-widthY);
        }

        kick(np, currentFigure.state*2);
        panel.repaint();

    }

    // coordinatest condition: rotates the piece according to the state of the rotation
    // this method performs the actual rotation and copies the coordinatesitions of the blocks into the active block
    private void kick (Figure.Point[] coordinates, int id) {
        for (int i = 0; i < 5; i++) {
            boolean valid = true;
            int dr = currentFigure.id == 2 ? moveX_2[id][i] : moveX_1[id][i];
            int dc = currentFigure.id == 2 ? moveY_2[id][i] : moveY_1[id][i];

            for (Figure.Point block : coordinates) {
                if (block.x + dr < 0 || block.x + dr >= 22)
                    valid = false;
                else if (block.y + dc < 0 || block.y + dc >= 10)
                    valid = false;
                else if (grid[block.x+dr][block.y+dc] != 0)
                    valid = false;
            }

            if (valid) {
                for (int j = 0; j < 4; j++) {
                    currentFigure.coordinates[j].x = coordinates[j].x + dr;
                    currentFigure.coordinates[j].y = coordinates[j].y + dc;
                }

                currentFigure.heightY += dc;
                currentFigure.widthY += dc;
                currentFigure.heightX += dr;
                currentFigure.widthX += dr;

                if (id % 2 == 1)
                    currentFigure.state = (currentFigure.state+3)%4;
                else
                    currentFigure.state = (currentFigure.state+1)%4;
                return;
            }
        }
    }

    // attempts to move the active piece
    // coordinatest-condition: will return false if it cannot move and true if it can move
    protected boolean movePiece (int dr, int dc) {
        if (currentFigure == null)
            return false;

        for (Figure.Point block : currentFigure.coordinates) {
            if (block.x+dr < 0 || block.x+dr >= 22)
                return false;
            if (block.y+dc < 0 || block.y+dc >= 10)
                return false;
            if (grid[block.x+dr][block.y+dc] != 0)
                return false;
        }

        for (int i = 0; i < 4; i++) {
            currentFigure.coordinates[i].x += dr;
            currentFigure.coordinates[i].y += dc;
        }

        currentFigure.widthY += dc;
        currentFigure.heightY += dc;
        currentFigure.widthX += dr;
        currentFigure.heightX += dr;

        return true;
    }

    protected void addGarbage (int lines) {
        for (int i = 0; i < 22; i++) {
            for (int j = 0; j < 10; j++) {
                if (grid[i][j] != 0 && i - lines < 0) {
                    isGameOver = true;
                    panel.setGameOver();
                } else if (i - lines >= 0){
                    grid[i-lines][j] = grid[i][j];
                }
            }
        }

        for (int i = 21; i >= Math.max(0, 22-lines); i--) {
            for (int j = 0; j < 10; j++)
                grid[i][j] = 8;
            grid[i][(int)(Math.random()*8)] = 0;
        }

        if (currentFigure == null) {
            panel.repaint();
            return;
        }

        boolean valid = false;

        while (!valid) {
            valid = true;
            for (Figure.Point block : currentFigure.coordinates) {
                if (block.x >= 0 && grid[block.x][block.y] != 0)
                    valid = false;
            }
            if (!valid)
                for (int i = 0; i < 4; i++)
                    currentFigure.coordinates[i].x--;
        }

        panel.repaint();
    }
}
