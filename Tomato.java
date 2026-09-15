// Tomato.java
// Harry Wei && Leo Lei
// class for each tomato layer in the burger time game

import java.awt.image.BufferedImage;

class Tomato extends Layer {

    public Tomato(int x, int y, BufferedImage im) {
        super(x, y, im);
        Layerpic = loadBuffImage("pics/BurgerLayers/Tomato.png");
    }
}