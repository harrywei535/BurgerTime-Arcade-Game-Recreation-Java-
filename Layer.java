/*
Layer.java
Leo Lei & Harry Wei
This is a super class for the burger layers(lettuce, patties, buns, Tomato, cheese. ).
Nothing is special between the layers, just the way that the layer is drawn.
Other than that, they all act the same, i.e:
- Layers fall when Player steps on it
- can crush enemies
- will fall further depending on the number of enemies on the layer(+2 per enemy).
*/
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

class Layer {
    // burger sound effects. there's many of the same sound effect for each part of
    // the burger so
    // it's more consistent when playing
    protected static final SoundEffect SOUNDEFFECT[] = { new SoundEffect("Sounds/burgerdepressed.wav"),
            new SoundEffect("Sounds/burgerdepressed.wav"), new SoundEffect("Sounds/burgerdepressed.wav"),
            new SoundEffect("Sounds/burgerdepressed.wav"),
            new SoundEffect("Sounds/burgerflop.wav") };
    public static final int DEPRESSED1 = 0, DEPRESSED2 = 1, DEPRESSED3 = 2, DEPRESSED4 = 3, FLOP = 4;

    protected int x, y;
    protected BufferedImage Layerpic;
    //burger is split into 4 parts that the Player can depress
    protected boolean[] falls = new boolean[4];// checks if each part has fallen
    protected int[] fallOrder = new int[4];// orders of the parts that have fallen. starts off as all null
    protected BufferedImage mask;// y values of the platforms that the layer can be on
    protected boolean atDestination = false;
    // flop counter counts the frames during both flop animations.
    // Fall count counts the number of times the layer has fallen and keeps falling
    // due to there being an enemy on it
    protected int flopCounter, enemyOn;
    protected int fallCount;
    protected boolean flop, flopBump; // flop and flopbump are used to determine when the flop or flop bump
                                      // animations should be played. enemy on is when there is an enemy on it
                                      // when it falls

    //buffered image is how it's drawn
    public Layer(int xx, int yy, BufferedImage im) {
        x = xx;
        y = yy;
        Arrays.fill(falls, false);
        mask = im;
        fallCount = 0;
        flopCounter = 0;
        flop = false;
        flopBump = false;
        enemyOn = 0;
    }

    // getter , setter methods
    public int getWidth() {
        return Layerpic.getWidth();
    }

    public int getHeight() {
        return Layerpic.getHeight();
    }

    public BufferedImage getLayerpic() {
        return Layerpic;
    }

    public boolean isAtDestination() {
        return atDestination;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getEnemiesOn() {
        return enemyOn;
    }

    public void setY(int yy) {
        y = yy;
    }

    //starts the flop animation
    public void setFlop(boolean b) {
        flop = b;
        SOUNDEFFECT[FLOP].play();
    }

    //starts the flopBump animation(flops, and bumps another layer)
    public void setFlopBump(boolean b) {
        flopBump = b;
        SOUNDEFFECT[FLOP].play();
    }

    //adds one to the number of platforms it has fallen through
    public void addFallCount() {
        fallCount++;
    }


    public int getFallCount() {
        return fallCount;
    }

    public void addEnemyOn() {
        enemyOn++;
    }

    public void setAtDestination() {
        if (!atDestination) {
            atDestination = true;
        }
    }

    //if flopping or not
    public boolean getFlop() {
        return flop;
    }

    public boolean getFlopBump() {
        return flopBump;
    }


    public void resetFallCount() {
        fallCount = 0;
        enemyOn = 0;
    }

    // checks if the layer will have fallen. happens when all the parts are
    // depressed
    protected boolean isFallen(boolean[] falls) {
        for (boolean b : falls) {
            if (b == false) {
                return false;
            }
        }
        return true;
    }

    //tells if layer is falling
    public boolean isFalling() {
        return isFallen(falls);
    }

    // sets the layer to falling or not
    public void setisFallen(boolean bool) {
        Arrays.fill(falls, bool);
    }

    //loads layer image
    protected BufferedImage loadBuffImage(String name) {
        try {
            return ImageIO.read(new File(name));
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    //resets sounds that aren't playing
    public void resetSoundEffects() {
        for (SoundEffect s : SOUNDEFFECT) {
            if (!s.isPlaying()) {
                s.reset();
            }
        }
    }

    // method for move. pt is the point that needs to be checked.
    // it moves the part of the layer down. if all of them are down(stepped on by
    // the player)
    // then it falls to the next layer
    // the depth of the layer depression depends on the order of it being depressed
    public void move(Point pt) {
        // split it into 4 rectangles, move them down then move whole thing
        // 4 sections are each quarter of the layer's width
        int width = Layerpic.getWidth(null);
        // checks if x is between the lines and at the right height
        if (x < pt.x && pt.x < x + width && pt.y < y + 24 && pt.y > y) {
            if (pt.x < x + width / 4) {
                if (falls[0] == false) {// plays the depressed sound
                    SOUNDEFFECT[DEPRESSED1].play();
                }
                falls[0] = true;

                if (fallOrder[0] == 0) {
                    fallOrder[0] = 1;
                    changeFallOrder();
                }
            } else if (pt.x < x + width / 2) {
                if (falls[1] == false) {
                    SOUNDEFFECT[DEPRESSED2].play();
                }
                falls[1] = true;
                // once the order is set, it cannot change, until the layer falls to rest on another platform
                if (fallOrder[1] == 0) {
                    fallOrder[1] = 1;
                    changeFallOrder();
                }
            } else if (pt.x < x + width / 2 + width / 4) {
                if (falls[2] == false) {
                    SOUNDEFFECT[DEPRESSED3].play();
                }
                falls[2] = true;
                if (fallOrder[2] == 0) {
                    fallOrder[2] = 1;
                    changeFallOrder();
                }
            } else {
                if (falls[3] == false) {
                    SOUNDEFFECT[DEPRESSED4].play();
                }
                falls[3] = true;
                if (fallOrder[3] == 0) {
                    fallOrder[3] = 1;
                    changeFallOrder();
                }
            }
            for (SoundEffect s : SOUNDEFFECT) {
                if (!s.isPlaying()) {
                    s.reset();
                }
            }
        }
    }

    // adds 1 to every part that has already been stepped on, to get the effect
    public void changeFallOrder() {
        for (int i = 0; i < 4; i++) {
            if (fallOrder[i] != 0) {
                fallOrder[i]++;
            }
        }
    }

    // gets the distance to the next platform
    public int distanceToPlat() {
        int d = 0; // distance to the platform in pixels
        int height = Layerpic.getHeight();
        int width = Layerpic.getWidth();
        while (clear(x + width / 2, y + height + d)) {
            d++;
        }
        return d + 8; // add 8 since platform is 8 pixels thick
    }

    // checks if the layer is on the white platform
    public boolean isOnWhitePlatform() {
        int white = 0xFFFFFFFF;
        int c = mask.getRGB(x + getWidth() / 2, y + getHeight());
        return c == white;
    }

    // when the layer is falling
    public void fall(BurgerTimePanel bPanel) {
        // only fall if all parts are depressed
        int height = Layerpic.getHeight();// some layers are shorter than others
        int width = Layerpic.getWidth();
        if (isFallen(falls)) {
            Arrays.fill(fallOrder, 0);// resets fall order
            if (distanceToPlat() - 8 > 10) { // when distance is more than 10, it will move 10
                y += 10;
            } else if (clear(x + width / 2, y + height)) { // when the distance to the platform is less than 10
                for (int i = 1; i <= 9; i++) {
                    // checks if it is not clear, meaning it hit a platform
                    if (!clear(x + width / 2, y + height + i + 1)) {
                        SOUNDEFFECT[FLOP].play();
                        y = y + i + 1; // moves the layer on the platform
                        if (isOnWhitePlatform()) {
                            setAtDestination();
                        } else {
                            // when the layer is not on the white platform, the y must be adjusted due to
                            // the platform thickness
                            y = y + 8;
                        }
                        bPanel.addScore(50);
                        flop = true; // flop animation
                        Arrays.fill(falls, false);// stops the fall
                        break;
                    }
                }
            }
        }
    }

    // checks if two layers overlap (collide). copied from the box example
    // from the OOP lesson
    public boolean Overlaps(Layer layer) {
        int width = Layerpic.getWidth();
        int height = Layerpic.getHeight();
        int Layerwidth = layer.getLayerpic().getWidth();
        int Layerheight = layer.getLayerpic().getHeight();
        return x + width >= layer.getX() && x <= layer.getX() + Layerwidth &&
                y + height >= layer.getY() && y <= layer.getY() + Layerheight;
    }

    // adjusts height
    public void AdjustY(int amount) {
        y += amount;
    }

    // checks if the layer is supposed to fall on a platform
    private boolean clear(int x, int y) {
        int platform1 = 0xFF0000FF;// platforms are BLUE
        int platform2 = 0xFFFFFFFF;// bottom platform is WHITE
        if (x < 0 || x >= mask.getWidth(null) || y < 0 || y >= mask.getHeight(null)) {
            return false;
        }
        int c = mask.getRGB(x, y);

        // checks if it is BLUE
        if (c != platform1) {
            // then checks if it is WHITE
            return c != platform2;
        }
        return false;
    }

    // Method that animates the flop of the layers when they fall onto a platform
    private void flop(Graphics g) {
        if (flopCounter == 0) { // moves the layer down on the first frame
            y += 12;
        } else if (flopCounter <= 3) { // moves the layer back to the original position
            y -= 4;
        }
        flopCounter++;
        int part = Layerpic.getWidth() / 4;
        int height = Layerpic.getHeight();
        for (int i = 0; i < 4; i++) {
            BufferedImage subimage = Layerpic.getSubimage(i * part, 0, part, height);
            if (flopCounter <= 3) { // first part of the animation
                //middle is lower than the sides
                if (i == 1 || i == 2) {
                    g.drawImage(subimage, x + (i * part), y + 5, null);
                } else {
                    g.drawImage(subimage, x + (i * part), y + 3, null);
                }
            } else if (flopCounter <= 6) { // second part of animation
                //sides are lower than the middle
                if (i == 1 || i == 2) {
                    g.drawImage(subimage, x + (i * part), y, null);
                } else {
                    g.drawImage(subimage, x + (i * part), y + 3, null);
                }
            } else { // when the animation is finished
                g.drawImage(subimage, x + (i * part), y, null);
                flop = false; // resets everything
                flopCounter = 0;
                if (enemyOn > 0) {
                    addFallCount(); // adds to fall count if there is an enemy on
                }
            }
        }
    }

    // method that animates the layer when bumping another layer
    private void flopBump(Graphics g) {
        if (flopCounter < 7) {
            y -= 4;
        } else if (flopCounter <= 17 && flopCounter > 10) {
            y += 4;
        }
        flopCounter++;
        int part = Layerpic.getWidth() / 4;
        int height = Layerpic.getHeight();
        for (int i = 0; i < 4; i++) {
            BufferedImage subimage = Layerpic.getSubimage(i * part, 0, part, height);
            // side parts are higher
            if (flopCounter <= 7) {
                if (i == 1 || i == 2) {
                    g.drawImage(subimage, x + (i * part), y, null);
                } else {
                    g.drawImage(subimage, x + (i * part), y - 5, null);
                }
            } else if (flopCounter <= 17) {
                // middle parts are higher
                if (i == 1 || i == 2) {
                    g.drawImage(subimage, x + (i * part), y - 7, null);
                } else {
                    g.drawImage(subimage, x + (i * part), y - 5, null);
                }
            } else {
                g.drawImage(subimage, x + (i * part), y, null);
                flopBump = false;
                flopCounter = 0;
                if (enemyOn > 0) {
                    addFallCount();
                }
            }
        }
    }

    //draws the Layer
    public void draw(Graphics g) {
        int part = Layerpic.getWidth(null) / 4;
        int height = Layerpic.getHeight(null);
        //flop animations
        if (flopBump) {
            flopBump(g);
        } else if (flop) {
            flop(g);
        } else {
            //stationary
            for (int i = 0; i < 4; i++) {
                BufferedImage subimage = Layerpic.getSubimage(i * part, 0, part, height);
                if (falls[i]) {
                    // depth depends on the order of the part
                    g.drawImage(subimage, x + (i * part), y + (fallOrder[i]) * 5, null);
                } else {
                    g.drawImage(subimage, x + (i * part), y, null);
                }
            }
        }
    }
}