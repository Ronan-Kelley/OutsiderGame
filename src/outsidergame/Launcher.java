package outsidergame;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;

import outsidergame.game.GameCanvas;

public class Launcher {
    public static GameCanvas gameCanvas;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            System.setProperty("sun.java2d.opengl", "true");
            System.setProperty("sun.java2d.accthreshold", "0");
            gameCanvas = new GameCanvas(new Rectangle(50, 50, 1100, 850), new Dimension(1024, 768));
        });
    }
}
