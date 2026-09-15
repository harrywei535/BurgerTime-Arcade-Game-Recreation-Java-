// Patty.java
// Harry Wei && Leo Lei
// class for each patty layer in the burger time game

import java.awt.image.BufferedImage;

class Patty extends Layer {

    public Patty(int x, int y, BufferedImage im) {
        super(x, y, im);
        Layerpic = loadBuffImage("pics/BurgerLayers/Patty.png"); // sets individual image
    }
}
