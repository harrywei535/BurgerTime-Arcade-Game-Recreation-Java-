// Pickle.java
// Harry Wei && Leo Lei
// Class for the pickle enemy
// Copies player's direction if possible, otherwise will follow the egg's movement algorithm
import java.awt.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

class Pickle extends Enemy {
    public Pickle(int x, int y) {
        super(x, y);
        loadPics();
    }

    // adds score when the pickle is squished
    public void addScore(BurgerTimePanel bPanel) {
        bPanel.addScore(200); // pickle is worth 200 points
    }

    // returns how many points the pickle is worth
    public int pointValue() {
        return 200;
    }

    // checks collisions between the pickle and layers, returns true if ther is a
    // collison and false otherwise
    public boolean collides(Layer lay, BurgerTimePanel bPanel) {
        // checks if enemy is under the layer
        int layX = lay.getX();
        int layY = lay.getY();
        if (layX < x + 30 && layX + lay.getWidth() > x - 30 && layY >= y - 92 && y - 64 > layY && alive) {
            alive = false;
            resetFrame(); // resets the frame to allow death animation to occur
            bPanel.addScore(200);
            return true;
        }
        return false;
    }

    // loads all images for the pickle
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
                tmp[j] = new ImageIcon("pics/enemies/pickle/pickle" + i + j + ".png").getImage();
            }
            pic[i] = tmp;
        }

    }

    // method to determine which direction the pickle will go to chase the player
    // the pickle is as copy cat and will try to mimic the player's movements. if it
    // can't do that, it will chase the player similar to egg
    public void check(BufferedImage mask, Player player) {
        ArrayList<Integer> cleared = getCleared(mask); // gets all directions the pickle can go
        if (cleared.contains(player.getDirection())) {
            direction = player.getDirection(); // sets the direction to the player's direction if it is clear
        } else {
            // same code as that of the egg
            int[] distances = new int[cleared.size()];
            for (int i = 0; i < cleared.size(); i++) {
                int playerD = player.getDirection();
                int pX = player.getX();
                int pY = player.getY();
                if (playerD == UP) {
                    pY -= 84;
                } else if (playerD == DOWN) {
                    pY += 20;
                } else if (playerD == RIGHT) {
                    pX += 20;
                } else {
                    pX -= 20;
                }
                distances[i] = distance((int) cleared.get(i), pX, pY);
            }
            int shortest = distances[0];
            int d = cleared.get(0);
            for (int i = 0; i < distances.length; i++) {
                if (distances[i] < shortest) {
                    shortest = distances[i];
                    d = cleared.get(i);
                }
            }
            direction = d;
        }
    }
}