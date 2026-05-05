package data.util;

import java.io.*;
import java.util.Scanner;

/**
 * Manages the top (high) score for each difficulty level.
 * Scores are stored in a plain-text file as three integers,
 * one per line:  line 0 = EASY, line 1 = MEDIUM, line 2 = HARD.
 */
public class HighScoreManager {

    // ── Constants ─────────────────────────────────────────────────────────────

    private static final String FILE_NAME = "highscore.txt";
    private static final int    DIFF_COUNT = 3;  // EASY / MEDIUM / HARD


    public int loadHighScore(String difficulty) {
        int index = diffIndex(difficulty);
        int[] scores = readAllScores();
        return scores[index];
    }

    public void saveHighScore(int score, String difficulty) {
        int   index  = diffIndex(difficulty);
        int[] scores = readAllScores();

        // Only overwrite when the new score is strictly better
        if (score > scores[index]) {
            scores[index] = score;
            writeAllScores(scores);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /** Converts a difficulty string to an array index (0/1/2). */
    private int diffIndex(String difficulty) {
        return switch (difficulty) {
            case "EASY" -> 0;
            case "HARD" -> 2;
            default     -> 1;   // "medium" and anything unexpected
        };
    }

    /**
     * Reads all three scores from the file.
     * Creates the file (filled with zeros) if it does not exist.
     * Returns {0, 0, 0} on any read / parse error.
     */
    private int[] readAllScores() {
        int[] scores = new int[DIFF_COUNT];   // default = all zeros

        File file = new File(FILE_NAME);

        // Create the file with default zeros if it is missing
        if (!file.exists()) {
            writeAllScores(scores);
            return scores;
        }

        try (Scanner sc = new Scanner(file)) {
            for (int i = 0; i < DIFF_COUNT; i++) {
                if (sc.hasNextInt()) {
                    scores[i] = sc.nextInt();
                }
                // If a line is missing or non-numeric, 0 stays in place
            }
        } catch (IOException e) {
            // Could not read → return the zero-filled array
            e.printStackTrace();
        }

        return scores;
    }

    /**
     * Writes all three scores to the file, one integer per line.
     * Silently ignores write errors so the game never crashes on I/O.
     */
    private void writeAllScores(int[] scores) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int score : scores) {
                bw.write(score + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}