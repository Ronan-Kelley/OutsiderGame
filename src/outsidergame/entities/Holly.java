package outsidergame.entities;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import outsidergame.util.GraphicsUtil;

public class Holly extends CharacterBase {
    private BufferedImage standingSprite = null;
    private BufferedImage inAirSprite = null;
    private BufferedImage walkingSprite1 = null;
    private BufferedImage walkingSprite2 = null;
    private BufferedImage attackingSprite = null;
    private short currentWalkSprite = 1;
    private Timer timer = new Timer();

    public Holly(Point pos, Rectangle characterSize, int controller) {
        super(pos, characterSize, controller);

        timer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                if (currentWalkSprite + 1 > 2) {
                    currentWalkSprite = 1;
                } else {
                    currentWalkSprite++;
                }
            }
        }, 0, (int) (1000/6));
    }

    @Override
    public void draw(Graphics2D g) {
        if (super.dead) {
            return;
        }
        if (standingSprite == null) {
            try {
                standingSprite = GraphicsUtil.makeBuffered((Image) ImageIO.read(getClass().getResourceAsStream("../resources/hollyStanding.png")));
            } catch (Exception e) {
                System.out.println("couldn't make holly into a buffered image!");
                e.printStackTrace();
            }
        }

        if (inAirSprite == null) {
            try {
                inAirSprite = GraphicsUtil.makeBuffered((Image) ImageIO.read(getClass().getResourceAsStream("../resources/hollyJumping.png")));
            } catch (Exception e) {
                System.out.println("couldn't make holly into a buffered image!");
                e.printStackTrace();
            }
        }

        if (walkingSprite1 == null) {
            try {
                walkingSprite1 = GraphicsUtil.makeBuffered((Image) ImageIO.read(getClass().getResourceAsStream("../resources/HollyWalking1.png")));
            } catch (Exception e) {
                System.out.println("couldn't make holly into a buffered image!");
                e.printStackTrace();
            }
        }

        if (walkingSprite2 == null) {
            try {
                walkingSprite2 = GraphicsUtil.makeBuffered((Image) ImageIO.read(getClass().getResourceAsStream("../resources/hollyWalking2.png")));
            } catch (Exception e) {
                System.out.println("couldn't make holly into a buffered image!");
                e.printStackTrace();
            }
        }

        if (attackingSprite == null) {
            try {
                attackingSprite = GraphicsUtil.makeBuffered((Image) ImageIO.read(getClass().getResourceAsStream("../resources/hollyAttacking.png")));
            } catch (Exception e) {
                System.out.println("couldn't make holly into a buffered image!");
                e.printStackTrace();
            }
        }

        // make the sprite appear in the correct location
        AffineTransform t = new AffineTransform();
        t.translate(super.getPos().getX(), super.getPos().getY());
        t.scale(1, 1);

        AffineTransform tb = new AffineTransform();
        tb.translate(super.getPos().getX() + super.getSize().width, super.getPos().getY());
        tb.scale(-1, 1);

        if (!super.charCanAttack()) {
            if (super.getCurrentFacingDir() == FacingDir.LEFT) {
                g.drawImage(attackingSprite, tb, null);
            } else {
                g.drawImage(attackingSprite, t, null);
            }
        } else {
            // standing still conditions
            if (super.getXVelocity() == 0 && super.getOnGround()) {
                if (super.getCurrentFacingDir() == FacingDir.LEFT) {
                    g.drawImage(standingSprite, tb, null);
                } else {
                    g.drawImage(standingSprite, t, null);
                }
            }
            // walking conditions
            else if (super.getXVelocity() > 0 && super.getOnGround()) {
                if (currentWalkSprite == 1) {
                    g.drawImage(walkingSprite1, t, null);
                } else if (currentWalkSprite == 2) {
                    g.drawImage(walkingSprite2, t, null);
                }
            } else if (super.getXVelocity() < 0 && super.getOnGround()) {
                if (currentWalkSprite == 1) {
                    g.drawImage(walkingSprite1, tb, null);
                } else if (currentWalkSprite == 2) {
                    g.drawImage(walkingSprite2, tb, null);
                }
            }
            // jumping conditions
            else if (!super.getOnGround() && super.getXVelocity() > 0) {
                g.drawImage(inAirSprite, t, null);
            } else if (!super.getOnGround() && super.getXVelocity() < 0) {
                g.drawImage(inAirSprite, tb, null);
            } else if (!super.getOnGround()) {
                if (super.getCurrentFacingDir() == FacingDir.LEFT) {
                    g.drawImage(inAirSprite, tb, null);
                } else {
                    g.drawImage(inAirSprite, t, null);
                }
            }
        }

    }

    @Override
    public void lightAttack() {

        super.lightAttack();
    }

    @Override
    public void heavyAttack() {

        super.heavyAttack();
    }

}
