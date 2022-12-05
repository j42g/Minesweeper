package Input;

public class Colour {

    public static int TOLERANCE = 10000; // eigentlich das Quadrat der Toleranz

    public static int[] UNREVEALED = new int[]{201, 201, 201}; // 0, 0 RGB
    public static int[] MARKED     = new int[]{86, 86, 86}; // 12, 12 RGB

    public static int[] REVEALED   = new int[]{148, 148, 148}; // 0, 0 RGB
    public static int[] ONE        = new int[]{0, 0, 255}; // 12, 12 RGB
    public static int[] TWO        = new int[]{0, 128, 0}; // 12, 12 RGB
    public static int[] THREE      = new int[]{255, 0, 0}; // 12, 12 RGB
    public static int[] FOUR       = new int[]{0, 0, 128}; // 12, 12 RGB
    public static int[] FIVE       = new int[]{128, 0, 0}; // 12, 12 RGB
    public static int[] SIX        = new int[]{0, 128, 128}; // 12, 12 RGB
    public static int[] SEVEN      = new int[]{0, 0, 0}; // 12, 12 RGB
    public static int[] EIGHT      = new int[]{128, 128, 128}; // 12, 12 RGB

    public static int RGBDistance(int[] a, int[] b){
        int d = 0;
        for(int i = 0; i < 3; i++){
            d += (a[i] - b[i])*(a[i] - b[i]);
        }
        return d; // gibt das Quadrat zurück
    }


}
