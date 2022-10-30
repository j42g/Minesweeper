package Game;

import java.util.Scanner;
import java.util.ArrayList;

import Starter.Util;

public class Grid {

    // GAME CODES
    public final static int LOST = -2;
    public final static int MOVE_VALID = 0;
    public final static int MOVE_INVALID = -1;
    public final static int WON = 1;

    private Tile[][] field;
    private final int width;
    private final int height;
    private final int totalSquares;
    private final int totalBombs;
    private int markedBombCount;
    private int revealedCount;
    private boolean lost;
    private boolean won;
    private boolean firstMove;

    public Grid(int width, int height, int bombs) {
        // Spielparameter
        this.width = width;
        this.height = height;
        this.totalSquares = this.width * this.height;
        this.totalBombs = bombs;
        this.markedBombCount = 0;
        this.revealedCount = 0;
        this.lost = false;
        this.won = false;
        this.firstMove = true;
        this.generateField();
    }

    public Grid(char d) {
        switch (d) {
            case 'e':
                this.width = 8;
                this.height = 8;
                this.totalBombs = 10;
                break;
            case 'm':
                this.width = 16;
                this.height = 16;
                this.totalBombs = 40;
                break;
            case 'h':
                this.width = 30;
                this.height = 16;
                this.totalBombs = 99;
                break;
            default:
                this.width = 10;
                this.height = 10;
                this.totalBombs = 16;
        }
        this.markedBombCount = 0;
        this.revealedCount = 0;
        this.totalSquares = this.width * this.height;
        this.lost = false;
        this.won = false;
        firstMove = true;
        this.generateField();
    }

    public void consoleGame() { // main gameloop
        Scanner s = new Scanner(System.in);
        int x;
        int y;
        int m;
        while (!lost) {
            System.out.println("Curr Grid:");
            this.print();
            System.out.print("X? ");
            x = s.nextInt() - 1;
            System.out.print("Y? ");
            y = s.nextInt() - 1;
            if(!(-1 < x && x < this.width && -1 < y && y < this.height)) {
                System.out.println("Point not on grid.");
                continue;
            }
            System.out.print("Reveal=0 or Mark=1?");
            m = s.nextInt();
            this.move(new Move(x, y, (m == 1)));
            this.genEquations();
        }
        System.out.println("You lost.");
    }

    public void genEquations() {
        // get a list of all non-zero count, revealed thingies
        ArrayList<Tile> edge = new ArrayList<Tile>();
        Tile curr;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                curr = field[x][y];
                if((curr.isRevealed() || curr.isMarked()) && curr.getCount() != 0) {
                    edge.add(curr);
                }
            }
        }
        // print out all equations
        int variableCount = 0;
        String totalEquation = "";
        String currEquation;
        boolean empty;
        for(Tile e : edge) {
            empty = true;
            currEquation = e.getCount() + "=";
            for(Tile n : e.getNeighbours()) {
                if(!n.isRevealed())
                    currEquation = currEquation + "+x_" + n.getID();
                empty = false;
            }
            if(!empty) {
                totalEquation = totalEquation + currEquation + ",";
            }
        }
        System.out.println(totalEquation);


    }

    public int move(Move m) {
        Tile curr = this.field[m.getX()][m.getY()];
        if (m.shouldMark()) {
            if(curr.isRevealed()) {
                return MOVE_INVALID;
            }
            curr.changeMarked();
            if(curr.isMarked()){
                this.markedBombCount++;
            } else {
                this.markedBombCount--;
            }
        } else { // wanne click the tile
            if(this.firstMove){ // first move
                this.firstMove = false;
                if(curr.isBomb()){
                    this.handleFirstMove(m.getX(), m.getY());
                    return MOVE_VALID;
                }
            }
            if(curr.isBomb()){ // is bomb
                this.lost = true;
                return LOST;
            }
            if (curr.isRevealed()) { // already revealed
                return MOVE_INVALID;
            }
            this.revealSection(curr);
            return MOVE_VALID;
        }
        if(this.checkIfSolved()) {
            return WON;
        } else {
            return MOVE_VALID;
        }
    }

    private void revealSection(Tile curr) {
        curr.reveal();
        this.revealedCount++;
        if(curr.getCount() != 0) {
            return;
        }
        for (Tile i : curr.getNeighbours()) {
            if(i.isRevealed()) {
                continue;
            }
            if (i.getCount() == 0) {
                this.revealSection(i);
                continue;
            }
            i.reveal();
            this.revealedCount++;
        }
    }

    private boolean checkIfSolved() {
        Tile curr;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                curr = this.field[x][y];
                if (curr.isBomb() ^ curr.isMarked()) {
                    return false;
                }
            }
        }
        this.won = true;
        return true;
    }

    private void handleFirstMove(int x, int y) {
        this.generateField();
        if (this.field[x][y].isBomb()) {
            this.handleFirstMove(x, y);
        } else {
            this.revealSection(this.field[x][y]);
        }
    }

    private void generateField() {
        // gen field itself
        int remainingBombs = this.totalBombs;
        int remainingSquares = this.totalSquares;
        this.field = new Tile[this.width][this.height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (Math.random() < remainingBombs / ((double) remainingSquares)) {
                    this.field[x][y] = new Tile(x, y, true, x*this.width + y);
                    remainingBombs--;
                } else {
                    this.field[x][y] = new Tile(x, y, false, x*this.width + y);
                }
                remainingSquares--;
            }
        }
        // give neighbours
        ArrayList<Tile> s;
        Tile[] arr;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                s = new ArrayList<Tile>();
                for (int i = -1; i < 2; i++) { // x-offset
                    for (int j = -1; j < 2; j++) { // y-offset
                        if (i == 0 && j == 0) { // (x, y) is not a neighbour of (x, y)
                            continue;
                        }
                        if ((-1 < (x + i) && (x + i) < this.width && -1 < (y + j) && (y + j) < this.height)) { // check
                            // if in
                            // Grid
                            s.add(field[x + i][y + j]);
                        }
                    }
                }
                arr = new Tile[s.size()];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = s.get(i);
                }
                this.field[x][y].addNeighbours(arr);
            }
        }
    }

    public Tile[][] getField() {
        return this.field;
    }

    public boolean isLost() {
        return this.lost;
    }

    public boolean isWon() {
        return this.won;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void print() {
        System.out.println("Remaining Bombs:\t" + (this.totalBombs-this.markedBombCount) +
                "\tUnrevealed Tiles:\t" + (this.totalSquares - this.revealedCount));

        if (this.lost || this.won) {
            this.printRevealed();
        } else {
            this.printNormal();
        }
    }

    public void printDebug(boolean see) {
        System.out.println("Remaining Bombs:\t" + (this.totalBombs-this.markedBombCount) +
                "\tUnrevealed Tiles:\t" + (this.totalSquares - this.revealedCount));
        if(see) {
            this.printRevealed();
        } else {
            this.printNormal();
        }
    }

    private void printNormal() {
        // Rand
        System.out.print("---");
        for (int x = 0; x < this.width; x++) {
            System.out.print("-" + Util.toNum(x + 1, 2) + "-");
        }
        System.out.print("-\n");
        // Grid
        for (int y = 0; y < this.height; y++) {
            System.out.print(Util.toNum(y + 1, 2) + "|"); // Rand
            for (int x = 0; x < this.width; x++) {
                if (field[x][y].isMarked()) {
                    System.out.print("  M ");
                    continue;
                }
                if (!field[x][y].isRevealed()) {
                    System.out.print("    ");
                    continue;
                }
                System.out.print(" " + Util.toNum(field[x][y].getCount(), 2) + " ");
            }
            System.out.print("|\n"); // Rand + Zeilenumsprung
        }
        // Rand
        for (int x = 0; x < this.width * 4 + 4; x++) {
            System.out.print("-");
        }
        System.out.print("\n");
    }

    private void printRevealed() {
        // Rand
        System.out.print("---");
        for (int x = 0; x < this.width; x++) {
            System.out.print("-" + Util.toNum(x + 1, 2) + "-");
        }
        System.out.print("-\n");
        // Grid
        for (int y = 0; y < this.height; y++) {
            System.out.print(Util.toNum(y + 1, 2) + "|"); // Rand
            for (int x = 0; x < this.width; x++) {
                if (field[x][y].isBomb()) {
                    System.out.print("  B ");
                } else {
                    System.out.print(" " + Util.toNum(field[x][y].getCount(), 2) + " ");
                }
            }
            System.out.print("|\n"); // Rand + Zeilenumsprung
        }
        // Rand
        for (int x = 0; x < this.width * 4 + 4; x++) {
            System.out.print("-");
        }
        System.out.print("\n");

    }
}