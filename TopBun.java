// TopBun.java
// Harry Wei && Leo Lei
// class for each top bun layer in the burger time game

import java.awt.image.BufferedImage;

class TopBun extends Layer {

    public TopBun(int x, int y, BufferedImage im) {
        super(x, y, im);
        Layerpic = loadBuffImage("pics/BurgerLayers/TopBun.png");
    }
}