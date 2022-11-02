package Game;

public class NTile implements Tileable {

    private final int[] coords;
    private NTile[] neighbours;
    private final boolean bomb;

    public NTile(int[] coords, boolean bomb){
        this.coords = coords;
        this.bomb = bomb;
    }

    private boolean isBomb(){
        return this.bomb;
    }

    @Override
    public int getCount() {
        int count = 0;
        for(NTile curr : this.neighbours){
            if(curr.isBomb()){
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean isMarked() {
        return false;
    }

    @Override
    public boolean isRevealed() {
        return false;
    }

    @Override
    public int[] getCoord() {
        return new int[0];
    }

    @Override
    public Tileable[] getNeighbours() {
        return new Tileable[0];
    }

    @Override
    public void changeMarked() {

    }
}
