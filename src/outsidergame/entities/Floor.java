package outsidergame.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Floor {
    private int x, y;
    private Rectangle size;
    private BufferedImage bImg = null;

    public Floor(int x, int y, Rectangle size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void draw(Graphics2D g) {
        Graphics2D gg;
        if (bImg == null) {
            try {
                // create buffered image
                bImg = new BufferedImage((int) size.width, (int) size.height, BufferedImage.TYPE_INT_RGB);
                gg = (Graphics2D) bImg.getGraphics();

                // draw the image to the buffered image
                gg.setColor(Color.GRAY);
                gg.fillRect(0, 0, size.width, size.height);
                gg.dispose();
            } catch (Exception e) {

            }
        }

        g.drawImage(bImg, null, x, y);
    }

    public Point getPos() {
        return new Point(x, y);
    }

    public Rectangle getSize() {
        return size;
    }

}
