package outsidergame.game;

import outsidergame.Launcher;
import outsidergame.entities.CharacterBase;

public class GameLoop extends Thread {
    public static volatile boolean endFlag = false;
    private final double sleepTime = (/** one millisecond in nanoseconds */1000000*(1000/60));
    private double curTime = 0;

    @Override
    public void run() {
        while (!endFlag) {
            try {
                Thread.sleep((long) ((int) sleepTime / 1000000), (int) sleepTime % 1000000);

                curTime += 1;

                for (CharacterBase charBase : GameCanvas.characters) {
                    charBase.update(curTime);
                }

                boolean allDead = true;
                for (CharacterBase charBase : GameCanvas.characters) {
                    if (charBase.getLives() > 0) {
                        allDead = false;
                        break;
                    }
                }

                if (allDead) {
                    Launcher.gameCanvas.resetGame();
                }

                if (GameCanvas.characters[0].dead) {
                    GameCanvas.characters[1].opponentDead = true;
                } else if (GameCanvas.characters[1].dead) {
                    GameCanvas.characters[0].opponentDead = true;
                } else {
                    for (CharacterBase charBase : GameCanvas.characters) {
                        charBase.opponentDead = false;
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("thread down, gamers!");
                break;
            }
        }
    }
}
