package game;

import java.io.*;
import java.util.*;

public class ScoreManager {

    private static final String FILE = "data/highscores.txt";
    private static final int MAX_SCORES = 10; // keep top 10

    //one entry in the leaderboard
    public static class ScoreEntry {
        public final String name;
        public final int score;
        public final boolean isPlayer; // true = this session's score

        public ScoreEntry(String name, int score, boolean isPlayer) {  //pass in params + assign
            this.name = name;
            this.score = score;
            this.isPlayer = isPlayer;
        }
    }

    //save a new score — appends to file then trims to top 10 only
    public static void saveScore(String name, int score) {
        List<ScoreEntry> all = loadScores(null); // load existing without marking player
        all.add(new ScoreEntry(name, score, false));

        //sort desc
        all.sort((a, b) -> Integer.compare(b.score, a.score));

        //trim to top 10
        if (all.size() > MAX_SCORES) all = all.subList(0, MAX_SCORES);

        //write back to file
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE, false))) {
            for (ScoreEntry e : all) {
                pw.println(e.name + "," + e.score);
            }
        }
        catch (IOException ex) {
            System.out.println("ScoreManager: could not save - " + ex.getMessage());

        }
    }

    //load scores from file, marking playerName as isPlayer = true
    public static List<ScoreEntry> loadScores(String playerName) {
        List<ScoreEntry> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name  = parts[0].trim();
                    int    score = Integer.parseInt(parts[1].trim());
                    boolean mine = name.equals(playerName);
                    list.add(new ScoreEntry(name, score, mine));
                }
            }
        } catch (IOException ex) {
            System.out.println("not loading - " + ex.getMessage());
        }

        //sort desc
        list.sort((a, b) -> Integer.compare(b.score, a.score));
        return list;
    }
}