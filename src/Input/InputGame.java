package Input;

import Game.Grid;
import Game.Move;
import Solver.DetSolve;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class InputGame {

    private static final int WIDTH = 30;
    private static final int HEIGHT = 16;
    private static final int SQUARELENGTH = 24;
    private static final int TOTALBOMBS = 99;
    private static final int[] URCorner = new int[]{1022, 310};

    // hard/evil etc : {1055, 310}
    // hard/evil etc : {1022, 310}
    // 80*40 : {422, 360}
    // LAPTOP : {511, 311}

    //
    private static final int[] coordsULC = {URCorner[0] - 1, URCorner[1] - 1};
    private static final int[] coordsLRC = {URCorner[0] + SQUARELENGTH*WIDTH + 1, URCorner[1] + SQUARELENGTH*HEIGHT + 1};
    private static final Rectangle section = new Rectangle(coordsULC[0], coordsULC[1],
            coordsLRC[0] - coordsULC[0], coordsLRC[1] - coordsULC[1]);

    public static void solve(){
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
        for(int j = 0; j < 100; j++){
            a = new Grid(WIDTH, HEIGHT, TOTALBOMBS, takeScreenshot());
            b = new DetSolve(a);
            //a.print();
            for(Move i : b.giveBestMoves()) {
                if (i.shouldMark()) {
                    a.move(i);
                }
            }
            b = new DetSolve(a);
            for(Move i : b.giveBestMovesEqOnly()){
                if(i.shouldMark()){
                    continue;
                }
                c.mouseMove(coordsULC[0] + SQUARELENGTH*i.getX() + 12, coordsULC[1] + SQUARELENGTH*i.getY() + 12);
                if(i.shouldMark()){
                    c.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                    c.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                } else {
                    c.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    c.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                }
                //a.print();
                if(shouldStop(a) && false){
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

    private static boolean shouldStop(Grid grid){
        // expand with other logic
        return grid.getRemainingBombCount() <= 1;
    }

    public static void bestMoves(){
        Grid a = new Grid(WIDTH, HEIGHT, TOTALBOMBS, takeScreenshot());
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
