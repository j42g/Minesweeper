package Game;

import java.util.ArrayList;

public class NGrid implements Gridable {

    private final Tileable[] field;
    private final int totalSquares;
    private final int totalBombs;
    private final int dimension;
    private final int[] dimensions;
    private int markedBombCount;
    private int revealedCount;
    private ArrayList<Tileable> revealed;
    private boolean lost;
    private boolean won;
    private boolean firstMove;

    public NGrid(int[] dimensions, int bombs){
        this.dimension = dimensions.length;
        this.dimensions = dimensions;
        int tempTotal = 1;
        for(int i : dimensions){
            tempTotal *= i;
        }
        this.totalSquares = tempTotal;
        this.field = new Tileable[tempTotal];
        this.totalBombs = bombs;
        this.markedBombCount = 0;
        this.revealedCount = 0;
        this.revealed = new ArrayList<Tileable>();
        this.lost = false;
        this.won = false;
        this.firstMove = true;
        this.genField();
    }

    private void genField(){
        int remainingBombs = this.totalBombs;
        int remainingSquares = this.totalSquares;
        for(int i = 0; i < this.totalSquares; i++){
            if (Math.random() < remainingBombs / ((double) remainingSquares)) {
                this.field[i] = new NTile(this.indexToCoord(i), true);
                remainingBombs--;
            } else {
                this.field[i] = new NTile(this.indexToCoord(i), false);
            }
            remainingSquares--;
        }
    }

    @Override
    public boolean isLost() {
        return this.lost;
    }

    @Override
    public boolean isWon() {
        return this.won;
    }

    @Override
    public ArrayList<Tileable> getRevealed() {
        return this.revealed;
    }

    @Override
    public Tileable getTileAt(int[] coord) {
        return this.field[this.coordToIndex(coord)];
    }

    @Override
    public int getRemainingBombCount() {
        return this.totalBombs - this.markedBombCount;
    }

    @Override
    public int getRemainingUnrevealedCount() {
        return this.totalSquares - this.revealedCount;
    }

    @Override
    public int move(Move m) {
        return 0;
    }

    public int[] indexToCoord(int index){
        int[] coord = new int[this.dimension];
        int nextIndex;
        for(int i = coord.length - 1; i > 0; i--){
            coord[this.dimension - i] = index % this.dimensions[i];
            index %= this.dimensions[i];
        }
        return coord;
    }
    public int coordToIndex(int[] coord){
        int index = 0;
        int currMul = 1;
        for(int i = this.dimension - 1; i > 0; i--){

        }
        return index;
    }
}
