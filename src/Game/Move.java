package Game;

public class Move {

    private final int x;
    private final int y;
    private final boolean mark;

    public Move(int x, int y, boolean mark){
        this.x = x;
        this.y = y;
        this.mark = mark;
    }

    public Move(Tile a, boolean mark){
        this.x = a.getX();
        this.y = a.getY();
        this.mark = mark;
    }

    public Move(int x, int y){
        this(x, y, false);
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
        if(o instanceof Move){
            Move b = (Move) o;
            return this.x == b.x && this.y == b.y && this.mark == b.mark;
        } else {
            return false;
        }
    }

    @Override
    public String toString(){
        return "(" + this.x + ", " + this.y + ", " + this.mark + ")";
    }

}
