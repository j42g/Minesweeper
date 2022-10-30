package Game;

public class Tile {

    private final int x;
    private final int y;
    private boolean revealed;
    private boolean marked;
    private final boolean bomb;
    private Tile[] neighbours;
    private int count;
    private final int ID;

    public Tile(int x, int y, boolean bomb, int ID) {
        this.x = x;
        this.y = y;
        this.bomb = bomb;
        this.revealed = false;
        this.marked = false;
        this.ID = ID;
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