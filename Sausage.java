// Sausage.java
// Harry Wei && Leo Lei
// Class for the sausage enemy
// Finds the closest path to the player in a straight line
import java.awt.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

class Sausage extends Enemy {

    public Sausage(int x, int y) {
        super(x, y);
        loadPics();
    }

    // adds score when the sausage is squished
    public void addScore(BurgerTimePanel bPanel) {
        bPanel.addScore(100); // sausage is worth 100 points
    }

    // returns how many points the sausage is worth
    public int pointValue() {
        return 100;
    }

    // checks collisions between the sausage and the layers, returns true if there
    // is a collision and false otherwise
    public boolean collides(Layer lay, BurgerTimePanel bPanel) {
        // checks if enemy is under the layer
        int layX = lay.getX();
        int layY = lay.getY();
        if (layX < x + 30 && layX + lay.getWidth() > x - 30 && layY >= y - 92 && y - 64 > layY && alive) {
            alive = false;
            resetFrame();
            bPanel.addScore(100);
            return true;
        }
        return false;
    }

    // method to determine which direction the sausage will go to chase the player
    // the goal of the sausage is to take the shortest path towards the player
    public void check(BufferedImage mask, Player player) {
        ArrayList<Integer> cleared = getCleared(mask); // gets all directions the sausage can go

        int[] distances = new int[cleared.size()]; // array of distances to the player for each direction the sausage
                                                   // can travel
        for (int i = 0; i < cleared.size(); i++) {
            distances[i] = (distance((int) cleared.get(i), player.getX(), player.getY()));
        }

        int shortest = distances[0]; // shortest distance
        int d = cleared.get(0); // direction to achieve shortest distance
        for (int i = 0; i < distances.length; i++) { // finds shortest distance
            if (distances[i] < shortest) {
                shortest = distances[i];
                d = cleared.get(i);
            }
        }
        direction = d; // sets direction to be that where you will get the shortest distance
    }

    // method to load all images for the sausage
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
                tmp[j] = new ImageIcon("pics/enemies/sausage/sausage" + i + j + ".png").getImage();
            }
            pic[i] = tmp;
        }

    }

}