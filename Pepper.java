/*
Leo Lei and Harry Wei
Pepper.java
This class is for the peppers that Peter Pepper can use to stun
Enemies. It is an animation of the peppers with a specific location and direction,
 */

import java.awt.*;
import javax.swing.ImageIcon;

class Pepper {
    public static final int DOWN = 0, LEFT = 1, UP = 2, RIGHT = 3;
    private int x, y, direction, frame;
    private Image[][] pics;

    //constructed on direction and position
    public Pepper(int xx, int yy, int d) {
        x = xx;
        y = yy;
        direction = d;
        if (d == DOWN) {
            x -= 32;
        } else if (d == LEFT) {
            x -= 96;
            y -= 64;
        } else if (d == UP) {
            x -= 32;
            y -= 128;
        } else {
            x += 32;
            y -= 64;
        }
        loadPics();
    }

    //getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getFrame() {
        return frame;
    }

    //loads images
    public void loadPics() {
        pics = new Image[4][4];
        frame = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                pics[i][j] = new ImageIcon("pics/player/peppers/peppers" + i + j + ".png").getImage();
            }
        }
    }

    //draws pepper. The panel will remove the pepper after it hits a certain frame
    public void draw(Graphics g) {
        frame = (frame + 1) % 4;
        g.drawImage(pics[direction][frame], x, y, null);
    }

}