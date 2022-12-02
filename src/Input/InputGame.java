package Input;

import Game.Grid;
import Game.Move;
import Solver.DetSolve;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class InputGame {

    private static final int[] coordsULC = {1022 - 1, 310 - 1};
    private static final int[] coordsLRC = {1741 + 1, 693 + 1};

    private static final int[] coordsLAPULC = {511 - 1, 311 - 1};
    private static final int[] coordsLAPLRC = {1229 + 1, 693 + 1};
    private static final Rectangle section = new Rectangle(coordsLAPULC[0], coordsLAPULC[1],
            coordsLAPLRC[0] - coordsLAPULC[0], coordsLAPLRC[1] - coordsLAPULC[1]);

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
        for(int j = 0; j < 70; j++){
            a = new Grid(takeScreenshot());
            if(a.getRemainingBombCount() == 0){
                break;
            }
            b = new DetSolve(a);
            a.print();
            System.out.println("Screenshot:\t" + (j + 1));
            for(Move i : b.giveBestMoves()){
                //System.out.println(i);
                c.mouseMove(coordsLAPULC[0] + 24*i.getX() + 12, coordsLAPULC[1] + 24*i.getY() + 12);
                if(i.shouldMark()){
                    c.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                    c.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                } else {
                    c.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    c.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                }
                //a.print();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void bestMoves(){
        Grid a = new Grid(takeScreenshot());
        a.print();
        DetSolve b = new DetSolve(a);
        for(Move i : b.giveBestMoves()){
            System.out.println(i);
        }
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
