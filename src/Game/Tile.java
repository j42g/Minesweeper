package Game;

public class Tile {

    private final int x;
    private final int y;
    private boolean revealed;
    private boolean marked;
    private final boolean bomb;
    private Tile[] neighbours;
    private int count;

    public Tile(int x, int y, boolean bomb) {
        this.x = x;
        this.y = y;
        this.bomb = bomb;
        this.revealed = false;
        this.marked = false;
    }

    void addNeighbours(Tile[] neighbours) {
        this.neighbours = neighbours;
        this.count();
    }

    boolean isBomb() {
        return this.bomb;
    }

    void changeMarked() {
        this.marked = !this.marked;
    }

    void setCount(int count){ // only to enter custome fields
        this.count = count;
    }

    private void count() {
        for(Tile i : this.neighbours) {
            if(i.isBomb()) {
                this.count++;
            }
        }
    }

    public Tile[] getNeighbours() {
        return this.neighbours;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getCount() {
        return this.count;
    }

    public boolean isMarked() {
        return this.marked;
    }

    public void reveal() {
        this.revealed = true;
    }

    public boolean isRevealed() {
        return this.revealed;
    }

    public int getID() {
        return this.ID;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

}