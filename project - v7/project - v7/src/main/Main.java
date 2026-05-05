package main;

import javax.swing.SwingUtilities;

import gamealgorithms.game.Game;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }
}