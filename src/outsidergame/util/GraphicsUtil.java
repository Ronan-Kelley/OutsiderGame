package outsidergame.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class GraphicsUtil {

    /**
     *
     * @param img image to be converted to a buffered image
     * @return a buffered image on success, null on failure.
     */
    public static BufferedImage makeBuffered(Image img) {
        BufferedImage bImg = null;
        Graphics2D gg;

        try {
            // create buffered image
            bImg = new BufferedImage(img.getWidth(null), img.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);
            gg = (Graphics2D) bImg.getGraphics();

            // draw the image to the buffered image
            gg.drawImage(img, 0, 0, null);
            gg.dispose();
        } catch (Exception e) {
            bImg = null;
        }

        return bImg;
    }
}
