package Starter;

import Game.Grid;
import Solver.DetSolve;

public class Starter {

    public static void main(String[] args) {

        /*Grid g = new Grid('h');
        DetSolve s = new DetSolve(g);*/

        long t1 = System.nanoTime();
        Util.test(10000);
        long t2 = System.nanoTime();
        System.out.println((t2 - t1)/100000000d);




    }

}
