package Game;

import java.util.ArrayList;

public interface Gridable {

    public final static int LOST = -2;
    public final static int MOVE_VALID = 0;
    public final static int MOVE_INVALID = -1;
    public final static int WON = 1;
    boolean isLost();
    boolean isWon();
    ArrayList<Tileable> getRevealed();
    Tileable getTileAt(int[] coord);
    int getRemainingBombCount();
    int getRemainingUnrevealedCount();
    int move(Move m);
}
