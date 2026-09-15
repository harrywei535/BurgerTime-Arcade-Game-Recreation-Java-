// Lettuce.java
// Harry Wei && Leo Lei
// class for each lettuce layer in the burger time game
//nothing special,
import java.awt.image.BufferedImage;

class Lettuce extends Layer {

    public Lettuce(int x, int y, BufferedImage im) {
        super(x, y, im);
        Layerpic = loadBuffImage("pics/BurgerLayers/Lettuce.png"); // sets lettuce's individual picture
    }
}