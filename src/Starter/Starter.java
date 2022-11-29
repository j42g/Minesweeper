package Starter;

import Game.Grid;
import Input.HTTPGame;
import Input.InputGame;
import Solver.DetSolve;

public class Starter {
    public static void main(String[] args) {
        InputGame.solve();
        try {
            HTTPGame.getHTML("https://minesweeper.online/game/1783966242");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}