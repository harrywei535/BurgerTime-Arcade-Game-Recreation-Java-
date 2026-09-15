/*
DrawnPoint.java
Leo Lei & Harry Wei
This class is for the drawn points animation when an enemy dies, an item is picked up, or when enemies fall
with the layer.
 */

import java.awt.*;
import javax.swing.ImageIcon;
import java.util.Random;

class DrawnPoint {
    private int x, y, val, cooldown;
    private Image pic;
    public static final int COOLDOWN = 50;
    //xx, yy are position, v is the value it will display
    public DrawnPoint(int xx, int yy, int v) {
        Random rand = new Random();
        x = xx + rand.nextInt(20) - 10;
        y = yy + rand.nextInt(20) - 10;
        val = v;
        cooldown = COOLDOWN;
        pic = new ImageIcon("pics/score/" + val + ".png").getImage();
    }

    //each point has a cooldown. when cooldown is less than 0, it stops being displayed in the panel class
    public void reduceCD() {
        cooldown--;
    }

    public int getCD() {
        return cooldown;
    }

    public void draw(Graphics g) {
        reduceCD();
        g.drawImage(pic, x, y, null);
    }

}
