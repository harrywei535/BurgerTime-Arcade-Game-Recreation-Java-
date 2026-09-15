import java.awt.*;
import java.awt.event.*;
import javax.swing.ImageIcon;

/*
Leo Lei and Harry Wei
This class is for the nameselect screen. An object of this class is the name that the player
selects, and the box that's drawn around the letters. Only meant to be drawn in the nameselect class.
 */
class NameSelector {
    //positions of the functions                                    //positions for DELETE, SAVE, and COMMA
    private final static int WAIT = 6, DISPLAYWAIT = 25, SPACE = 13, DELETE = 173, SAVE = 628, COMMA = 573;
    public final static String[][] ALPHABET = { { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" },
                                                { "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T" },
                                                { "U", "V", "W", "X", "Y", "Z", "-", ",", "?", "!" } };
    private int x, y, arrowCD, displayFrame, enterCD;
    private Image pic, bigPic;//images that could be drawn
    private String name;//burgertimepanel also has a name, however this one can change, only when "SAVE"
    //is pressed, then name in burgertimepanel changes
    private boolean enterKey;//state of the enter key. used to register input

    //loads images, and sets position
    public NameSelector(int xx, int yy) {
        x = xx;
        y = yy;
        arrowCD = 0;
        displayFrame = 0;
        enterCD = 0;
        name = "";
        pic = new ImageIcon("pics/nameSelector.png").getImage();
        bigPic = new ImageIcon("pics/bigNameSelector.png").getImage();
    }

    //getter
    public String getName() {
        return name;
    }

    //adjusts the X position to put the big box around the DELETE, SPACE and SAVE buttons
    public void adjustX() {
        if ((x + 22 - 35) / 80 < 2) {
            x = SPACE;
        } else if (((x + 22 - 35) / 80) < 6) {
            x = DELETE;
        } else{
            x = SAVE;
        }

    }

    //moves the selecting box based on the key's input
    public void move(boolean[] keys, BurgerTimePanel bPanel) {
        enterKey = keys[KeyEvent.VK_ENTER];
        //cooldown to switch letters when arrow keys are pressed
        if (keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_LEFT] ||
                keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_DOWN]) {
            arrowCD++;
        }
        //if an arrow key is not pressed, there is no cooldown to switch letters
        else {
            arrowCD = 0;
        }
        //always an enterCD
        enterCD++;
        if (arrowCD > WAIT || arrowCD == 0) {
            //moves based on arrow input if position is on the keyboard
            if (y < 260 + 450) {
                if (keys[KeyEvent.VK_RIGHT] && x < 733) {
                    x += 80;
                } else if (keys[KeyEvent.VK_LEFT] && x > 13) {
                    x -= 80;
                } else if (keys[KeyEvent.VK_UP] && y > 20 + 450) {
                    y -= 80;

                } else if (keys[KeyEvent.VK_DOWN] && y < 260 + 450) {
                    if (y + 80 >= 260 + 450) {
                        adjustX();
                    }
                    y += 80;
                }
            } else {
                if (keys[KeyEvent.VK_UP]) {
                    if (x > 173) {
                        x = COMMA;
                    }
                    y -= 80;
                } else if (keys[KeyEvent.VK_LEFT] && x > 13 && x < 200) {
                    x -= 160;
                } else if (keys[KeyEvent.VK_RIGHT] && x < 173) {
                    x += 160;
                } else if (keys[KeyEvent.VK_RIGHT] && x < 628) {
                    x = SAVE;
                } else if (keys[KeyEvent.VK_LEFT]) {
                    x = DELETE;
                }
            }
            arrowCD = 0;
        }
        // adds letter when enter is pressed
        if (enterCD > WAIT) {
            if (keys[KeyEvent.VK_ENTER]) {
                if (y >= 260 + 450) {

                    if (x < SPACE + 1 && name.length() < 8) {
                        name += " ";
                    } else if (x < DELETE + 1 && name.length() > 0) {
                        name = name.substring(0, name.length() - 1);
                    } else {
                        bPanel.setName(name);
                    }
                } else if (name.length() < 8) {
                    int i = ((y + 60 - 80 - 450) / 80);
                    int j = ((x + 22 - 35) / 80);
                    name += ALPHABET[i][j];
                }

            }
            enterCD = 0;
        }
    }


    //draws the score in the nameselect screen
    public void drawScore(Graphics g, int pscore, int y){
        g.setColor(Color.RED);//score is RED
        Graphics2D g2d = (Graphics2D) g;
        FontMetrics metrics = g2d.getFontMetrics();
        String place = y+1+"";//place is y+1
        if(Integer.parseInt(place) >5){
            place = "?";
        }
        //blincking animation for the next character in the name
        if(displayFrame %40<25){
            g.drawString(place +". " +getName()+"_", 100, y*45+150);
        }else{
            g.drawString(place +". " +getName(), 100, y*45+150);
        }

        String score = "" + pscore;
        int width = metrics.stringWidth(score);
        //draws the score
        g.drawString( score+" pts", 715 - width, y*45+150);
    }


    //draw method for nameselect. draws the big box that moves around telling the user
    //which letter is selected
    public void draw(Graphics g, boolean[] keys) {
        g.setColor(Color.WHITE);
        displayFrame++;
        //displayframe% DISPLAYWAIT is for the blinking animation.
        //If the box is being moved the blinking animation stops
        if (displayFrame % DISPLAYWAIT < 6 * DISPLAYWAIT / 8 || enterKey ||
                keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_DOWN]) {
            if (y < 260 + 450) {
                g.drawImage(pic, x, y, 80, 80, null);
            } else if (x <= 173) {
                g.drawImage(bigPic, x, y, 165, 90, null);
            } else {
                g.drawImage(bigPic, x, y, 200, 90, null);
            }
        }

    }
}