package pacman;


import java.awt.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private Dimension d;
    private final Font smallfont = new Font("", Font.BOLD, 14);

    private Image ii;
    private final Color dotcolor = new Color(192, 192, 0);
    private Color mazecolor;

    private boolean ingame = false;
    private boolean dying = false;

    private final int blocksize = 24;
    private final int blocks = 15;
    private final int scrsize = blocks * blocksize;
    private final int delay = 2;
    private final int count = 4;
    private final int maxghosts = 12;
    private final int pacmanspeed = 6;

    private int paccount = delay;
    private int dir = 1;
    private int pos = 0;
    private int ghosts = 6;
    private int pacsleft, score;
    private int[] dx, dy;
    private int[] ghostx, ghosty, ghostdx, ghostdy, ghostspeed;

    private Image ghost;
    private Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    private Image pacman3up, pacman3down, pacman3left, pacman3right;
    private Image pacman4up, pacman4down, pacman4left, pacman4right;

    private int pacmanx, pacmany, pacmandx, pacmandy;
    private int reqdx, reqdy, viewdx, viewdy;

    //地圖設定
    private final short leveldata[] = {
        19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        21, 0 , 0 , 0 , 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        21, 0 , 0 , 0 , 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        21, 0 , 0 , 0 , 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20,
        17, 18, 18, 18, 16, 16, 20, 0 , 17, 16, 16, 16, 16, 16, 20,
        17, 16, 16, 16, 16, 16, 20, 0 , 17, 16, 16, 16, 16, 24, 20,
        25, 16, 16, 16, 24, 24, 28, 0 , 25, 24, 24, 16, 20, 0 , 21,
         1, 17, 16, 20, 0 , 0 , 0 , 0 , 0 , 0 , 0 , 17, 20, 0 , 21,
         1, 17, 16, 16, 18, 18, 22, 0 , 19, 18, 18, 16, 20, 0 , 21,
         1, 17, 16, 16, 16, 16, 20, 0 , 17, 16, 16, 16, 20, 0 , 21,
         1, 17, 16, 16, 16, 16, 20, 0 , 17, 16, 16, 16, 20, 0 , 21,
         1, 17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0 , 21,
         1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0 , 21,
         1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20,
         9,  8,  8,  8,  8,  8,  8,  8,  8,  8, 25, 24, 24, 24, 28
    };

    private final int validspeeds[] = {1, 2, 3, 4, 6, 8};
    private final int maxspeed = 6;

    private int currentspeed = 3;
    private short[] screendata;
    private Timer timer;
    
    //設定主畫面面板
    public Board() {
    	loadImages();
    	
        initVariables();
        
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.black);
        setDoubleBuffered(true);
    }
    
    //初始化各個基本參數
    private void initVariables() {

        screendata = new short[blocks * blocks];
        mazecolor = new Color(5, 100, 5);
        d = new Dimension(400, 400);
        ghostx = new int[maxghosts];
        ghostdx = new int[maxghosts];
        ghosty = new int[maxghosts];
        ghostdy = new int[maxghosts];
        ghostspeed = new int[maxghosts];
        dx = new int[4];
        dy = new int[4];
        
        timer = new Timer(40, this);
        timer.start();
    }

    //判斷動畫改變
    private void doAnim() {

        paccount--;

        if (paccount <= 0) {
            paccount = delay;
            pos = pos + dir;

            if (pos == (count - 1) || pos == 0) {
                dir = -dir;
            }
        }
    }
    
    //遊戲的基本判定   從"gameover"開始
    private void playGame(Graphics2D g2d) {

        if (dying) {

            death();

        } else {

            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }
    //設定一開始的畫面凍結
    private void showIntroScreen(Graphics2D g2d) {

        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, scrsize / 2 - 30, scrsize - 100, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, scrsize / 2 - 30, scrsize - 100, 50);

        String s = "Press s to start.";
        Font small = new Font("", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (scrsize - metr.stringWidth(s)) / 2, scrsize / 2);
    }

    //顯示分數
    private void drawScore(Graphics2D g) {

        int i;
        String s;

        g.setFont(smallfont);
        g.setColor(new Color(96, 128, 255));
        s = "Score: " + score;
        g.drawString(s, scrsize / 2 + 96, scrsize + 16);

        for (i = 0; i < pacsleft; i++) {
            g.drawImage(pacman3left, i * 28 + 8, scrsize + 1, this);
        }
    }

    //地圖更新判斷  分數與遊戲難度的判斷
    private void checkMaze() {

        short i = 0;
        boolean finished = true;

        while (i < blocks * blocks && finished) {

            if ((screendata[i] & 48) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) {

            score += 50;

            if (ghosts < maxghosts) {
                ghosts++;
            }

            if (currentspeed < maxspeed) {
                currentspeed++;
            }

            initLevel();
        }
    }

    //遊戲結束判斷
    private void death() {

        pacsleft--;

        if (pacsleft == 0) {
            ingame = false;
        }

        continueLevel();
    }

    //鬼的邏輯移動判斷
    private void moveGhosts(Graphics2D g2d) {

        short i;
        int pos;
        int count;

        for (i = 0; i < ghosts; i++) {
            if (ghostx[i] % blocksize == 0 && ghosty[i] % blocksize == 0) {
                pos = ghostx[i] / blocksize + blocks * (int) (ghosty[i] / blocksize);

                count = 0;

                if ((screendata[pos] & 1) == 0 && ghostdx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screendata[pos] & 2) == 0 && ghostdy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screendata[pos] & 4) == 0 && ghostdx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screendata[pos] & 8) == 0 && ghostdy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screendata[pos] & 15) == 15) {
                        ghostdx[i] = 0;
                        ghostdy[i] = 0;
                    } else {
                        ghostdx[i] = -ghostdx[i];
                        ghostdy[i] = -ghostdy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghostdx[i] = dx[count];
                    ghostdy[i] = dy[count];
                }

            }

            ghostx[i] = ghostx[i] + (ghostdx[i] * ghostspeed[i]);
            ghosty[i] = ghosty[i] + (ghostdy[i] * ghostspeed[i]);
            drawGhost(g2d, ghostx[i] + 1, ghosty[i] + 1);

            if (pacmanx > (ghostx[i] - 12) && pacmanx < (ghostx[i] + 12)
                    && pacmany > (ghosty[i] - 12) && pacmany < (ghosty[i] + 12)
                    && ingame) {

                dying = true;
            }
        }
    }
    //載入鬼的美術圖
    private void drawGhost(Graphics2D g2d, int x, int y) {

        g2d.drawImage(ghost, x, y, this);
    }
    //PACMAN的移動邏輯判斷
    private void movePacman() {

        int pos;
        short ch;

        if (reqdx == -pacmandx && reqdy == -pacmandy) {
            pacmandx = reqdx;
            pacmandy = reqdy;
            viewdx = pacmandx;
            viewdy = pacmandy;
        }

        if (pacmanx % blocksize == 0 && pacmany % blocksize == 0) {
            pos = pacmanx / blocksize + blocks * (int) (pacmany / blocksize);
            ch = screendata[pos];

            if ((ch & 16) != 0) {
                screendata[pos] = (short) (ch & 15);
                score++;
            }

            if (reqdx != 0 || reqdy != 0) {
                if (!((reqdx == -1 && reqdy == 0 && (ch & 1) != 0)
                        || (reqdx == 1 && reqdy == 0 && (ch & 4) != 0)
                        || (reqdx == 0 && reqdy == -1 && (ch & 2) != 0)
                        || (reqdx == 0 && reqdy == 1 && (ch & 8) != 0))) {
                    pacmandx = reqdx;
                    pacmandy = reqdy;
                    viewdx = pacmandx;
                    viewdy = pacmandy;
                }
            }

            // Check for standstill
            if ((pacmandx == -1 && pacmandy == 0 && (ch & 1) != 0)
                    || (pacmandx == 1 && pacmandy == 0 && (ch & 4) != 0)
                    || (pacmandx == 0 && pacmandy == -1 && (ch & 2) != 0)
                    || (pacmandx == 0 && pacmandy == 1 && (ch & 8) != 0)) {
                pacmandx = 0;
                pacmandy = 0;
            }
        }
        pacmanx = pacmanx + pacmanspeed * pacmandx;
        pacmany = pacmany + pacmanspeed * pacmandy;
    }
    //PACMAN的美術圖與玩家控制建立關聯
    private void drawPacman(Graphics2D g2d) {

        if (viewdx == -1) {
            drawPacnanLeft(g2d);
        } else if (viewdx == 1) {
            drawPacmanRight(g2d);
        } else if (viewdy == -1) {
            drawPacmanUp(g2d);
        } else {
            drawPacmanDown(g2d);
        }
    }
    //PACMAN移動時  繪圖必須跟著動
    private void drawPacmanUp(Graphics2D g2d) {

        switch (pos) {
            case 1:
                g2d.drawImage(pacman2up, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3up, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4up, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }

    private void drawPacmanDown(Graphics2D g2d) {

        switch (pos) {
            case 1:
                g2d.drawImage(pacman2down, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3down, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4down, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }

    private void drawPacnanLeft(Graphics2D g2d) {

        switch (pos) {
            case 1:
                g2d.drawImage(pacman2left, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3left, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4left, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }

    private void drawPacmanRight(Graphics2D g2d) {

        switch (pos) {
            case 1:
                g2d.drawImage(pacman2right, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3right, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4right, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }
    //地圖的美術 畫綠線以及PAC點點
    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < scrsize; y += blocksize) {
            for (x = 0; x < scrsize; x += blocksize) {

                g2d.setColor(mazecolor);
                g2d.setStroke(new BasicStroke(2));

                if ((screendata[i] & 1) != 0) { 
                    g2d.drawLine(x, y, x, y + blocksize - 1);
                }

                if ((screendata[i] & 2) != 0) { 
                    g2d.drawLine(x, y, x + blocksize - 1, y);
                }

                if ((screendata[i] & 4) != 0) { 
                    g2d.drawLine(x + blocksize - 1, y, x + blocksize - 1,
                            y + blocksize - 1);
                }

                if ((screendata[i] & 8) != 0) { 
                    g2d.drawLine(x, y + blocksize - 1, x + blocksize - 1,
                            y + blocksize - 1);
                }

                if ((screendata[i] & 16) != 0) { 
                    g2d.setColor(dotcolor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                i++;
            }
        }
    }
    //初始化遊戲
    private void initGame() {

        pacsleft = 3;
        score = 0;
        initLevel();
        ghosts = 6;
        currentspeed = 3;
    }
    //設定關卡難度  以及轉換地圖
    private void initLevel() {

        int i;
        for (i = 0; i < blocks * blocks; i++) {
            screendata[i] = leveldata[i];
        }

        continueLevel();
    }
    //設定角色數值
    private void continueLevel() {

        short i;
        int dx = 1;
        int random;

        for (i = 0; i < ghosts; i++) {

            ghosty[i] = 4 * blocksize;
            ghostx[i] = 4 * blocksize;
            ghostdy[i] = 0;
            ghostdx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentspeed + 1));

            if (random > currentspeed) {
                random = currentspeed;
            }

            ghostspeed[i] = validspeeds[random];
        }

        pacmanx = 7 * blocksize;
        pacmany = 11 * blocksize;
        pacmandx = 0;
        pacmandy = 0;
        reqdx = 0;
        reqdy = 0;
        viewdx = -1;
        viewdy = 0;
        dying = false;
    }
    
    //載入圖檔
    private void loadImages() {
    	ghost = new ImageIcon(getClass().getResource("Ghost.png")).getImage();
        pacman1 = new ImageIcon(getClass().getResource("PacMan1.png")).getImage();
        pacman2up = new ImageIcon(getClass().getResource("PacMan2UP.png")).getImage();
        pacman3up = new ImageIcon(getClass().getResource("PacMan3UP.png")).getImage();
        pacman4up = new ImageIcon(getClass().getResource("PacMan4UP.png")).getImage();
        pacman2down = new ImageIcon(getClass().getResource("PacMan2Down.png")).getImage();
        pacman3down = new ImageIcon(getClass().getResource("PacMan3Down.png")).getImage();
        pacman4down = new ImageIcon(getClass().getResource("PacMan4Down.png")).getImage();
        pacman2left = new ImageIcon(getClass().getResource("PacMan2Left.png")).getImage();
        pacman3left = new ImageIcon(getClass().getResource("PacMan3Left.png")).getImage();
        pacman4left = new ImageIcon(getClass().getResource("PacMan4Left.png")).getImage();
        pacman2right = new ImageIcon(getClass().getResource("PacMan2.png")).getImage();
        pacman3right = new ImageIcon(getClass().getResource("PacMan3.png")).getImage();
        pacman4right = new ImageIcon(getClass().getResource("PacMan4.png")).getImage();
        

    }
    //繪圖
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
    //整個程式的繪圖和loading圖片的邏輯順序
    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);
        doAnim();

        if (ingame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        g2d.drawImage(ii, 5, 5, this);
    }
    //玩家的控制設定
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (ingame) {
                if (key == KeyEvent.VK_LEFT) {
                    reqdx = -1;
                    reqdy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    reqdx = 1;
                    reqdy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    reqdx = 0;
                    reqdy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    reqdx = 0;
                    reqdy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    ingame = false;
                } else if (key == KeyEvent.VK_PAUSE) {
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }
            } else {
                if (key == 's' || key == 'S') {
                    ingame = true;
                    initGame();
                }
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        repaint();
    }
}
