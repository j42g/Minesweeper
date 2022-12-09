package input;

import game.Grid;
import game.Move;
import solver.DetSolve;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Stack;

public class InputGame {

    private static final int WIDTH = 42;
    private static final int HEIGHT = 42;
    private static final int SQUARELEN = 16;
    private static final int TOTALBOMBS = 420;
    private static final int[] URCorner = new int[]{16, 101};

    // hard/evil etc : {1055, 310}
    // hard/evil etc : {1022, 310}
    // 80*40 : {422, 360}
    // LAPTOP : {511, 311}

    // MinesweeperX Curr : {16, 101}

    // derived
    private static final int HALFSQUARELEN = SQUARELEN / 2;
    private static final int[] coordsULC = {URCorner[0] - 1, URCorner[1] - 1};
    private static final int[] coordsLRC = {URCorner[0] + SQUARELEN *WIDTH + 1, URCorner[1] + SQUARELEN *HEIGHT + 1};
    private static final Rectangle section = new Rectangle(coordsULC[0], coordsULC[1],
            coordsLRC[0] - coordsULC[0], coordsLRC[1] - coordsULC[1]);

    public static void solve(boolean shouldMark) {
        for(int i = 3; i > 0; i--){
            System.out.print("\rStarting in " + i);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {}
        }
        System.out.print("\rStarted");
        Grid a;
        DetSolve b;
        Robot c;
        try {
            c = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        for(int j = 0; j < 200; j++){
            a = new Grid(WIDTH, HEIGHT, TOTALBOMBS, SQUARELEN, takeScreenshot());
            //System.out.println(a);
            b = new DetSolve(a);
            //a.print();
            if(!shouldMark){
                markInternal(a, b);
                b = new DetSolve(a);
            }
            for(Move i : b.giveBestMoves()){
                if(i.shouldMark() && !shouldMark){
                    continue;
                }
                click(c, i);
                //a.print();
                if(shouldStop(a) && false){ // change if one wants different behaviour
                    return;
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void markInternal(Grid grid, DetSolve solver){
        Stack<Move> moves = solver.giveBestMoves();
        for(Move i : moves) {
            if (i.shouldMark()) {
                grid.move(i);
            }
        }
        DetSolve solv2 = new DetSolve(grid);
        moves = solv2.giveBestMovesEqOnly();
        for(Move i : moves) {
            if (i.shouldMark()) {
                grid.move(i);
            }
        }
    }

    private static void click(Robot clicker, Move move){
        clicker.mouseMove(coordsULC[0] + SQUARELEN *move.getX() + HALFSQUARELEN, coordsULC[1] + SQUARELEN *move.getY() + HALFSQUARELEN);
        if(move.shouldMark()){
            clicker.mousePress(InputEvent.BUTTON3_DOWN_MASK);
            clicker.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
        } else {
            clicker.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            clicker.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
    }

    private static boolean shouldStop(Grid grid){
        // expand with other logic
        return grid.getRemainingBombCount() <= 1;
    }

    public static void bestMoves(){
        Grid a = new Grid(WIDTH, HEIGHT, TOTALBOMBS, SQUARELEN, takeScreenshot());
        a.print();
        DetSolve b = new DetSolve(a);
        for(Move i : b.giveBestMovesEqOnly()){
            if(!i.shouldMark()) a.move(i);
            if(!i.shouldMark()) System.out.println(i);
        }
        a.print();
    }

    private static BufferedImage takeScreenshot(){
        try {
            Robot awt_robot = new Robot();
            BufferedImage game = awt_robot.createScreenCapture(section);
            ImageIO.write(game, "PNG", new File("screenshot.png"));
            return game;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveScreenshot(){
        try {
            for(int i = 3; i > 0; i--){
                System.out.print("\rStarting in " + i);
                Thread.sleep(1000);
            }
            System.out.print("\rStarted");
            Robot awt_robot = new Robot(); // new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
            BufferedImage game = awt_robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ImageIO.write(game, "PNG", new File("sample.png"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
