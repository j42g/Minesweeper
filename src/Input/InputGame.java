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

    private static int[] coordsULC = {1022 - 1, 310 - 1};
    private static int[] coordsLRC = {1741 + 1, 693 + 1};
    private static Rectangle section = new Rectangle(coordsULC[0], coordsULC[1],
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
        for(int j = 0; j < 40; j++){
            a = new Grid(takeScreenshot());
            b = new DetSolve(a);
            a.print();
            for(Move i : b.giveBestMoves()){
                //System.out.println(i);
                c.mouseMove(coordsULC[0] + 24*i.getX() + 12, coordsULC[1] + 24*i.getY() + 12);
                if(i.shouldMark()){
                    c.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                    c.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                } else {
                    c.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    c.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                }
                //a.print();
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
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
}
