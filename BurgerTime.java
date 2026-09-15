// BurgerTime.java
// Harry Wei && Leo Lei
// Recreation of classic burger time game.
// This version does not use any mouse input to give it an old arcade feel,
// where everything is controlled with buttons

import javax.swing.*;

public class BurgerTime extends JFrame {
    BurgerTimePanel game = new BurgerTimePanel();

    public BurgerTime() {
        super("Burger Time");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(game);
        pack();
        setVisible(true);
    }

    public static void main(String[] arguments) {
        new BurgerTime();
    }
}