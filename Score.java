/*
Score.java
Leo Lei
Class for the scoreboard of the game. getting the score, name, etc. The scoreboard will display the
top 5 best scores.
 */
class Score implements Comparable<Score> {
    String name;
    int score;

    public Score(String n, int s) {
        name = n;
        score = s;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(Score s2) {
        if (score == s2.score) {
            return -name.compareTo(s2.name);
        } else {
            return s2.score - score;
        }
    }

    public String twoLine() {
        return String.format("%s\n%d", name, score);
    }

    @Override
    public String toString() {
        return String.format("%-20s%5d", name, score);
    }
}