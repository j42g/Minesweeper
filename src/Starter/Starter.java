package Starter;

import Game.Grid;
import Game.NGrid;
import Solver.DetSolve;

import java.util.ArrayList;

public class Starter {

    public static void main(String[] args) {

        //Util.test(10000);

        NGrid g = new NGrid(new int[]{4, 5}, 4);
        for(int i = 0; i < 19; i++){
            Util.printArr(g.indexToCoord(i));
        }

    }

}
