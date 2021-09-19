package outsidergame.game;

import outsidergame.Launcher;

public class DrawThread extends Thread {
    public static volatile boolean endFlag = false;
    private final double sleepTime = (/** one millisecond in nanoseconds */1000000);

    @Override
    public void run() {
        while (!endFlag) {
            try {
                Thread.sleep((long) ((int) sleepTime / 1000000), (int) sleepTime % 1000000);

                Launcher.gameCanvas.repaint();

            } catch (InterruptedException e) {
                System.out.println("draw thread closing!");
                break;
            }
        }
    }
}
