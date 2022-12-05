package Game;

public class Move {

    private final int x;
    private final int y;
    private final boolean mark;
    private final double probSafe;

    public Move(int x, int y, boolean mark, double probSafe){
        this.x = x;
        this.y = y;
        this.mark = mark;
        this.probSafe = probSafe;
    }

    public Move(Tile a, boolean mark, double probSafe){
        this.x = a.getX();
        this.y = a.getY();
        this.mark = mark;
        this.probSafe = probSafe;
    }

    public Move(int x, int y, double probSafe){
        this(x, y, false, probSafe);
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public boolean shouldMark(){
        return this.mark;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Move b){
            return this.x == b.x && this.y == b.y && this.mark == b.mark;
        } else {
            return false;
        }
    }

    @Override
    public String toString(){
        return "(" + this.x + ", " + this.y + ", " + this.mark + ", " + this.probSafe + ")";
    }

}
