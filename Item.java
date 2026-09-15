// Item.java
// Leo Lei && Harry Wei
// class for the bonus items (fries, coffee, icecream)
// that give the player peppers and score

import java.awt.*;
import javax.swing.ImageIcon;

class Item {
    private int x, y, timer, num;//num is used to distinguish from other items.
    // Otherwise, the item would just keep spawning over and over until another layer is dropped
    private Image pic;//picture varies depending on level
    public static final int LASTS = 200;
    //getters
    public int getX(){return x;}
    public int getY(){return y;}
    public int getTimer(){return timer;}
    public Item(int xx, int yy, int level, int n) {
        x = xx;
        y = yy;
        timer = LASTS; // time the item lasts
        num = n; // number given to it to distinguish itself

        if (level % 3 == 0) {
            pic = new ImageIcon("pics/items/fries.png").getImage();
        } else if (level % 3 == 1) {
            pic = new ImageIcon("pics/items/icecream.png").getImage();
        } else {
            pic = new ImageIcon("pics/items/coffee.png").getImage();
        }
    }

    // same as overlaps in Layer class
    public boolean collides(Player player) {
        int x1 = x;
        int y1 = y;
        int x2 = x + 64;
        int y2 = y + 64;
        int px1 = player.getX() - 32;
        int py1 = player.getY() - 64;
        int px2 = player.getX() + 32;
        int py2 = player.getY();
        if (x2 >= px1 && x1 <= px2 && y2 >= py1 && y1 <= py2) {
            return true;
        }
        return false;
    }

    public int getNum() {
        return num;
    }

    // counts down the timer
    public void countDown() {
        timer--;
        if (timer < 0) {
            offScreen();
        }
    }

    // checks if item is on screen
    public boolean onScreen() {
        if (x <= 832 && x >= 0 && y <= 850 && y >= 0) {
            return true;
        }
        return false;
    }

    // puts the item off the screen
    public void offScreen() {
        x = 1000;
        y = 1000;
    }

    public void draw(Graphics g) {
        g.drawImage(pic, x, y, null);
    }

}