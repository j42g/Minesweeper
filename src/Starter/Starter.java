package Starter;

import Game.Grid;
import Solver.DetSolve;

public class Starter {

    public static void main(String[] args) {
        Grid g = new Grid('h');
        DetSolve s = new DetSolve(g);
        s.start();
        /*Grid g = new Grid('h');
        g.consoleGame();*/
    }

}
