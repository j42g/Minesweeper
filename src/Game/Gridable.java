package Game;

import java.util.ArrayList;

public interface Gridable {
    boolean isLost();
    boolean isWon();
    ArrayList<Tile> getRevealed();
}
