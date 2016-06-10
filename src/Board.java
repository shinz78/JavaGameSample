import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Board extends JPanel
        implements Runnable {

    private final int B_WIDTH = 1280;
    private final int B_HEIGHT = 960;
    private final int INITIAL_X = 0;
    private final int INITIAL_Y = 700;
    private final int DELAY = 25;
    private final int MOVE_INCREMENT = 25;
    private final int J_HEIGHT = 300;

    private Image mario;
    private Image level;
    private Thread animator;
    private int x, y, jump_y = 0, orig_y = 0;
    private boolean jumping = false, falling = false;

    public Board() {

        initBoard();

        addKeyListener(new TAdapter());
    }

    private void loadLevel() {
        ImageIcon ii = new ImageIcon("mariolevel.png");
        level = ii.getImage();
    }

    private void loadImage() {

        ImageIcon ii = new ImageIcon("mariosmall.png");
        mario = ii.getImage();
    }

    private void loadImageLeft() {

        ImageIcon ii = new ImageIcon("mariosmall_left.png");
        mario = ii.getImage();
    }

    private void initBoard() {

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setDoubleBuffered(true);
        setFocusable(true);

        loadLevel();
        loadImage();

        x = INITIAL_X;
        y = INITIAL_Y;
    }

    @Override
    public void addNotify() {
        super.addNotify();

        animator = new Thread(this);
        animator.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawLevel(g);
        drawMario(g);
    }

    private void drawLevel(Graphics g) {
        g.drawImage(level, 0, 0, this);
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawMario(Graphics g) {

        g.drawImage(mario, x, y, this);
        Toolkit.getDefaultToolkit().sync();
    }

    private void animJump() {

        //Move Mario up or down
        if (jumping) {
            if (y > jump_y) {
                //Move up
                y -= MOVE_INCREMENT;
            }

            if (y <= jump_y) {
                //Stop jumping and start falling
                jumping = false;
                falling = true;
            }
        }

        if (falling) {
            if (y < orig_y) {
                //Move down
                y += MOVE_INCREMENT;
            }

            if (y >= orig_y) {
                //Stop falling
                falling = false;
            }
        }
    }

    private void moveMario(int key) {

        switch ( key ) {
            case KeyEvent.VK_UP:
                y -= MOVE_INCREMENT;
                break;
            case KeyEvent.VK_DOWN:
                y += MOVE_INCREMENT;
                break;
            case KeyEvent.VK_LEFT:
                loadImageLeft();
                x -= MOVE_INCREMENT;
                break;
            case KeyEvent.VK_RIGHT:
                loadImage();
                x += MOVE_INCREMENT;
                break;
            case KeyEvent.VK_SPACE:
                jumping = true;
                jump_y = y - J_HEIGHT;
                orig_y = y;
                break;
        }

    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            moveMario(key);

        }

        @Override
        public void keyReleased(KeyEvent e) {

            int key = e.getKeyCode();

            //MORE
        }
    }

    @Override
    public void run() {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();

        while (true) {

            repaint();

            if (jumping || falling) {
                animJump();
            }

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;

            if (sleep < 0) {
                sleep = 2;
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("Interrupted: " + e.getMessage());
            }

            beforeTime = System.currentTimeMillis();
        }
    }
}