package outsidergame.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import outsidergame.entities.CharacterBase;
import outsidergame.entities.Floor;
import outsidergame.entities.Holly;
import outsidergame.entities.Outsider;

@SuppressWarnings("serial")
public class GameCanvas extends JPanel {
    private JFrame container = new JFrame();
    public static Dimension size = null;
    public static Floor floor;
    private GameLoop gameLoop;
    private DrawThread drawThread;
    private BufferedImage bImg;

    public static CharacterBase[] characters;

    public GameCanvas(Rectangle bounds, Dimension size) {
        GameCanvas.size = size;

        resetGame();

        GameCanvas.floor = new Floor(200, size.height - 200, new Rectangle((int) (size.width / 1.7), 50));

        container.setVisible(true);
        container.setBounds(bounds);
        container.setSize(size);
        container.setPreferredSize(size);
        container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setVisible(true);
        this.setBounds(bounds);
        this.setSize(size);
        this.setPreferredSize(size);
        this.setBackground(Color.BLACK);
        this.setOpaque(true);

        container.add(this);

        container.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                GameLoop.endFlag = true;
                gameLoop.interrupt();
                DrawThread.endFlag = true;
                drawThread.interrupt();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                GameLoop.endFlag = true;
                gameLoop.interrupt();
                DrawThread.endFlag = true;
                drawThread.interrupt();
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }

        });

        gameLoop = new GameLoop();
        gameLoop.start();

        drawThread = new DrawThread();
        drawThread.start();
    }

    public void resetGame() {
        characters = new CharacterBase[] {
            new Holly(new Point((int) (size.width/2), (int) (size.height/2)), new Rectangle(48, 48), 1),
            new Outsider(new Point((int) (size.width/2), (int) (size.height/2)), new Rectangle(48, 48), 0)
        };
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        Graphics2D gg;
        if (bImg == null) {
            try {
                // create buffered image
                bImg = new BufferedImage((int) super.getSize().getWidth(), (int) super.getSize().getHeight(),
                        BufferedImage.TYPE_INT_RGB);
                gg = (Graphics2D) bImg.getGraphics();

                // draw the image to the buffered image
                gg.drawImage((Image) ImageIO.read(getClass().getResourceAsStream("../resources/otherCaveBackground.png")), 0, 0, null);
                gg.dispose();
            } catch (Exception e) {
                bImg = null;
            }
        } else {
            g2d.drawImage(bImg, null, null);
        }

        // draw the game boundaries
        g2d.setStroke(new BasicStroke(4f));
        g2d.setColor(Color.RED);

        g2d.drawLine(0, 0, size.width, 0);
        g2d.drawLine(0, 0, 0, size.height);
        g2d.drawLine(size.width, size.height, size.width, 0);
        g2d.drawLine(size.width, size.height, 0, size.height);

        g2d.setStroke(new BasicStroke(1f));

        // draw the floor
        floor.draw(g2d);

        for (CharacterBase curCharacter : characters) {
            curCharacter.draw(g2d);
        }

        if (!characters[0].dead && !characters[1].dead) {
            g2d.setColor(Color.ORANGE);
            g2d.drawString("outsider's lives: " + characters[1].getLives(), 50, 50);
            g2d.drawString("holly's lives: " + characters[0].getLives(), 50, 65);
        }

        g2d.setFont(new Font("Monospaced", Font.BOLD, 40));
        g2d.setColor(Color.GREEN);
        if (characters[0].dead) {
            g2d.drawString("OUTSIDER WINS!", 300, 300);
        } else if (characters[1].dead) {
            g2d.drawString("HOLLY WINS!", 300, 300);
        }
    }
}
