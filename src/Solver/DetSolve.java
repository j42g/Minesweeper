package Solver;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import Game.Grid;
import Game.Move;
import Game.Tile;
import Starter.Util;

public class DetSolve {

    private final Grid grid;
    private final ArrayList<BigInteger> allFullSolutions; // binary representation corresponds to whether tile is bomb or not
    private final Stack<Move> moveStack;

    public DetSolve(Grid g) {
        this.grid = g;
        this.moveStack = new Stack<Move>();
        this.allFullSolutions = new ArrayList<BigInteger>();
    }

    public boolean start() {
        this.random();
        while (!this.grid.isLost() && !grid.isWon()) {
            // actual logic
            this.simpleRules();
            if (!this.moveStack.empty()) {
                this.moveHandle();
                continue;
            }
            this.equationSolution();
            if (!this.moveStack.empty()) {
                this.moveHandle();
                continue;
            }
            // guess for now
            this.random();
            this.moveHandle();
        }
        if(this.grid.isWon()){
            System.out.println("WON");
        } else {
            System.out.println("LOST");
            //this.grid.printDebug(false);
        }
        return this.grid.isWon();
    }

    private void simpleRules() {
        int markedCount;
        ArrayList<Tile> cleanNeighbours = new ArrayList<Tile>(); // clean means unmarked und revealed
        Tile currTile;
        Move currMove;
        for (int x = 0; x < this.grid.getWidth(); x++) {
            for (int y = 0; y < this.grid.getHeight(); y++) { // Loop over revealed Tiles TODO change
                currTile = this.grid.getField()[x][y];
                if (!currTile.isRevealed() || currTile.getCount() == 0) {
                    continue;
                }
                cleanNeighbours.clear();
                markedCount = 0;
                for (Tile neighbour : currTile.getNeighbours()) {
                    if (!neighbour.isMarked() && !neighbour.isRevealed()) {
                        cleanNeighbours.add(neighbour);
                    }
                    if (neighbour.isMarked()) {
                        markedCount++;
                    }
                }
                if (cleanNeighbours.size() == 0) {
                    continue;
                }
                // actual cases
                if (currTile.getCount() == cleanNeighbours.size() + markedCount) { // all neighbours are bombs
                    for(Tile neighbour : cleanNeighbours){
                        currMove = new Move(neighbour, true);
                        if(!this.moveStack.contains(currMove)){
                            this.moveStack.push(currMove);
                        }
                    }

                }
                if (currTile.getCount() == markedCount) { // all neighbours are NOT bombs
                    for(Tile neighbour : cleanNeighbours){
                        currMove = new Move(neighbour, false);
                        if(!this.moveStack.contains(currMove)){
                            this.moveStack.push(currMove);
                        }
                    }
                }
            }
        }
    }

    private void equationSolution() {
        // count/gen edge tiles
        ArrayList<Tile> edgeRevealed = new ArrayList<Tile>();
        ArrayList<Tile> edgeUnrevealed = new ArrayList<Tile>();
        Tile curr;
        boolean isEdgeRevealed;
        for (int x = 0; x < this.grid.getWidth(); x++) {
            for (int y = 0; y < this.grid.getHeight(); y++) {
                curr = this.grid.getField()[x][y];
                if (curr.getCount() == 0 || !curr.isRevealed()) {
                    continue;
                }
                isEdgeRevealed = false;
                for (Tile neighbour : curr.getNeighbours()) {
                    if (!neighbour.isRevealed() && !neighbour.isMarked()) {
                        isEdgeRevealed = true;
                        if (!edgeUnrevealed.contains(neighbour)) {
                            edgeUnrevealed.add(neighbour);
                        }
                    }
                }
                if (isEdgeRevealed) {
                    edgeRevealed.add(curr);
                }
            }
        }

        // generate equations
        int[][] a = new int[edgeRevealed.size()][edgeUnrevealed.size()];
        int[] b = new int[edgeRevealed.size()];
        for (int i = 0; i < b.length; i++) {
            curr = edgeRevealed.get(i);
            b[i] = curr.getCount();
            for (Tile n : curr.getNeighbours()) {
                if (n.isMarked()) {
                    b[i]--;
                } else if (!n.isRevealed()) {
                    a[i][edgeUnrevealed.indexOf(n)] = 1;
                }
            }

        }
        //Util.printEqns(a, b);
        int[] xi = new int[a[0].length];
        Arrays.fill(xi, -1); // invalidate everything
        this.allFullSolutions.clear();
        guessSolutions(a, b, xi, 0);
        // AND and OR the Solutions
        BigInteger and = new BigInteger("0");
        BigInteger or = new BigInteger("0");
        for (BigInteger allFullSolution : this.allFullSolutions) {
            and = and.and(allFullSolution);
            or = or.or(allFullSolution);
        }
        boolean certainSolutionFound = false;
        for(int i = 0; i < and.bitCount(); i++){
            if(and.testBit(i)){
                this.moveStack.push(new Move(edgeUnrevealed.get(i), true));
                certainSolutionFound = true;
            }
        }
        for(int i = 0; i < or.bitCount(); i++){
            if(!or.testBit(i)){
                this.moveStack.push(new Move(edgeUnrevealed.get(i), false));
                certainSolutionFound = true;
            }
        }
        if(certainSolutionFound){
            return;
        }
        // most likely square to not have bomb
        int[] p = new int[xi.length];
        int iMax = 0;
        for(int i = 0; i < p.length; i++){
            for(BigInteger allFullSolution : this.allFullSolutions) {
                if (!allFullSolution.testBit(i)) {
                    p[i]++;
                }
            }
            if(p[i] > p[iMax]){
                iMax = i;
            }
        }
        double iMaxP = p[iMax] / ( (double) this.allFullSolutions.size());
        double randomP = 1 - this.grid.getRemainingBombCount() / ( (double) this.grid.getRemainingUnrevealedCount() );
        if(iMaxP > randomP){
            System.out.println(edgeUnrevealed.get(iMax).toString());
            this.moveStack.push(new Move(edgeUnrevealed.get(iMax), false));
        } else {
            // TODO choose non edge tile at random
        }
    }

    private void guessSolutions(int[][] a, int[] b, int[] xi, int aiIndex) { // guess solutions to equation aiIndex given x_i
        if (aiIndex == a.length) {
            BigInteger solution = new BigInteger("0");
            for(int i = 0; i < xi.length; i++){
                if(xi[i] == 1){
                    solution = solution.setBit(i);
                }
            }
            this.allFullSolutions.add(solution);
            return;
        }
        if (checkEquation(a[aiIndex], b[aiIndex], xi)) {
            guessSolutions(a, b, xi, aiIndex + 1);
            return;
        }
        // generate all Solutions given xi
        ArrayList<Integer> indFreeVars = new ArrayList<Integer>();
        for (int j = 0; j < a[aiIndex].length; j++) { // get Index of free variable of that equation
            if (a[aiIndex][j] == 1 && xi[j] == -1) {
                indFreeVars.add(j);
            }
        }
        int num;
        int[] xiNew = Arrays.copyOf(xi, xi.length);
        for (int i = 0; i < Math.pow(2, indFreeVars.size()); i++) {
            num = i;
            for (Integer indFreeVar : indFreeVars) {
                xiNew[indFreeVar] = num % 2;
                num /= 2;
            }
            if (checkEquation(a[aiIndex], b[aiIndex], xiNew)) {
                guessSolutions(a, b, xiNew, aiIndex + 1);
            }
        }
    }

    private boolean checkEquation(int[] ai, int bi, int[] xi) {
        int sum = 0;
        for (int j = 0; j < ai.length; j++) {
            if (ai[j] == 1) {
                if (xi[j] == -1) {
                    return false;
                }
                sum += xi[j];
            }
        }
        return sum == bi;
    }

    private void random() {
        // top left
        if (!this.grid.getField()[0][0].isRevealed()) {
            this.moveStack.push(new Move(0, 0, false));
            return;
        }
        // top right
        if (!this.grid.getField()[this.grid.getWidth() - 1][0].isRevealed()) {
            this.moveStack.push(new Move(this.grid.getWidth() - 1, 0, false));
            return;
        }
        // bottom right
        if (!this.grid.getField()[this.grid.getWidth() - 1][this.grid.getHeight() - 1].isRevealed()) {
            this.moveStack.push(new Move(this.grid.getWidth() - 1, this.grid.getHeight() - 1, false));
            return;
        }
        // bottom left
        if (!this.grid.getField()[0][this.grid.getHeight() - 1].isRevealed()) {
            this.moveStack.push(new Move(0, this.grid.getHeight() - 1, false));
            return;
        }
        // completely random
        int x;
        int y;
        while (true) {
            x = (int) (Math.random() * this.grid.getWidth());
            y = (int) (Math.random() * this.grid.getHeight());
            if (!this.grid.getField()[x][y].isRevealed()) {
                this.moveStack.push(new Move(x, y, false));
                return;
            }
        }
    }

    private void moveHandle() {
        while (!this.moveStack.empty()) {
            this.grid.move(this.moveStack.pop());
        }
        this.grid.print();
        //try { Thread.sleep(2000); } catch (Exception e) {}
    }
}