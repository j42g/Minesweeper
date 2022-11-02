package Game;

public interface Tileable {
    int getCount();
    boolean isMarked();
    boolean isRevealed();
    int[] getCoord();
    Tileable[] getNeighbours();
    void changeMarked();
}
