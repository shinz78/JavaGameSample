import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Board extends JPanel
        implements Runnable {

    private final int B_WIDTH = 1280;
    private final int B_HEIGHT = 960;
    private final int INITIAL_X = 0;
    private final int INITIAL_Y = 690;
    private final int DELAY = 30;
    private final int MOVE_INCREMENT = 25;
    private final int J_HEIGHT = 300;
    private final String spriteLevel = "sprites/mariolevel.png";
    private final String soundJump = "sounds/Mario-jump-sound.mp3";
    private final String spriteStandingRight = "sprites/standingRight.png";
    private final String spriteStandingLeft = "sprites/standingLeft.png";
    private final String spriteWalkingRight = "sprites/walkingRight.png";
    private final String spriteWalkingLeft = "sprites/walkingLeft.png";
    private final String spriteJumpingRight = "sprites/jumpingRight.png";
    private final String spriteJumpingLeft = "sprites/jumpingLeft.png";

    private Image mario;
    private Image level;
    private Thread animator;
    private int x, y, jump_y = 0, orig_y = 0;
    private boolean walking = false, jumping = false, falling = false;
    private String direction = "right";

    public Board() {

        initBoard();

        addKeyListener(new TAdapter());

    }

    private void loadLevel() {
        ImageIcon ii = new ImageIcon(spriteLevel);
        level = ii.getImage();
    }

    private void loadMario() {

        String sprite = "";

        if (jumping || falling) {

            switch (direction) {
                case "left":
                    sprite = spriteJumpingLeft;
                    break;
                case "right":
                    sprite = spriteJumpingRight;
                    break;
            }

            animJump();

        } else {
            switch (direction) {
                case "left":
                    if (walking) {
                        sprite = spriteWalkingLeft;
                    } else {
                        sprite = spriteStandingLeft;
                    }
                    break;
                case "right":
                    if (walking) {
                        sprite = spriteWalkingRight;
                    } else {
                        sprite = spriteStandingRight;
                    }
                    break;
            }
        }

        ImageIcon ii = new ImageIcon(sprite);
        mario = ii.getImage();
    }

    private void initBoard() {

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setDoubleBuffered(true);
        setFocusable(true);

        loadLevel();
        SoundEffect.init();

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
                //y -= MOVE_INCREMENT;
                break;
            case KeyEvent.VK_DOWN:
                //y += MOVE_INCREMENT;
                break;
            case KeyEvent.VK_LEFT:
                walking = true;
                direction = "left";
                x -= MOVE_INCREMENT;
                break;
            case KeyEvent.VK_RIGHT:
                walking = true;
                direction = "right";
                x += MOVE_INCREMENT;
                break;
            case KeyEvent.VK_SPACE:
                jumping = true;
                jump_y = y - J_HEIGHT;
                orig_y = y;
                SoundEffect.JUMP.play();
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

            switch ( key ) {
                case KeyEvent.VK_LEFT:
                    walking = false;
                    break;
                case KeyEvent.VK_RIGHT:
                    walking = false;
                    break;
            }
        }
    }

    @Override
    public void run() {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();

        while (true) {

            loadMario();
            repaint();

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