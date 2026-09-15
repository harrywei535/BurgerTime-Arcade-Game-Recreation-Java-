// Cheese.java
// Harry Wei && Leo Lei
// class for each cheese layer in the burger time game

import java.awt.image.BufferedImage;

class Cheese extends Layer {

    public Cheese(int x, int y, BufferedImage im) {
        super(x, y, im);
        Layerpic = loadBuffImage("pics/BurgerLayers/Cheese.png"); // sets an individual image for cheese
    }
}