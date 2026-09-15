// Egg.java
// Harry Wei && Leo Lei
// Class for the egg enemy
// Egg's goal is to cut off the player

import java.awt.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

class Egg extends Enemy {
    public Egg(int x, int y) {
        super(x, y);
        loadPics();
    }

    // adds score when the egg is squished
    public void addScore(BurgerTimePanel bPanel) {
        bPanel.addScore(300); // egg is worth 300 points
    }

    // returns how many points the egg is worth
    public int pointValue() {
        return 300;
    }

    // checks collisions between the egg and the layers, returns true if there is a
    // collision and false otherwise
    public boolean collides(Layer lay, BurgerTimePanel bPanel) {
        // checks if enemy is under the layer
        int layX = lay.getX();
        int layY = lay.getY();
        if (layX < x + 30 && layX + lay.getWidth() > x - 30 && layY >= y - 92 && y - 64 > layY && alive) {
            alive = false;
            resetFrame(); // resets the frame to allow death animation to occur
            bPanel.addScore(300);
            return true;
        }
        return false;
    }

    // loads all images for the egg
    private void loadPics() {
        pic = new Image[6][];
        for (int i = 0; i < 6; i++) {
            Image[] tmp;
            if (i == 4) {
                tmp = new Image[4];
            } else {
                tmp = new Image[2];
            }
            for (int j = 0; j < tmp.length; j++) {
                tmp[j] = new ImageIcon("pics/enemies/egg/egg" + i + j + ".png").getImage();
            }
            pic[i] = tmp;
        }
    }

    // method to determine which direction the egg will go to chase the player
    // the egg's goal is to cut the player off
    public void check(BufferedImage mask, Player player) {
        ArrayList<Integer> cleared = getCleared(mask); // gets which directions are clear to move

        int[] distances = new int[cleared.size()]; // distances to the player that the egg will be if the egg decides to
                                                   // go a certain direction
        for (int i = 0; i < cleared.size(); i++) {
            int playerD = player.getDirection();
            int pX = player.getX(); // target x of the egg
            int pY = player.getY(); // targer y of the egg
            // changes the target based on player's current direction
            if (playerD == UP) {
                pY -= 96;
            } else if (playerD == DOWN) {
                pY += 32;
            } else if (playerD == RIGHT) {
                pX += 64;
            } else {
                pX -= 64;
            }
            distances[i] = distance((int) cleared.get(i), pX, pY); // adds the distance to the array
        }

        int shortest = distances[0]; // shortest distance to player
        int d = cleared.get(0); // direction to achieve shortest distance
        for (int i = 0; i < distances.length; i++) {// finds shortest distance
            if (distances[i] < shortest) {
                shortest = distances[i];
                d = cleared.get(i);
            }
        }

        direction = d; // sets the egg's direction to determined shortest paths
    }
}