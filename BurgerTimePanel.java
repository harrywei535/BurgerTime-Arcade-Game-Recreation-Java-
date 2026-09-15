/*
Leo Lei & Harry Wei
BurgerTimePanel.java
This class controls the movement and drawing of *everything* on the screen.
It will take keyboard input, and the player will control chef Peter Pepper with the arrows
and pepper with the space bar. This class will also play the music and soundeffects,
when various things happen on the screen. Also displays the nameselect, intro and end screens.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;

class BurgerTimePanel extends JPanel implements KeyListener, ActionListener{
    //player spawn points for each level.
    public static final Point[] PLAYERSPAWN = { new Point(416, 678), new Point(416, 742), new Point(416, 806),
            new Point(416, 550), new Point(608, 806), new Point(416, 678) };
    //item spawn points for each level
    public static final Point[] ITEMSPAWN = { new Point(385, 238), new Point(385, 366), new Point(385, 366),
            new Point(385, 38), new Point(577, 174), new Point(385, 366) };
    //number of layers that need to fall for an item to spawn
    public static final int[][] LAYERNUMBERS = { { 4, 8, 12 }, { 5, 7, 13 }, { 4, 8, 14 }, { 6, 16, 22, 28 },
            { 3, 6, 9 }, { 3, 7, 12 } };
    public static final int INTRO = 0, GAME = 1, END = 2, NAMESELECT = 3, ENTERCD = 30;
    private int entercooldown;// cooldown for enterkey


    //sound relating to the state of the game(player dies, new level, etc.)     // start sound
    public static final Sound[] SOUNDS = { new Sound("Sounds/Start.wav"),
            // dying sound                              //win sound
            new Sound("Sounds/dies.wav"), new Sound("Sounds/Win.wav"), };
    //magic numbers controlling which Sound to play
    public static final int START = 0, DIE = 1, WIN = 2, NONE = 3;
    public static final Music BACKGROUND = new Music("Sounds/Theme.mid");// background music
    // from https://strategywiki.org/wiki/File:Burgertime_toc_splash.jpg


    //a burgertime picture of enemies chasing the chef. from https://strategywiki.org/wiki/BurgerTime
    public static final Image CHARACTERS = new ImageIcon("pics/Burgerchase.png").getImage();

    //different sound effects related to the panel
    public static final SoundEffect[] SOUNDEFFECTS = { new SoundEffect("Sounds/ItemAppear.wav"),
            new SoundEffect("Sounds/Bonus.wav"),
            new SoundEffect("Sounds/enemysquish.wav") };
    //magic numbers to play the sound effects
    public static final int ITEM = 0, BONUS = 1, SQUISH = 2;
    private int currentSnd;//current sound to be played
    private int screen = INTRO;
    private int playerScore, tenThousands;//score, ten thousands to know when to add lives
    private boolean[] keys;
    Timer timer;
    private int level, totLevel;//totlevel is the number of levels beaten, level is the stage
    private int difficulty = 1;//goes up to 9, increases enemy speed
    private Player player;
    private Image back, levelTen, levelFive, levelOne;//number of levels beaten
    private BufferedImage mask;//mask for the movement of enemies and player
    private ArrayList<Layer> BurgerLayers;//layers
    private ArrayList<Enemy> Enemies;//enemies
    private Point[] respawnPoints;//enemy respawn points
    private ArrayList<DrawnPoint> drawnscores;// where to draw the scores
    private ArrayList<Pepper> peppers;//player peppers
    private ArrayList<Score> high;//high scores
    private Item bonusItem;
    private NameSelector nameSelect;//used to get player's name
    private String name;//player's name for high score

    public BurgerTimePanel() {
        entercooldown = 0;
        level = 1;
        totLevel = 1;
        keys = new boolean[KeyEvent.KEY_LAST + 1];
        high = loadScores();
        player = new Player(416, 678, mask, this);
        //initialize everything for the game
        loadBackground(level);
        loadBurgerLayers(level);
        loadEnemies(level);
        resetPlayer();//puts player in right spot
        peppers = new ArrayList<>();
        drawnscores = new ArrayList<>();
        bonusItem = new Item(1000, 1000, level, -1);
        playerScore = 0;
        tenThousands = 1;
        nameSelect = new NameSelector(13, 470);
        currentSnd = START;
        setPreferredSize(new Dimension(831, 850));
        setFocusable(true);
        requestFocus();
        addKeyListener(this);
        timer = new Timer(20, this);
        timer.start();
        BACKGROUND.LoopContinuously();//background is looped forever
    }

    // method to reset the game after each level change
    public void resetGame() {
        loadBackground(level);
        loadBurgerLayers(level);
        loadEnemies(level);
        resetPlayer();
        changeSound(START);
    }

    // method to reset the game completely after player loses game
    public void completeReset() {
        player = new Player(416, 678, mask, this);
        level = 1;
        totLevel = 1;
        playerScore = 0;
        tenThousands = 1;
        nameSelect = new NameSelector(13, 20 + 450);
        currentSnd = START;
        bonusItem = new Item(1000, 1000, level, -1);
        resetGame();
    }

    // method to reset the player's location after each level
    public void resetPlayer() {
        Point respawn = PLAYERSPAWN[level - 1];
        player.setX((int) respawn.getX());
        player.setY((int) respawn.getY());
        player.setMask(mask);
    }

    // method to load Enemies based on the level
    public void loadEnemies(int level) {
        Enemies = new ArrayList<>();
        respawnPoints = new Point[4]; // spawn points for each level
        Scanner enemyReader = null;
        try {
            enemyReader = new Scanner(new File("maps/enemies" + level + ".txt"));
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
        String en;
        int index = 0; // used to put spawn points into the array

        while (enemyReader.hasNextLine()) {
            en = enemyReader.nextLine();
            // each line in the form of (x y enemytype) seperated by one space
            String[] vals = en.split(" ");

            // adds spawn points into array
            if (index < respawnPoints.length) {
                respawnPoints[index] = new Point(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]));
                index++;
            }

            // adds all enemies into the arrayList
            if (vals[2].equals("sausage")) {
                Enemies.add(new Sausage(Integer.parseInt(vals[0]), Integer.parseInt(vals[1])));
            } else if (vals[2].equals("egg")) {
                Enemies.add(new Egg(Integer.parseInt(vals[0]), Integer.parseInt(vals[1])));
            } else if (vals[2].equals("pickle")) {
                Enemies.add(new Pickle(Integer.parseInt(vals[0]), Integer.parseInt(vals[1])));
            }
        }

        // when the player goes past level 6, the game resets to level 1, but there will
        // be different enemies
        if (totLevel > 6) {
            Enemies = new ArrayList<>();
            for (int i = 1; i < 7; i++) {
                Point p = respawnPoints[(i - 1) % 4];
                if (i % 3 == 0) {
                    Enemies.add(new Sausage((int) p.getX(), (int) p.getY()));
                } else if (i % 3 == 1) {
                    Enemies.add(new Pickle((int) p.getX(), (int) p.getY()));
                } else if (i % 3 == 2) {
                    Enemies.add(new Egg((int) p.getX(), (int) p.getY()));
                }
            }
        }

        enemyReader.close();
        delaySpawns(Enemies); // makes the enemies spawn in a staggered manner
        setDifficulty();
    }

    // method to load background and mask
    public void loadBackground(int level) {
        back = new ImageIcon("pics/map" + level + "/map.png").getImage();
        try {
            mask = ImageIO.read(new File("pics/map" + level + "/mask.png"));
        } catch (IOException e) {
            System.out.println(e);
        }
        // images for the level indicators in the bottom right corner
        levelTen = new ImageIcon("pics/levels/levelTen.png").getImage();
        levelFive = new ImageIcon("pics/levels/levelFive.png").getImage();
        levelOne = new ImageIcon("pics/levels/levelOne.png").getImage();
    }

    // method to load all burgerlayers for each level
    public void loadBurgerLayers(int level) {
        BurgerLayers = new ArrayList<>();
        Scanner mapLayers = null;
        try {
            mapLayers = new Scanner(new File("maps/map" + level + ".txt"));
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
        String layer;
        while (mapLayers.hasNextLine()) {
            layer = mapLayers.nextLine();
            // each line in the form of (x y layertype) separated by one space
            String[] vals = layer.split(" ");
            if (vals[2].equals("TopBun")) {
                BurgerLayers.add(new TopBun(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), mask));
            } else if (vals[2].equals("Lettuce")) {
                BurgerLayers.add(new Lettuce(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), mask));
            } else if (vals[2].equals("Patty")) {
                BurgerLayers.add(new Patty(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), mask));
            } else if (vals[2].equals("BottomBun")) {
                BurgerLayers.add(new BottomBun(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), mask));
            } else if (vals[2].equals("Tomato")) {
                BurgerLayers.add(new Tomato(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), mask));
            } else if (vals[2].equals("Cheese")) {
                BurgerLayers.add(new Cheese(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), mask));
            }
        }
        mapLayers.close();
    }

    // saves scores
    public void saveScores() {
        try {
            PrintWriter out = new PrintWriter(new File("util/scores.txt"));
            for (Score sc : high) {
                out.println(sc.twoLine());
            }
            out.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    // loads scores
    public ArrayList<Score> loadScores() {
        ArrayList<Score> tmp = new ArrayList<Score>();
        try {
            Scanner input = new Scanner(new File("util/scores.txt"));
            for (int i = 0; i < 5; i++) {// loads top 5 scores
                String name = input.nextLine();
                int points = Integer.parseInt(input.nextLine());
                tmp.add(new Score(name, points));
            }
            input.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return tmp;
    }

    // load font method from Mr.Mckenzie's example
    public Font loadFont(String name, int size) {
        Font font = null;
        try {
            File fntFile = new File(name);
            font = Font.createFont(Font.TRUETYPE_FONT, fntFile).deriveFont((float) size);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (FontFormatException ex) {
            System.out.println(ex);
        }
        return font;
    }

    // changes the current sound that should be played
    private void changeSound(int snd) {
        //resets all the sounds if there is none playing
        if (snd == NONE) {
            for (Sound s : SOUNDS) {
                s.stop();
                s.startFromBeginning();
            }
        }
        //sets desired sound, resets the rest
        for (int i = 0; i < SOUNDS.length; i++) {
            if (i == snd) {
                currentSnd = snd;
            } else {
                SOUNDS[i].stop();
                SOUNDS[i].startFromBeginning();
            }
        }
    }

    //stops all Sounds
    private void stopSounds() {
        for (Sound s : SOUNDS) {
            s.stop();
        }
    }

    // method to reset all sound effects
    private void resetSoundEffects() {
        for (SoundEffect s : SOUNDEFFECTS) {
            if (!s.isPlaying()) {
                s.reset();
            }
        }
    }

    //resets the background music. stops it and reset's it to the beginning.
    //happens when a Sound in SOUNDS plays. (player dies, wins, etc.)
    private void resetBackground() {
        BACKGROUND.stop();
        BACKGROUND.startAtBeginning();
    }

    // move method for the game
    public void move() {
        if (screen == GAME) {
            //BACKGROUND and a sound never play at the same time
            if (!BACKGROUND.isPlaying()) {
                SOUNDS[currentSnd].play();
            }
            resetSoundEffects();
            if (player.getWin()) {
                resetBackground();
                changeSound(WIN);
            } else if (player.getDead()) {
                resetBackground();
                changeSound(DIE);
            } else if (!SOUNDS[START].isPlaying()) {
                changeSound(NONE);
                BACKGROUND.play();
            }

            if (player.getLives() <= 0) { // when player dies
                screen = NAMESELECT;
            }
            player.move(keys); // moves player

            // checks if the player can pepper
            if (!player.getWin() && !player.getDead() && keys[KeyEvent.VK_SPACE] && player.getPepperCD() < 0
                    && player.getPeppers() > 0) {
                // adds a new pepper
                peppers.add(new Pepper(player.getX(), player.getY(), player.getDirection()));
            }

            removePeppers(); // removes all old peppers on the screen
            removePointVals(); // removes all the point values on screen
            addLife(); // adds a life and changes the difficulty every 10000 points

            for (Enemy e : Enemies) {
                if (e.delaySpawn) { // happens at the start of a levels to stagger enemy spawns
                    e.spawn();
                } else if (!e.getFall()) {
                    // checks collisions between player and enemy
                    if (e.collides(player) && !e.isPeppered()) {
                        player.die();
                        bonusItem.offScreen();
                    }

                    if (!player.getWin() && !player.getDead()) {
                        e.move(player, mask); // moves enemy

                        // checks collisions between layers, or if it has been dropped
                        for (Layer lay : BurgerLayers) {
                            if (lay.isFalling()) { // checks if layer is falling
                                if (e.collides(lay, this)) {
                                    SOUNDEFFECTS[SQUISH].play();
                                    drawnscores.add(new DrawnPoint(e.x - 25, e.y - 10, e.pointValue()));
                                    e.setRespawnCD(countDead(Enemies));
                                }
                                // if e.on() returns true, it will set a fall variable in enemy to true
                                // this variable freezes the enemy so it doesn't move.
                                if (e.on(lay, player)) {
                                    lay.addEnemyOn(); // adds enemy on if there is one on it
                                }
                            }
                        }

                        // checks collisions with peppers
                        for (Pepper p : peppers) {
                            e.collides(p);
                        }

                        // if the enemy is peppered, it will be stunned for a short time
                        if (e.isPeppered()) {
                            e.peppered(); // stuns the enemy for a short time
                        }
                        // when the enemy is squished by a layer and has finished its squished animation
                        if (e.getFrame() == 3 && !e.isAlive()) {
                            e.respawn(respawnPoints); // respawns enemy
                        }
                    }
                } else { // when enemy is falling
                    if (e.moveWithLayer()) { // moves enemy with layer
                        if (e.getMoveWith().isAtDestination()) {// falls to the white platform
                            e.setFallen(true); // makes the animation to climb back onto the platform
                            e.setRespawnCD(2); // makes the slight pause when done falling
                        } else { // dead on platform
                            e.setRespawning(true); // is respawning
                            e.setRespawnCD(2); // makes the slight pause when done falling
                        }
                    }
                    if (!player.getWin() && !player.getDead()) {
                        if (e.getFallen()) { // if its fallen to the white platform
                            e.backToPlatform(mask); // makes the enemy climb back onto the platform
                        } else if (e.isRespawning()) { // if its fallen to a normal platform
                            e.waitRespawn(mask); // when the enemy falls onto a platform, it comes out of the layer
                        }
                    }
                }
            }

            int atDestination = 0; // counts the number of layers that have fallen onto the white platform
            for (Layer lay : BurgerLayers) {
                // checks to depress the layer's
                lay.resetSoundEffects();
                //if the player is moving right, the right foot leads and depresses layers
                //same with left foot
                if (player.getDirection() == Player.RIGHT) {
                    lay.move(player.getRightFoot());
                } else if (player.getDirection() == Player.LEFT) {
                    lay.move(player.getLeftFoot());
                } else {
                    lay.move(new Point(player.getX(), player.getY()));//middle of player
                }
                lay.fall(this); // makes layer fall

                // checks if there are enemies falling on the layer, forcing the layer to fall
                // more based on the number of enemies on it. The layer will fall 2 more
                // platforms for each enemy on it.
                if (lay.getEnemiesOn() > 0 && !lay.isFalling()
                        && lay.getFallCount() < lay.getEnemiesOn() * 2 + 1) {

                    // not flopping, since flop animation needs to be done
                    // not at destination to ensure it doesn't fall when at destination
                    if (!lay.getFlop() && !lay.getFlopBump() && !lay.isAtDestination()) {
                        lay.setisFallen(true); // makes it keep falling
                    }
                }
                // when the layer has enemies on it and has fallen enough times
                else if (lay.getFallCount() >= lay.getEnemiesOn() * 2 + 1) {
                    addDroppedEnemyScore(lay); // adds score based on how many enemies were dropped
                    lay.resetFallCount();
                }

                // when layer is at the white platform at the bottom
                if (lay.isAtDestination()) {
                    addDroppedEnemyScore(lay);
                    lay.resetFallCount();
                    atDestination++;
                }
            }

            spawnItem(atDestination); // spawns item when a certain number of layers have fallen

            if (bonusItem.onScreen()) {
                bonusItem.countDown(); // counts down the timer
                SOUNDEFFECTS[ITEM].play();
                if (bonusItem.collides(player)) { // checks collisions between players
                    addScore(((level - 1) % 3 + 1) * 500); // adds score based on point values of level
                    drawnscores.add(new DrawnPoint(bonusItem.getX(), bonusItem.getY() + 20, ((level - 1) % 3 + 1) * 500));
                    SOUNDEFFECTS[BONUS].play();
                    player.addPepper(); // adds to player's peppers
                    bonusItem.offScreen();
                }
            }

            // when all burger layers are a destination, the player moves on to the next
            // level
            if (atDestination == BurgerLayers.size()) {
                player.win();
            }

            // gets an arrayList of all layers that are colliding in the following order:
            // 1st one is the layer falling
            // 2nd one is the layer being bumped
            ArrayList<Layer> collidedLayers = CollidedLayers(BurgerLayers);
            if (!collidedLayers.isEmpty()) {
                for (int i = 0; i < collidedLayers.size(); i += 2) {
                    Layer[] collided = new Layer[] { collidedLayers.get(i), collidedLayers.get(i + 1) };
                    if (isOnBottomPlatform(collided)) {
                        stack(collided); // makes the layers stack
                    } else {
                        bump(collided); // makes the layer bump the other layer
                    }
                }
            }
        } else if (screen == NAMESELECT) {
            nameSelect.move(keys, this);
        } else if (screen == END) {
            stopSounds();
        }
    }

    // method that removes peppers that have done their animation
    public void removePeppers() {
        ArrayList<Pepper> toRemove = new ArrayList<>();
        for (Pepper p : peppers) {
            if (p.getFrame() == 3) {//peppers end animation at 3
                toRemove.add(p);
            }
        }
        peppers.removeAll(toRemove);
    }

    // method that removes all point values on the screen after a certain time
    public void removePointVals() {
        ArrayList<DrawnPoint> toRemove = new ArrayList<>();
        for (DrawnPoint dp : drawnscores) {
            if (dp.getCD() <= 0) {
                toRemove.add(dp);
            }
        }
        drawnscores.removeAll(toRemove);
    }

    // returns an ArrayList of layers that are collided.
    private ArrayList<Layer> CollidedLayers(ArrayList<Layer> Layers) {
        ArrayList<Layer> collidedLayers = new ArrayList<>();
        //goes through checking each pair of layers and checks if they are overlapping
        for (int i = 0; i < Layers.size(); i++) {
            for (int j = i + 1; j < Layers.size(); j++) {
                if (Layers.get(i).Overlaps(Layers.get(j))) {
                    //layer can't be at destination and has to be falling
                    if (Layers.get(i).isFalling() && !Layers.get(i).isAtDestination()) {
                        collidedLayers.add(Layers.get(i));
                        collidedLayers.add(Layers.get(j));
                    }
                }
            }
        }
        return collidedLayers;
    }

    // bumps one layer to the next platform
    private void bump(Layer[] layers) {
        // layers[0] is the layer falling while layers[1] is being bumped
        if (layers[0].isFalling() && !layers[1].isFalling()) {
            layers[0].setisFallen(false); // stops the first from falling
            layers[1].setisFallen(true); // makes the second layer fall
            layers[0].AdjustY(layers[0].distanceToPlat()); // adjusts first layer onto the platform
            layers[0].setFlopBump(true); // makes it do the flop animation
            addScore(50);
        }
    }

    // stacks the layers one on the other
    private void stack(Layer[] layers) {
        // both of them stop falling
        for (Layer lay : layers) {
            lay.setisFallen(false);
        }
        layers[0].setFlop(true);//flop animation
        addScore(50);
        // readjusts
        if (layers[0].getY() > layers[1].getY()) {
            layers[1].setY(layers[0].getY() - layers[1].getHeight());
        } else if (layers[1].getY() > layers[0].getY()) {
            layers[0].setY(layers[1].getY() - layers[0].getHeight());
        }
    }

    // checks if the layer is on the bottom platform, returns true if it is and
    // false otherwise
    private boolean isOnBottomPlatform(Layer[] layers) {
        boolean on = false;
        if (layers[1].isOnWhitePlatform() || layers[1].isAtDestination()) {
            on = true;
            layers[0].setAtDestination();
        }
        return on;

    }

    // method to change level. goes to the next level
    public void changeLevel() {
        level++;
        totLevel++;
        if (level > 6) {//loops back to level 1 after level 6
            level = 1;
        }
        resetGame();
    }

    // method to reset the game after a player loses a life
    public void playerDied() {
        loadEnemies(level);
        resetPlayer();
    }

    // method to count the number of dead enemies, returns how many are dead
    public int countDead(ArrayList<Enemy> Enemies) {
        int alive = 0;
        for (Enemy e : Enemies) {
            if (e.isAlive()) {
                alive++;
            }
        }
        return Enemies.size() - alive;
    }

    // method to delay the spawn of enemies at the start of each level
    public void delaySpawns(ArrayList<Enemy> Enemies) {
        for (int i = 0; i < Enemies.size(); i++) {
            Enemies.get(i).setSpawnCD(i);
            Enemies.get(i).setNum(i);
        }
    }

    // method to set the new Leaderboard
    public void setNewLeaderBoard() {
        if (playerScore > high.get(4).getScore()) {
            high.set(4, new Score(name, playerScore));
            Collections.sort(high);
        }
    }

    // method to set the name of the player onto the leaderboard.
    public void setName(String s) {
        name = s;
        setNewLeaderBoard();
        saveScores();
        screen = END;
        entercooldown = ENTERCD;
    }

    // method to add a life to the player every 10000 score they have earned and
    // change the difficulty every 10000 score
    private void addLife() {
        if (playerScore > tenThousands * 10000) {
            if (difficulty < 9) {
                difficulty++;
            }
            setDifficulty();
            tenThousands++;
            player.addLife();
        }
    }

    // method to change the speed at which the enemies move, changing the difficulty
    private void setDifficulty() {
        for (Enemy e : Enemies) {
            //vy is the frame rate at which enemies move up and down ladders
            //therefore lower vy is faster
            e.setVY(17 - difficulty);
            e.setVX(7 + difficulty);//vx is stride length of the enemy
        }
    }

    // place of the player's score in the top 5. if score isn't in top 5, returns 6
    public int LeaderboardSpot(ArrayList<Score> scores, int playerScore) {
        for (int i = 0; i < scores.size(); i++) {
            if (playerScore > scores.get(i).getScore()) {
                return i + 1;
            }
        }
        return 6;
    }

    // adds a value to the player's score
    public void addScore(int v) {
        playerScore += v;
    }

    // method that adds a point value to the player's score based on how many
    // enemies were dropped using the same layer
    public void addDroppedEnemyScore(Layer lay) {
        int fallCount = lay.getFallCount();
        int enemyOn = lay.getEnemiesOn();
        int x = lay.getX();
        int y = lay.getY();
        //if layer has fallen enough times or is at destination
        if (fallCount >= enemyOn * 2 || lay.isAtDestination()) {
            //draws the score where enemies die
            if (enemyOn < 3) {
                addScore(500 * enemyOn);
                drawnscores.add(new DrawnPoint(x + lay.getWidth() / 3, y - 20, 500 * enemyOn));
            } else if (enemyOn == 3) {
                addScore(2000);
                drawnscores.add(new DrawnPoint(x + lay.getWidth() / 3, y - 20, 2000));
            } else if (enemyOn == 4) {
                addScore(4000);
                drawnscores.add(new DrawnPoint(x + lay.getWidth() / 3, y - 20, 4000));
            } else if (enemyOn == 5) {
                addScore(8000);
                drawnscores.add(new DrawnPoint(x + lay.getWidth() / 3, y - 20, 8000));
            } else {
                addScore(16000);
                drawnscores.add(new DrawnPoint(x + lay.getWidth() / 3, y - 20, 16000));
            }

        }
    }

    // method that spawns a bonus item whenever a set amount of layers have fallen
    // to their destination
    public void spawnItem(int atDestination) {
        // checks if there are enough layers on the white platform to spawn an item
        for (int i = 0; i < LAYERNUMBERS[level - 1].length; i++) {
            // bonus item number is used to make sure it doesn't spawn infinitely and to
            // keep track of each item
            if (atDestination == LAYERNUMBERS[level - 1][i] && bonusItem.getNum() != i) {
                Point p = ITEMSPAWN[level - 1]; // gets the point where the item should spawn depending on the level
                bonusItem = new Item((int) p.getX(), (int) p.getY(), level, i); // makes new item
            }
        }
    }

    // what to draw for the introscreen
    // has scoreboard. could be put at the end
    // could also put info on how much each thing is worth in terms of points
    private void IntroScreen(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        // background. will need to be updated later
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        // load fonts of different sizes
        Font fnt = loadFont("util/GameFont.ttf", 20);
        Font Titlefnt = loadFont("util/GameFont.ttf", 60);
        Font SubHeadingfnt = loadFont("util/GameFont.ttf", 40);

        Image PeterPepper = new ImageIcon("pics/BurgerTimePlay.png").getImage();
        // red title, white for subheading
        g.setColor(Color.RED);
        g.setFont(Titlefnt);
        g.drawString("Burger Time", 96, 135);
        g.setFont(SubHeadingfnt);
        g.setColor(Color.WHITE);
        g.drawString("Best 5 Players:", 100, 280);
        g.setFont(fnt);
        g.drawString("Press Enter to Start", 200, 610);
        FontMetrics metrics = g2d.getFontMetrics();

        int y = 0;
        if (high != null) {
            for (Score s : high) {
                g.setColor(new Color(252, 255, 158));// yellowish colour for scores
                g.drawString(s.getName(), 100, y * 45 + 340);
                String score = "" + s.getScore();
                int width = metrics.stringWidth(score);
                g.drawString("" + s.getScore(), 700 - width, y * 45 + 340);
                y++;
            }
        }

        // image icon of Peter Pepper
        g.drawImage(PeterPepper, 338, 660, null);
        //press enter to start the game
        if (keys[KeyEvent.VK_ENTER] && entercooldown < 0) {
            screen = GAME;
            entercooldown = ENTERCD;
        }
    }

    // draws the end screen
    private void EndScreen(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        stopSounds();
        resetBackground();//stops music
        g.drawImage(CHARACTERS, 260, 700, null);
        g.setColor(Color.BLACK);

        Font fnt = loadFont("util/GameFont.ttf", 30);
        Font Titlefnt = loadFont("util/GameFont.ttf", 80);
        Font SubHeadingfnt = loadFont("util/GameFont.ttf", 40);
        // red title, white for subheading
        g.setColor(Color.RED);
        g.setFont(Titlefnt);
        g.drawString("GAME OVER", 60, 180);
        g.setFont(SubHeadingfnt);
        g.setColor(Color.WHITE);
        g.drawString("Best 5 Players:", 100, 300);
        g.setFont(fnt);
        FontMetrics metrics = g2d.getFontMetrics();
        g.setColor(Color.GREEN);
        g.drawString("PLAY AGAIN", 270, 650);
        int y = 0;
        //draws top scores
        if (high != null) {
            for (Score s : high) {
                g.setColor(new Color(252, 255, 158));// yellowish colour for scores
                g.drawString(s.getName(), 100, y * 45 + 385);
                String score = "" + s.getScore();
                int width = metrics.stringWidth(score);
                g.drawString("" + s.getScore(), 700 - width, y * 45 + 385);
                y++;
            }
        }
        //press enter to return to main screen
        if (keys[KeyEvent.VK_ENTER] && entercooldown < 0) {
            screen = INTRO;
            entercooldown = ENTERCD;
            completeReset();//completely resets the game
        }
    }

    // draws the name select screen
    private void NameSelect(Graphics g) {
        //stops all music
        resetBackground();
        stopSounds();
        Graphics2D g2d = (Graphics2D) g;
        FontMetrics metrics = g2d.getFontMetrics();
        Font fnt = loadFont("util/GameFont.ttf", 40);
        Font fnt2 = loadFont("util/GameFont.ttf", 20);
        String[][] alphabet = NameSelector.ALPHABET;
        g.setColor(Color.WHITE);
        g.setFont(fnt);
        // draw alphabet
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 10; j++) {
                g.drawString(alphabet[i][j], 35 + j * 80, (450 + 80) + i * 80);
            }
        }
        // draw buttons
        g.drawString("SPA", 35, 320 + 450);
        g.drawString("DEL", 200, 320 + 450);
        g.drawString("SAVE", 650, 320 + 450);
        // draw title
        g.drawString("Enter Your Name", 100, 80);
        // draw the Leaderboard
        g.setFont(fnt2);
        int y = 0;
        if (high != null) {
            for (Score s : high) {
                int place = y + 1;
                // if the player's score is in top 5
                if (place == LeaderboardSpot(high, playerScore)) {
                    nameSelect.drawScore(g, playerScore, y);
                    y++;
                    place++;
                }
                g.setColor(new Color(252, 255, 158));
                g.drawString(place + ". " + s.getName(), 100, y * 45 + 150);
                String score = "" + s.getScore();
                int width = metrics.stringWidth(score);
                g.drawString(s.getScore() + " pts", 650 - width, y * 45 + 150);
                y++;
            }
            // if player's score out of top 5 (i.e player is bad at game)
            if (LeaderboardSpot(high, playerScore) == 6) {
                nameSelect.drawScore(g, playerScore, 5);
            }
        }
        nameSelect.draw(g, keys);//draws the nameselect box, and letters
    }

    // method to draw the number of levels the player has passed
    // draws burger with 10 for every 10 levels beaten, 5 for 5, 1 for every additional
    public void drawLevels(Graphics g) {
        int tens = (totLevel - (totLevel % 10)) / 10;
        int fives = (totLevel % 10 - (totLevel % 10) % 5) / 5;
        int ones = totLevel % 10 - (fives * 5);
        int tot = tens + fives + ones;
        for (int i = 0; i < tot; i++) {
            if (i < tens) {
                g.drawImage(levelTen, 805, 818 - (i * 30), null);
            } else if (i < tens + fives) {
                g.drawImage(levelFive, 805, 818 - (i * 30), null);
            } else {
                g.drawImage(levelOne, 805, 818 - (i * 30), null);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        int key = ke.getKeyCode();
        keys[key] = false;
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int key = ke.getKeyCode();
        keys[key] = true;
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }


    // paint method
    @Override
    public void paint(Graphics g) {
        entercooldown--;// entercooldown give a cooldown for the enterkey.
        if (screen == INTRO) {
            IntroScreen(g);
        } else if (screen == GAME) {
            g.drawImage(back, 0, 0, null);
            g.setColor(Color.WHITE);
            Font fnt = loadFont("util/GameFont.ttf", 25);
            g.setFont(fnt);
            // player's score
            g.drawString("Score: ", 30, 65);
            g.drawString("" + playerScore, 210, 65);
            g.drawString("Peppers: ", 550, 65);
            g.drawString("" + player.getPeppers(), 760, 65);
            g.setColor(Color.BLACK);

            // draw layers, enemies, peppers, scores
            for (Layer lay : BurgerLayers) {
                lay.draw(g);
            }
            for (Enemy e : Enemies) {
                e.draw(g);
            }
            for (Pepper p : peppers) {
                p.draw(g);
            }
            for (DrawnPoint pointval : drawnscores) {
                pointval.draw(g);
            }
            drawLevels(g);

            player.draw(g);
            bonusItem.draw(g);
        } else if (screen == END) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            EndScreen(g);
        } else if (screen == NAMESELECT) {
            //black background
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            NameSelect(g);
        }
    }

    public static void main(String[] arguments) {
        new BurgerTime();
    }
}