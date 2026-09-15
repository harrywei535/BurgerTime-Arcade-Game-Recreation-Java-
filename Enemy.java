/*
Enemy.java
Leo Lei & Harry Wei
This is a super class for all enemies in the game.
The enemies include the egg, the pickle, and the sausage.
Unlike the original game, each enemy has its own pathfinding.
Other than that and looking different, they walk the same and all kill the player
if they collide
*/

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

class Enemy {
    public static final int DEATH = 4, PEPPERED = 5; // used to get the images for the animation when the enemy is dead
                                                     // or peppered
    protected static final int DOWN = 0, LEFT = 1, UP = 2, RIGHT = 3; // direction of the enemy
    protected int x, y, num, vx, vy; // x, y coordinates in pixels, speed in x and y direction, and individual number
                                     // used as the order the enemies spawn at the start of each level
    protected int direction; // direction of enemy
    // frame of enemy, delay of each animation, wait for each delay
    // respawnCD creates a delay for before the enemy respawns
    // pepperedCD is the time the enemy stays peppered
    protected int frame, delay, wait, respawnCD, pepperedCD;

    // alive or dead; whether the enemy is respawning, peppered, falling with the
    // layer or has fallen to the bottom
    protected boolean alive = false, respawning, peppered, fall, delaySpawn = true, fallen;
    protected Image[][] pic; // images for enemies
    protected Layer moveWith; // the layer the enemy will move with if it falls

    //constructed based on position
    public Enemy(int xx, int yy) {
        x = xx;
        y = yy;
        resetEnemy();
    }

    // method to reset an enemy after death
    public void resetEnemy() {
        frame = 0;
        delay = 0;
        pepperedCD = 100; // frame counter for when enemies are peppered

        moveWith = null;
        fall = false;
        alive = true;
        peppered = false;
        respawning = false;
        fallen = false;

        if (x < 450) {
            direction = RIGHT;
        } else {
            direction = LEFT;
        }
    }

    // getter and setters
    public void setVX(int x) {
        vx = x;
    }

    public void setVY(int y) {
        vy = y;
    }

    public void setNum(int n) {
        num = n;
    }

    public int getNum() {
        return num;
    }

    public boolean isRespawning() {
        return respawning;
    }

    public void setRespawning(boolean b) {
        respawning = b;
    }

    public boolean getFallen() {
        return fallen;
    }

    public int getFrame() {
        return frame;
    }

    public boolean getFall() {
        return fall;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isPeppered() {
        return peppered;
    }

    public void setRespawnCD(int dead) {
        respawnCD = 10 * dead + 5;
    }

    public Layer getMoveWith() {
        return moveWith;
    }

    public void setFallen(boolean b) {
        fallen = b;
    }

    //puts enemy offscreen.
    public void offScreen() {
        x = 1000;
        y = 1000;
    }

    // each enemy has a different pointvalue that the player gets when they die
    public int pointValue() {
        return pointValue();
    }

    // resets everything for frame
    public void resetFrame() {
        frame = 0;
        delay = 0;
    }

    // method to check if the enemy is on a green portion of the mask which tells it
    // to go up when the enemy has fallen under the platform
    // mask has a green area next to the white platforms
    private boolean onGreen(int x, int y, BufferedImage mask) {
        int GREEN = 0xFF00FF00;
        if (x < 0 || x >= mask.getWidth(null) || y < 0 || y >= mask.getHeight(null)) {
            return false;
        }
        int c = mask.getRGB(x, y);
        return c == GREEN;
    }

    // method to get the distance from the feet of an enemy to the green in pixels. if the
    // enemy is not directly on top of the green, returns the distance from the
    // bottom of the screen
    private int distanceToGreen(int x, BufferedImage mask) {
        int d = 0;
        while (y + d < 850) {
            if (onGreen(x, y + d, mask)) {
                break;
            }
            d++;
        }
        return d;
    }

    // method to check if the enemy is on a platform
    private boolean isOnPlatform(int x, int y, BufferedImage mask) {
        int blue = 0xFF0000FF; // colours for platforms
        int cyan = 0xFF00FFFF;
        if (x < 0 || x >= mask.getWidth(null) || y < 0 || y >= mask.getHeight(null)) {
            return false;
        }
        int c = mask.getRGB(x, y);
        if (c != blue) {
            return c == cyan;
        }
        return true;
    }

    // method to get the distance above an enemy that will be clear in pixels
    private int distanceToClear(BufferedImage mask) {
        int d = 0;
        while (!clear(x, y - d, mask)) {
            d++;
        }
        return d + 8; // adds 8 since platform is 8 pixels thick
    }

    // method to get the distance under the enemy that will be on a platform in
    // pixels
    // used to readjust the enemy onto the platform after being dropped
    private int adjustY(BufferedImage mask) {
        int d = 0;
        while (!isOnPlatform(x, y + d, mask)) {
            d++;
        }
        return d;
    }

    // method to change the frame of an enemy when it's peppered or dead
    public void frame() {
        if (peppered) { // changes the wait based on different animations
            wait = 5;
        } else if (!alive) {
            wait = 3;
        } else {
            wait = 4;
        }
        delay++;
        if (delay % wait == 0) {
            //switches frames
            if (alive) {
                frame = (frame + 1) % 2;
            } else {
                frame = (frame + 1) % 4;
            }
        }
    }

    // method to get the distance of an enemy if it were to go in a certain
    // direction towards a target x and y as a straight line in pixels
    public int distance(int direction, int targetX, int targetY) {
        if (direction == UP) {
            return (int) Math.hypot(targetX - x, targetY - (y - 8));
        } else if (direction == DOWN) {
            return (int) Math.hypot(targetX - x, targetY - (y + 8));
        } else if (direction == LEFT) {
            return (int) Math.hypot(targetX - (x - 8), targetY - y);
        } else {
            return (int) Math.hypot(targetX - (x + 8), targetY - y);
        }
    }

    // method to get the opposite direction of the enemy
    public int opposite(int direction) {
        if (direction == RIGHT) {
            return LEFT;
        } else if (direction == LEFT) {
            return RIGHT;
        } else if (direction == UP) {
            return DOWN;
        } else {
            return UP;
        }

    }

    // method to check if the enemy can walk at a certain pixel
    public boolean clear(int x, int y, BufferedImage mask) {
        int WALL = 0xFFFF0000;// walls are RED
        if (x < 0 || x >= mask.getWidth(null) || y < 0 || y >= mask.getHeight(null)) {
            return false;
        }
        int c = mask.getRGB(x, y);
        return c != WALL;
    }

    // method to check if the enemy could walk in a certain direction
    public boolean clear(int direction, BufferedImage mask) {
        if (direction == UP) {
            return clear(x - 19, y - 8, mask) && clear(x + 19, y - 8, mask);
        } else if (direction == DOWN) {
            return clear(x - 19, y + 8, mask) && clear(x + 19, y + 8, mask);
        } else if (direction == LEFT) {
            return clear(x - 34, y, mask);
        } else {
            return clear(x + 32, y, mask);
        }
    }

    // checks collisions between layers and enemies
    public boolean collides(Layer lay, BurgerTimePanel bPanel) {
        return collides(lay, bPanel); // calls individual collides methods
    }

    // checks collisions between peppers and enemies
    public void collides(Pepper p) {
        int pX = p.getX();
        int pY = p.getY();
        Rectangle pRectangle = new Rectangle(pX, pY, 64, 64);
        Rectangle eRectangle = new Rectangle(x - 32, y - 64, 64, 64);
        if (pRectangle.intersects(eRectangle)) {
            peppered = true;
            resetFrame();
        }
    }

    // checks collisions between enemies and player, same as overlaps is Layer class
    public boolean collides(Player player) {
        int x1 = x - 16;
        int y1 = y - 32;
        int x2 = x + 16;
        int y2 = y - 10;
        int px1 = player.getX() - 16;
        int py1 = player.getY() - 32;
        int px2 = player.getX() + 16;
        int py2 = player.getY();

        return x2 >= px1 && x1 <= px2 && y2 >= py1 && y1 <= py2;
    }

    // method that stuns enemies when peppered
    public void peppered() {
        pepperedCD--;
        if (pepperedCD < 0) {
            peppered = false;
            pepperedCD = 100;
        }
    }

    // respawns enemy
    public void respawn(Point[] respawnPoints) {
        offScreen();
        respawnCD--;
        if (respawnCD < 0) {
            Random rand = new Random();
            Point p = respawnPoints[rand.nextInt(respawnPoints.length)]; // gets random respawn point
            x = (int) p.getX();
            y = (int) p.getY();
            resetEnemy(); // resets enemy
        }
    }

    // method to check if the enemy can move to a range of pixels within their vx
    // speed, allowing them to change directions to up or down without having to be
    // perfectly aligned with the ladders
    private boolean partialClear(int y, BufferedImage mask) {
        boolean cl1 = clear(x - 20, y, mask); // pixels we are checking to see if the player can move up or down
        boolean cl2 = clear(x + 20, y, mask);
        //if they are both clear then it's already perfectly aligned
        if (cl1 && cl2) {
            return true;
        }

        for (int i = 1; i < vx; i++) {
            //adjusts horizontally until it is aligned with the ladder(both cl1&cl2 are clear)
            //if it can't, returns false.
            if (cl1 && !cl2 && clear(x + 20 - i, y, mask)) {
                x -= i; // aligns the enemy with the ladder
                return true;
            } else if (cl2 && !cl1 && clear(x - 20 + i, y, mask)) {
                x += i;
                return true;
            }
        }
        return false;
    }

    // method to get all directions an enemy can travel. returns them as an Int
    // arrayList
    public ArrayList<Integer> getCleared(BufferedImage mask) {
        ArrayList<Integer> cleared = new ArrayList<>();
        if (clear(LEFT, mask)) {
            cleared.add(LEFT);
        }
        if (clear(RIGHT, mask)) {
            cleared.add(RIGHT);
        }
        if (clear(UP, mask) || partialClear(y - 8, mask)) {//y-8 to check above enemy
            cleared.add(UP);
        }
        if (clear(DOWN, mask) || partialClear(y + 8, mask)) {//y+8 to check underneath
            cleared.add(DOWN);
        }

        if (cleared.size() > 1) { // removes the opposite direction to ensure enemies don't turn around in the
                                  // middle of platforms and ladders
            ArrayList<Integer> toRemove = new ArrayList<>();
            toRemove.add(opposite(direction));
            cleared.removeAll(toRemove);
        }
        return cleared;
    }

    // method to check if an enemy is on the layer when it is falling
    public boolean on(Layer lay, Player player) {
        // the enemy must be on the same platform as the player to be dropped
        if (x + 20 >= lay.getX() && x - 20 <= lay.getX() + lay.getWidth() && player.getY() == y) {
            if (lay.getY() - 10 <= y && lay.getY() >= y) {
                fall = true;
                moveWith = lay; // sets the layer it will now move with
                return true;
            }
        }
        return false;
    }

    // method to move the enemy with the layer, returns true if the layer has
    // stopped falling, false otherwise
    public boolean moveWithLayer() {
        if (!fallen) { // checks to see if the enemy has fallen under the platform
            y = moveWith.getY();
        }
        if ((moveWith.isAtDestination() || moveWith.getFallCount() >= moveWith.getEnemiesOn() * 2 + 1) && !fallen) {
            alive = false;
            return true;
        }
        return false;
    }

    // when the enemy falls with the layer, but ends up on a platform, it stays
    // invisible for a while and then respawns. this method makes it wait and
    // readjusts the enemy's y coordinate onto the platform and resets the enemy
    public void waitRespawn(BufferedImage mask) {
        respawnCD--;
        if (respawnCD < 0) {
            y += adjustY(mask);
            resetEnemy();
        }
    }

    // makes the enemy walk back onto the platform after being dropped with a layer
    // under the platform. The enemy must first walk either left or right depending
    // on the side of the screen it is on, then it will reach a green box on the
    // mask telling it to change its direction to up and go up until it reaches the
    // platform
    public void backToPlatform(BufferedImage mask) {
        respawnCD--;
        if (respawnCD < 0) {
            alive = true; // allows the enemy to be drawn
            peppered = false; // if the enemy was peppered, it must be set to false
            if (!onGreen(x, y + distanceToGreen(x, mask), mask)) { // if it not on the green
                if (x < 832 / 2) {
                    direction = LEFT;
                } else if (x > 832 / 2) {
                    direction = RIGHT;
                }
            } else if (respawnCD < -5) { // ensures it doesn't go up instantly
                if (direction != UP) {
                    delay = 0; // resets delay to make it go up instantly
                }
                direction = UP;
            }
            if (direction == UP) {
                climbframe();
            } else { // when direction is left or right
                moveFrameToPlat(mask);
            }
        }

        if (isOnPlatform(x, y, mask)) {
            if (distanceToClear(mask) < 12) {
                y -= distanceToClear(mask); // adjusts y coodinate
            }
            x -= x % 8;
            y += y % 8 - 4; // resets enemy back onto platform
            resetEnemy();
        }
    }

    // sets the spawn cooldown at the start when the enemies walk in for the first
    // time to stagger them
    public void setSpawnCD(int num) {
        respawnCD = 25 * num;
    }

    // resets everything when the enemies first appear on the screen after the
    // staggered start
    public void spawn() {
        respawnCD--;
        if (respawnCD < 0) {
            delaySpawn = false;
            resetEnemy();
        }
    }

    // method for when the enemy is climbing a ladder.
    // this is used to give the enemy the effect of
    // stepping on each step of the ladder
    private void climbframe() {
        wait = vy; // change wait to move faster
        int end = 2;
        // switches feet every two frames
        if (delay % wait == 0 || delay % wait == wait / 2) {
            frame = (frame + 1) % end;
            y -= 12;// each step 4 thick
        } else if (delay % wait == wait / 4 || delay % wait == 3 * wait / 4) {
            // if it's not switching, we move the picture down 4
            // to give its stepping effect
            y += 4;
        }
        delay++;
    }

    // when the enemy is descending a ladder
    // gives the enemy the same effect of stepping on each step of the ladder
    private void descendframe() {
        wait = vy;
        int end = 2;
        // switches feet every 8 frames
        if (delay % wait == 0 || delay % wait == wait / 2) {
            frame = (frame + 1) % end;
            y += 12;// each step 4 thick
        } else if (delay % wait == wait / 4 || delay % wait == 3 * wait / 4) {
            // if it's not switching, we move the picture down 4
            // to give its stepping effect
            y -= 4;
        }
        delay++;
    }

    // moves the enemy left and right with the animation
    private void moveFrame() {
        wait = 4;
        int end = 2; // length of animation
        if (delay % 4 == 0) {
            frame = (frame + 1) % end;
        }
        if (delay % wait == 0) {
            if (direction == LEFT) {
                x -= vx;
            } else if (direction == RIGHT) {
                x += vx;
            }
        }
        delay++;
    }

    // moves the enemy with the animation after it has fallen under the platform. if
    // the difficulty(speed) is too high, the enemy might walk over the green box that
    // tells it to go up, so there is an autocorrect that adjusts it onto the green
    private void moveFrameToPlat(BufferedImage mask) {
        wait = 4;
        int end = 2;
        if (delay % 4 == 0) { // changes animation
            frame = (frame + 1) % end;
        }

        boolean autocorrect = false; // whether the enemy has been autocorrected onto the green or not
        if (direction == LEFT) {
            for (int i = 1; i < vx; i++) { // finds if the enemy is within range of the green
                if (onGreen(x - i, y + distanceToGreen(x - i, mask), mask)) {
                    x -= i;
                    autocorrect = true;
                    break;
                }
            }
        } else { // same as previous
            for (int i = 1; i < vx; i++) {
                if (onGreen(x + i, y + distanceToGreen(x + i, mask), mask)) {
                    x += i;
                    autocorrect = true;
                    break;
                }
            }
        }
        if (!autocorrect) { // when it's not autocorrected, it moves normally
            if (delay % wait == 0) {
                if (direction == LEFT) {
                    x -= vx;
                } else if (direction == RIGHT) {
                    x += vx;
                }
            }
        }
        delay++;
    }

    // each enemy is worth different amount of score and has individual methods
    public void addScore(BurgerTimePanel bPanel) {
    }

    // each enemy has a different way to chase the player. this method determines
    // the direction of the enemy based on the position of the player
    public void check(BufferedImage mask, Player player) {
    }

    // move method for enemies
    public void move(Player player, BufferedImage mask) {
        if (alive && !peppered) { // normal left, right, up, down movement
            int oldDirection = direction;

            check(mask, player); // changes the direction of the enemy

            if (oldDirection != direction) {
                delay = 0; // ensures enemy moves on first time changing direction
                if (oldDirection == UP || oldDirection == DOWN) {
                    if (direction == LEFT || direction == RIGHT) {
                        if (y % 8 != 6) {
                            y -= y % 8 + 2; // adjusts the y to be on the platform
                        }
                    }
                } else if (oldDirection == LEFT || oldDirection == RIGHT) {
                    if (direction == UP || direction == DOWN) {
                        if (x % 8 != 0) {
                            x -= x % 8; // adjusts the x to be aligned with the ladders
                        }
                    }
                }
            }
            if (direction == UP) {
                climbframe();
            } else if (direction == DOWN) {
                descendframe();
            } else if (direction == LEFT || direction == RIGHT) {
                moveFrame();
            }

        } else { // dead, peppered, fallen
            frame();
        }

    }

    // draw method
    public void draw(Graphics g) {
        if (alive && !peppered) { // normal movement
            g.drawImage(pic[direction][frame], x - 32, y - 64, null);
        } else if (!alive && !fallen && !respawning) { // squish animation
            g.drawImage(pic[DEATH][frame], x - 32, y - 64, null);
        } else if (peppered) { // peppered animation
            g.drawImage(pic[PEPPERED][frame], x - 32, y - 64, null);
        }
    }
}