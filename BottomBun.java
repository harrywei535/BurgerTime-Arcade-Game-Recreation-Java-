// BottomBun.java
// Harry Wei && Leo Lei
// class for each bottom bun layer in the burger time game

import java.awt.image.BufferedImage;

class BottomBun extends Layer {

    public BottomBun(int x, int y, BufferedImage im) {
        super(x, y, im);
        Layerpic = loadBuffImage("pics/BurgerLayers/BottomBun.png"); // sets image of the layer
    }
}