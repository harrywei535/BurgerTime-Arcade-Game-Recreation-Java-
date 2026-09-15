/*
Leo Lei & Harry Wei
Player.java
This class is for the player (Peter pepper) that the player controls.
It draws, controls its movement, peppers, etc.
Its draw method also draws the lives of the player on the bottom corner
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.ImageIcon;
import java.awt.image.*;

class Player {
    //position, lives, cooldowns, etc.
    private int x, y, lives, peppers, pepperCD, pepperWait, winCD, deadCD;
    //direction, controlled by magic numbers below it
    private int direction;
    public static final int DOWN = 0, LEFT = 1, UP = 2, RIGHT = 3;

    private Image[][] pics;//pictures
    private int frame, delay, wait;
    private BufferedImage mask;//mask for the movement
    private Image playerLives;
    private BurgerTimePanel bPanel;
    private int action = NONE;
    public static final int WALK = 10, PEPPER = 20, NONE = 30, WIN = 8, DEAD = 9;
    private boolean peppered, win, dead;

    //position, mask, and panel
    public Player(int xx, int yy, BufferedImage im, BurgerTimePanel panel) {
        x = xx;
        y = yy;
        lives = 3;
        peppers = 5;
        resetPlayer();
        bPanel = panel;
        mask = im;
        loadPics();
    }

    // getter setter methods
    public void setX(int xx) {
        x = xx;
    }

    public void setY(int yy) {
        y = yy;
    }

    public void setMask(BufferedImage im) {
        mask = im;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLives() {
        return lives;
    }

    public int getPeppers() {
        return peppers;
    }

    public int getPepperCD() {
        return pepperCD;
    }

    public int getDirection() {
        return direction;
    }

    // gets rough position for each of the player's feet
    //for the layer depressed.
    public Point getRightFoot() {
        return new Point(x + 8, y);
    }

    public Point getLeftFoot() {
        return new Point(x - 10, y);
    }

    public boolean getWin() {
        return win;
    }

    public boolean getDead() {
        return dead;
    }

    // resets everything to do with peppers
    public void resetPeppers() {
        peppered = false;
        pepperCD = 20;
        pepperWait = 2;
    }

    // resets the player after death or win
    public void resetPlayer() {
        winCD = 140;
        deadCD = 100;
        win = false;
        dead = false;
        resetFrame();
    }

    //add and lose peppers
    public void addPepper() {
        peppers++;
    }

    public void losePepper() {
        peppers--;
    }

    //adds life
    public void addLife() {
        lives++;
    }

    // loads all pictures for player
    private void loadPics() {
        frame = 0;
        delay = 0;
        pics = new Image[10][];
        for (int i = 0; i < 10; i++) {
            Image[] tmp;
            if (i == 9) {
                tmp = new Image[5];
            } else if (i == 8) {

                tmp = new Image[2];
            } else if (i > 3) {

                tmp = new Image[1];
            } else {
                tmp = new Image[3];
            }
            for (int j = 0; j < tmp.length; j++) {
                tmp[j] = new ImageIcon("pics/player/player" + i + j + ".png").getImage();
            }
            pics[i] = tmp;
        }
        playerLives = new ImageIcon("pics/player/playerLives.png").getImage();
    }

    // changes frame for animations
    private void frame() {
        delay++;
        // for walking
        int end = 3;
        wait = 2;
        if (action == WIN) {
            wait = 7;
            end = 2;
        } else if (action == DEAD) {
            wait = 4;
            end = 5;
        }

        if (delay % wait == 0) {
            if (action == DEAD && frame == 4) {
                frame = 2;
            }
            frame = (frame + 1) % end;
        }
    }

    // method for when the player is climbing a ladder.
    // this is used to give the player the effect of
    // stepping on each step of the ladder
    private void climbframe() {
        delay++;
        wait = 8;
        // we only want player20 and player21 as our frames when climbing
        int end = 2;
        // switches feet every two frames
        if (delay % wait == 0 || delay % wait == 4) {
            frame = (frame + 1) % end;
            y -= 12;// each step 4 thick
        } else if (delay % wait == 2 || delay % wait == 6) {
            // if it's not switching, we move the picture down 4
            // to give its stepping effect

            y += 4;
        }
    }

    //gives the player the effect of moving up and down a ladder when descending it
    private void descendframe() {
        delay++;
        wait = 8;
        // we only want player20 and player21 as our frames when climbing
        int end = 2;
        // switches feet every two frames
        if (delay % wait == 0 || delay % wait == 4) {
            frame = (frame + 1) % end;
            y += 12;// each step 4 thick
        } else if (delay % wait == 2 || delay % wait == 6) {
            // if it's not switching, we move the picture down 4
            // to give its stepping effect
            y -= 4;
        }
    }

    // checks if player is able to move based on the colours of the mask and
    // player's position
    private boolean clear(int x, int y) {
        int WALL = 0xFFFF0000;// walls are RED
        if (x < 0 || x >= mask.getWidth(null) || y < 0 || y >= mask.getHeight(null)) {
            return false;
        }
        int c = mask.getRGB(x, y);
        return c != WALL;
    }

    // checks if the player can move to a spot even though they are not fully on the
    // platform or ladder and adjusts them accordingly
    private boolean partialClear(int x, int y, int d) {
        if ((d == RIGHT || d == LEFT)) {
            //checks ahead for up and down and moves to that position
            if (clear(x, y + 8)) {
                setY(y + 8);
                return true;
            } else if (clear(x, y - 8)) {
                setY(y - 8);
                return true;
            }
        } else if (d == UP || d == DOWN) {
            //similar to enemy partial clear method for moving up and down
            //however it doesn't need a for loop since it always moves in a grid at the same speed
            boolean cl1 = clear(x - 17, y);
            boolean cl2 = clear(x + 17, y);
            if (cl1 && !cl2 && clear(x + 9, y)) {
                setX(x - 8);
                return true;
            } else if (cl2 && !cl1 && clear(x - 9, y)) {
                setX(x + 8);
                return true;
            } else if (cl1 && cl2) {
                return true;
            }
        }
        return false;
    }

    // returns the action that the player is doing
    public int checkAction(boolean[] keys) {
        if (win) {
            return WIN;
        } else if (dead) {
            return DEAD;
        } else if ((keys[KeyEvent.VK_SPACE] && pepperCD < 0 && peppers > 0) || peppered) {
            return PEPPER;
        } else if (keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_UP]
                || keys[KeyEvent.VK_DOWN]) {
            return WALK;
        } else {
            return NONE;
        }
    }

    // method to reset everything when player beats a level
    public void win() {
        winCD--; // delay so that winning animation can play
        if (!win) {
            resetFrame();
        }
        win = true;
        if (winCD < 0) {
            bPanel.changeLevel();
            resetPlayer();
        }
    }

    // method to reset everything when player dies
    public void die() {
        deadCD--;
        if (!dead) {
            resetFrame();
        }
        dead = true;
        if (deadCD < 0) {
            bPanel.playerDied();
            resetPlayer();
            lives--;
        }
    }

    // method to reset the frame
    public void resetFrame() {
        frame = 0;
    }

    // move method for player
    public void move(boolean[] keys) {
        action = checkAction(keys);// checks what action
        pepperCD--; // lowers pepper cooldown
        if (peppered) { // causes the slight pause in animation when the pepper is used
            pepperWait--; // pepperWait variable determines the time of that pause
        }
        if (action == PEPPER) {
            peppered = true;
            if (pepperWait < 0) {
                losePepper();
                resetPeppers();
            }

        } else if (action == WALK) {
            if (x % 8 == 0 && y % 8 == 6) { // forces player to walk in a grid
                if (keys[KeyEvent.VK_RIGHT] && (clear(x + 32, y) || partialClear(x + 32, y, RIGHT))) {
                    direction = RIGHT;
                } else if (keys[KeyEvent.VK_LEFT] && (clear(x - 34, y) || partialClear(x - 34, y, LEFT))) {
                    direction = LEFT;
                } else if (keys[KeyEvent.VK_UP]
                        && (clear(x - 17, y - 8) || (partialClear(x, y - 8, UP)))
                        && (clear(x + 17, y - 8) || (partialClear(x, y - 8, UP)))) {
                    direction = UP;
                } else if (keys[KeyEvent.VK_DOWN]
                        && (clear(x - 17, y + 8) || (partialClear(x, y + 8, DOWN)))
                        && (clear(x + 17, y + 8) || (partialClear(x, y + 8, DOWN)))) {
                    direction = DOWN;
                } else {
                    action = NONE;
                }
            }
            if (action != NONE) {
                if (direction == RIGHT || direction == LEFT) {
                    frame();
                } else if (direction == UP) {
                    climbframe();
                } else if (direction == DOWN) {
                    descendframe();
                }
                if (direction == RIGHT) {
                    x += 4;
                } else if (direction == LEFT) {
                    x -= 4;
                }
            }
        } else if (action == WIN || action == DEAD) {
            frame();
        }

    }

    // draw method for player
    public void draw(Graphics g) {
        if (action == WIN || action == DEAD) {
            g.drawImage(pics[action][frame], x - 32, y - 64, null);
        } else if (action == WALK || action == NONE) {
            g.drawImage(pics[direction][frame], x - 32, y - 64, null);
        } else { // drawing peppers
            g.drawImage(pics[direction + 4][0], x - 32, y - 64, null);
        }
        for (int i = 0; i < lives - 1; i++) { // draws the lives of the player
            g.drawImage(playerLives, 0, 818 - (i * 32), null);
        }
    }

}