# Burger Time

A Java recreation of the classic 1982 arcade game **BurgerTime**, built with Swing by Harry Wei and Leo Lei.

You play as chef Peter Pepper, running across platforms and climbing ladders to stomp giant burger ingredients down onto the plates below. Walk across every part of a bun, patty, lettuce, tomato or cheese layer and it drops to the next floor, knocking any layer underneath it further down. Finish every burger on the stage to clear the level. Mr. Hot Dog, Mr. Egg and Mr. Pickle are chasing you the whole time, so drop layers on them, trap them under falling ingredients, or blast them with pepper to stun them.

The game is controlled entirely from the keyboard, with no mouse input, to keep the old arcade feel.

## Features

- 6 hand-made stages, loaded from text files in `maps/`
- Three enemy types, each with its own chase AI:
  - **Sausage**: takes the shortest path to the player
  - **Egg**: tries to cut the player off
  - **Pickle**: copies the player's movement, and chases like the egg when it can't
- Burger layers that bounce, flop, knock other layers down, and crush or carry enemies
- Pepper spray that stuns enemies for a short time
- Bonus items (ice cream, coffee, fries) that give extra points and a pepper
- Difficulty that rises as your score grows
- Arcade-style name entry and a top 5 high score table that is saved between sessions
- Original-style sound effects and background music

## Controls

| Key | Action |
| --- | --- |
| Arrow keys | Move and climb ladders / move the cursor on the name entry screen |
| Space | Shoot pepper |
| Enter | Start the game / pick a letter on the name entry screen / go back to the title screen |

## Gameplay and scoring

- You start with **3 lives** and **5 peppers**.
- Every **10,000 points** gives you an extra life and makes the enemies faster.
- Bonus items show up in the middle of the stage once enough layers have dropped. They only stay for a short time.

| Event | Points |
| --- | --- |
| Drop a burger layer one floor | 50 |
| Crush a sausage | 100 |
| Crush a pickle | 200 |
| Crush an egg | 300 |
| Drop a layer with 1 / 2 enemies riding it | 500 / 1,000 |
| Drop a layer with 3 / 4 / 5 / 6+ enemies riding it | 2,000 / 4,000 / 8,000 / 16,000 |
| Bonus item (ice cream / coffee / fries) | 500 / 1,000 / 1,500, plus 1 pepper |

When you run out of lives, enter a name of up to 8 characters with the arrow keys and Enter, then pick **SAVE**. The top 5 scores are stored in `util/scores.txt`.

## Requirements

- Java JDK 8 or newer (no external libraries needed)

## Building and running

Run these from the project folder:

```bash
javac *.java
java BurgerTime
```

Images, sounds, maps, the font and the high score file are all loaded with relative paths (`pics/`, `Sounds/`, `maps/`, `util/`). The game has to be started from the project root, or those assets won't load.

## Project structure

| Path | Contents |
| --- | --- |
| `BurgerTime.java` | Entry point; creates the game window |
| `BurgerTimePanel.java` | Main game loop, input, drawing, levels, scoring and screens (title, game, name entry, leaderboard) |
| `Player.java` | Peter Pepper's movement, animation, lives and peppers |
| `Enemy.java` | Shared enemy behaviour (movement, falling, death, stun) |
| `Sausage.java`, `Egg.java`, `Pickle.java` | Each enemy's chase AI and point value |
| `Layer.java` | Shared burger layer behaviour (getting stepped on, falling, flopping, crushing) |
| `TopBun.java`, `BottomBun.java`, `Patty.java`, `Lettuce.java`, `Tomato.java`, `Cheese.java` | The burger ingredients |
| `Pepper.java` | Pepper spray projectile |
| `Item.java` | Bonus items |
| `DrawnPoint.java` | Floating point values shown on screen |
| `Score.java`, `NameSelector.java` | High score entries and the name entry screen |
| `Sound.java`, `SoundEffect.java`, `Music.java` | Audio playback |
| `maps/` | Stage layouts (`mapN.txt`) and enemy spawns (`enemiesN.txt`) |
| `pics/` | Sprites, backgrounds and UI images |
| `Sounds/` | Sound effects and music |
| `util/` | Game font and saved high scores |

## Credits

- Developed by **Harry Wei** and **Leo Lei**
- Based on *BurgerTime* (Data East, 1982)
- Some artwork, including the title screen image, is from [StrategyWiki](https://strategywiki.org/wiki/BurgerTime)

This is a fan-made project for educational purposes. BurgerTime and its characters belong to their respective owners.
