package Solver;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import Game.Grid;
import Game.Move;
import Game.Tile;

public class DetSolve {

    private final Grid grid;
    private final ArrayList<BigInteger> allCurrSolutions;
    private final Stack<Move> moveStack;
    private boolean allCornersOpen;

    public DetSolve(Grid g) {
        this.grid = g;
        this.moveStack = new Stack<Move>();
         this.allCurrSolutions = new ArrayList<BigInteger>();
        this.allCornersOpen = false;
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
        if (a.length == 0) { // check if empty
            return;
        }
        // split into sections for optimization
        ArrayList<ArrayList<Integer>> sections = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> remainingEqs = new ArrayList<Integer>();
        for (int i = 0; i < a.length; i++) { // fill with all equation indices
            remainingEqs.add(i);
        }
        sections.add(this.genSection(a, 0));
        remainingEqs.removeAll(sections.get(0));
        while (!remainingEqs.isEmpty()) {
            sections.add(0, this.genSection(a, remainingEqs.get(0)));
            remainingEqs.removeAll(sections.get(0));
        }
        // FOR EACH SECTION
        ArrayList<Integer> secVars;
        int[][] currA;
        int[] currB;
        int[] currXi;
        BigInteger AND;
        BigInteger OR;
        boolean foundSolution = false;
        int[] p;
        double bestP = 0;
        Move bestMove = null;
        //Util.printEqns(a, b);
        for (ArrayList<Integer> sec : sections) {
            //System.out.println(sec);
            this.allCurrSolutions.clear();
            secVars = this.getSectionVars(a, sec);
            currXi = new int[secVars.size()];
            Arrays.fill(currXi, -1); // invalidate everything
            currA = new int[sec.size()][secVars.size()];
            currB = new int[sec.size()];
            for (int i = 0; i < currA.length; i++) { // fill currA and currB with a and b
                for (int j = 0; j < secVars.size(); j++) {
                    currA[i][j] = a[sec.get(i)][secVars.get(j)];
                }
                currB[i] = b[sec.get(i)];
            }

            this.guessSolutions2(currA, currB, currXi, 0);
            OR = new BigInteger("0");
            AND = new BigInteger("0");
            for (BigInteger currSolution : this.allCurrSolutions) { // AND and OR
                AND = AND.and(currSolution);
                OR = OR.or(currSolution);
            }
            for (int i = 0; i < Math.max(AND.bitCount(), OR.bitCount()); i++) { // check if solution is found
                if (AND.testBit(i)) {
                    this.moveStack.push(new Move(edgeUnrevealed.get(secVars.get(i)), true));
                    System.out.println("PUSHED EQ: " + this.moveStack.peek());
                    foundSolution = true;
                }
                if (!OR.testBit(i)) {
                    this.moveStack.push(new Move(edgeUnrevealed.get(secVars.get(i)), false));
                    System.out.println("PUSHED EQ: " + this.moveStack.peek());
                    foundSolution = true;
                }
            }
            p = new int[currXi.length];
            for (int i = 0; i < p.length; i++) {
                for (BigInteger currSolution : this.allCurrSolutions) {
                    if (!currSolution.testBit(i)) {
                        p[i]++;
                    }
                }
                if (p[i] / ( (double) p.length) > bestP) {
                    bestP = p[i] / ( (double) p.length);
                    bestMove = new Move(edgeUnrevealed.get(secVars.get(i)), false);
                }
            }
        }
        if (!foundSolution && bestMove != null && this.allCornersOpen) { // have to guess
            System.out.println("PUSHED GUESSED: " + bestMove);
            this.moveStack.push(bestMove);
        }
    }

    private ArrayList<Integer> getSectionVars(int[][] a, ArrayList<Integer> section) {
        ArrayList<Integer> countedVars = new ArrayList<Integer>();
        for (Integer integer : section) {
            for (int var = 0; var < a[0].length; var++) {
                if (a[integer][var] == 1 && !countedVars.contains(var)) {
                    countedVars.add(var);
                }
            }
        }
        return countedVars;
    }

    private ArrayList<Integer> genSection(int[][] a, int index){
        ArrayList<Integer> section = new ArrayList<Integer>();
        section.add(index);
        int currEqInd;
        for(int i = 0; i < section.size(); i++){
            currEqInd = section.get(i);
            for(int j = 0; j < a[0].length; j++) { // all variables in eq i
                if (a[currEqInd][j] == 1) { // check if set
                    for(int k = 0; k < a.length; k++){ // check all other equations for j
                        if(k == i){ // dont have to check itself
                            continue;
                        }
                        if(a[k][j] == 1 && !section.contains(k)){
                            section.add(k);
                        }
                    }
                }
            }
        }

        return section;
    }

    private void guessSolutions2(int[][] a, int[] b, int[] xi, int aiIndex) { // guess solutions to equation aiIndex given x_i
        if (aiIndex == a.length) {
            BigInteger solution = new BigInteger("0");
            for(int i = 0; i < xi.length; i++){
                if(xi[i] == 1){
                    solution = solution.setBit(i);
                }
            }
            this.allCurrSolutions.add(solution);
            return;
        }
        if (checkEquation(a[aiIndex], b[aiIndex], xi)) {
            guessSolutions2(a, b, xi, aiIndex + 1);
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
                guessSolutions2(a, b, xiNew, aiIndex + 1);
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
        }
        this.allCornersOpen = true;
    }

    private void moveHandle() {
        while (!this.moveStack.empty()) {
            System.out.println("MOVED: " + this.moveStack.peek());
            this.grid.move(this.moveStack.pop());
        }
        this.grid.print();
        //try { Thread.sleep(2000); } catch (Exception e) {}
    }
}