package outsidergame.entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.studiohartman.jamepad.ControllerState;

import outsidergame.Launcher;
import outsidergame.controls.ControlsManager;
import outsidergame.controls.GamepadControllable;
import outsidergame.game.GameCanvas;
import outsidergame.util.MathUtil;

public abstract class CharacterBase implements GamepadControllable {
    public static enum FacingDir {
        LEFT, RIGHT
    }
    private FacingDir currentFacingDir = FacingDir.RIGHT;
    private final double speedMultiplier = 0.5;
    private final double gravity = 0.2;
    private Point2D pos = null;
    private int health = 0;
    private short lives = 3;
    private Rectangle characterSize = null;
    private boolean lastAState = false;
    private float lastLeftTriggerState = 0, lastRightTriggerState = 0;
    private double curTime = 0;
    private double lastTime = 0;
    private double lastLightAttackTime = 0, lastHeavyAttackTime = 0, lastJumpTime = 0;
    private short curConsecutiveJumps = 0;
    private double xVelocity = 0, yVelocity = 0;
    private double xAccel = 0, yAccel = 0;
    private ArrayList<Double> xAccelModifiers = new ArrayList<Double>();
    private double controlsXAccel = 0;
    private ArrayList<Double> yAccelModifiers = new ArrayList<Double>();
    private boolean onGround = false;
    private boolean canAttack = true;
    public boolean dead = false;
    public boolean opponentDead = false;

    public CharacterBase(Point2D pos, Rectangle characterSize, int controller) {
        this.pos = pos;
        this.health = 0;
        this.characterSize = characterSize;

        ControlsManager.getInstance().registerControllableClass(this, controller);
    }

    /**
     * this method should be run every loop
     */
    public void update(double curTime) {
        if (!dead) {
            this.curTime = curTime;

            if (this.lives <= 0) {
                dead = true;
            }

            // split up some of this into separate methods to keep this function from
            // becoming too massive
            calcAccels();
            checkOnGround();
            addFriction();

            // check to see if the character is dead
            if (GameCanvas.size.height <= this.pos.getY() + this.characterSize.getHeight()) {
                lives--;
                this.xAccel = 0;
                this.yAccel = 0;
                this.yAccelModifiers.clear();
                this.xAccelModifiers.clear();
                this.xVelocity = 0;
                this.yVelocity = 0;
                this.health = 0;
                this.setPos(new Point2D.Double(GameCanvas.size.width / 2, GameCanvas.size.height / 2));
            }

            changePos(xVelocity * (curTime - lastTime), yVelocity * (curTime - lastTime));

            xVelocity += xAccel * (curTime - lastTime);
            yVelocity += yAccel * (curTime - lastTime);

            // reset number of jumps if on ground
            if (onGround && curTime - lastJumpTime >= 10) {
                curConsecutiveJumps = 0;
            }

            // make falling off of the floor one of the jumps
            if (!onGround && curConsecutiveJumps == 0) {
                curConsecutiveJumps = 1;
            }

            // record what direction the character is currently facing when moving on the x
            // axis
            // if (xVelocity > 0.3 || xVelocity < -0.3) {
            // if (xVelocity > 0) {
            // currentFacingDir = FacingDir.RIGHT;
            // } else {
            // currentFacingDir = FacingDir.LEFT;
            // }
            // }

            lastTime = curTime;
        }
    }

    public void calcAccels() {
        xAccel = 0;
        yAccel = 0;

        // copy the array lists to arrays to prevent them from changing while being ran through
        Double[] xAccelModifiersCopy = new Double[xAccelModifiers.size()];
        Double[] yAccelModifiersCopy = new Double[yAccelModifiers.size()];
        xAccelModifiersCopy = xAccelModifiers.toArray(xAccelModifiersCopy);
        yAccelModifiersCopy = yAccelModifiers.toArray(yAccelModifiersCopy);

        for (double val : xAccelModifiersCopy) {
            xAccel += val;
        }
        xAccel += controlsXAccel;

        for (double val : yAccelModifiersCopy) {
            yAccel += val;
        }
        // yAccel += controlsYAccel;

        // clear accel modifiers
        xAccelModifiers.clear();
        yAccelModifiers.clear();
        if (this.pos.getY() < GameCanvas.size.getHeight()
                || (this.pos.getY() + this.characterSize.getHeight() < GameCanvas.floor.getPos().getY()
                        && this.pos.getX() + this.characterSize.getWidth() >= GameCanvas.floor.getPos().getX()
                        && this.pos.getX() <= GameCanvas.floor.getPos().getX()
                                + GameCanvas.floor.getSize().getWidth())) {
            yAccelModifiers.add(gravity);
        }
    }

    public void checkOnGround() {
        if (this.pos.getX() + this.characterSize.width >= GameCanvas.floor.getPos().getX() - 2
                && this.pos.getX() <= GameCanvas.floor.getPos().getX() + GameCanvas.floor.getSize().getWidth() + 2
                && this.pos.getY() + this.characterSize.height >= GameCanvas.floor.getPos().getY() - 2
                && this.pos.getY() <= GameCanvas.floor.getPos().getY() + GameCanvas.floor.getSize().getHeight() + 2) {
            onGround = true;
        } else {
            onGround = false;
        }
    }

    public void addFriction() {
        final double onGroundBand = 0.5;
        final double onGroundAdjustment = 0.3;

        final double inAirBand = 0.5;
        final double inAirAdjustment = 0.2;


        if (onGround) {
            if (xVelocity > onGroundBand) {
                xAccelModifiers.add(-onGroundAdjustment);
            } else if (xVelocity < -onGroundBand) {
                xAccelModifiers.add(onGroundAdjustment);
            } else if (xVelocity > -onGroundBand && xVelocity < onGroundBand) {
                xVelocity = 0;
            }
        } else {
            if (xVelocity > inAirBand) {
                xAccelModifiers.add(-inAirAdjustment);
            } else if (xVelocity < -inAirBand) {
                xAccelModifiers.add(inAirAdjustment);
            }
        }
    }

    public void draw(Graphics2D g) {
        // Rectangle2D rect = new Rectangle2D.Double(pos.getX(), pos.getY(), characterSize.height, characterSize.width);
        // g.draw(rect);
    }

    @Override
    public void getGamepadState(ControllerState state) {
        handleInput(state);
    }

    public void handleInput(ControllerState state) {

        if (!dead) {
            // check to see if attack cooldowns have finished
            if (curTime - lastHeavyAttackTime >= 45 && curTime - lastLightAttackTime >= 60 / 3) {
                canAttack = true;
            } else {
                canAttack = false;
            }

            if (state.leftStickMagnitude >= 0.3 || state.leftStickMagnitude <= -0.3) {
                if (state.leftStickAngle <= 90 && state.leftStickAngle >= -90) {
                    currentFacingDir = FacingDir.RIGHT;
                } else {
                    currentFacingDir = FacingDir.LEFT;
                }
            }

            float movementDir = (float) Math.toRadians(state.leftStickAngle);
            float movementMag = state.leftStickMagnitude;

            float xDir = (float) (movementMag * Math.cos(movementDir));

            double finalXDir;

            if (xDir >= 0.3f || xDir <= -0.3) {
                finalXDir = xDir * speedMultiplier;
            } else {
                finalXDir = 0;
            }

            controlsXAccel = finalXDir;

            // allow for jumping
            if (state.a && state.a != lastAState) {
                jump();
            }

            // bind y to heavy attack as defined in the characters own class
            if (state.rightTrigger >= 0.3f && lastRightTriggerState < 0.3f && canAttack) {
                heavyAttack();
                lastHeavyAttackTime = curTime;
            }

            // bind x to light attack as defined in the character's own class
            if (state.leftTrigger >= 0.3f && lastLeftTriggerState < 0.3f && canAttack) {
                lightAttack();
                lastLightAttackTime = curTime;
            }

            if (opponentDead && state.back) {
                this.opponentDead = false;
                Launcher.gameCanvas.resetGame();
            }

            lastAState = state.a;
            lastLeftTriggerState = state.leftTrigger;
            lastRightTriggerState = state.rightTrigger;
        }
    }

    public void setPos(Point2D pos) {
        this.pos = pos;
    }

    public void changePos(double deltaX, double deltaY) {
        // System.out.println("deltaX/deltaY: " + deltaX + "/" + deltaY);
        if (this.pos.getX() + this.characterSize.width + deltaX >= GameCanvas.floor.getPos().getX()
                && this.pos.getX() + deltaX <= GameCanvas.floor.getPos().getX() + GameCanvas.floor.getSize().getWidth()
                && this.pos.getY() + this.characterSize.height + deltaY >= GameCanvas.floor.getPos().getY()
                && this.pos.getY() + deltaY <= GameCanvas.floor.getPos().getY()
                        + GameCanvas.floor.getSize().getHeight()) {

            // System.out.println("collision detected with a character and the floor!");

            if (this.pos.getX() + this.characterSize.width + deltaX >= GameCanvas.floor.getPos().getX()
                    && this.pos.getX() <= GameCanvas.floor.getPos().getX()
                    && this.pos.getY() > GameCanvas.floor.getPos().getY()
                    && this.pos.getY() < GameCanvas.floor.getPos().getY() + GameCanvas.floor.getSize().height) {
                this.pos.setLocation(GameCanvas.floor.getPos().getX() - this.characterSize.width, this.pos.getY());
                // System.out.println("corrected left X!");
            } else if (this.pos.getX() + deltaX <= GameCanvas.floor.getPos().getX() + GameCanvas.floor.getSize().width
                    && this.pos.getX() + this.characterSize.width >= GameCanvas.floor.getPos().getX()
                            + GameCanvas.floor.getSize().width
                    && this.pos.getY() > GameCanvas.floor.getPos().getY()
                    && this.pos.getY() < GameCanvas.floor.getPos().getY() + GameCanvas.floor.getSize().height) {
                this.pos.setLocation(GameCanvas.floor.getPos().getX() + GameCanvas.floor.getSize().width,
                        this.pos.getY());
                // System.out.println("corrected right X!");
            } else {
                this.pos.setLocation(this.pos.getX() + deltaX, this.pos.getY() + deltaY);
            }

            if (this.pos.getY() + this.characterSize.height + deltaY >= GameCanvas.floor.getPos().getY()
                    && this.pos.getY() <= GameCanvas.floor.getPos().getY()
                    && this.pos.getX() + this.characterSize.width > GameCanvas.floor.getPos().getX()
                    && this.pos.getX() < GameCanvas.floor.getPos().getX() + GameCanvas.floor.getSize().width) {
                this.pos.setLocation(this.pos.getX(), GameCanvas.floor.getPos().getY() - this.characterSize.height);
                if (this.yVelocity > 0) {
                    // stop falling
                    this.yVelocity = 0;
                }
                // System.out.println("corrected top Y!");
            } else if (this.pos.getY() + deltaY <= GameCanvas.floor.getPos().getY() + GameCanvas.floor.getSize().height
                    && this.pos.getY() >= GameCanvas.floor.getPos().getY() + GameCanvas.floor.getSize().height
                    && this.pos.getX() + this.characterSize.width > GameCanvas.floor.getPos().getX()
                    && this.pos.getX() < GameCanvas.floor.getPos().getX() + GameCanvas.floor.getSize().width) {
                this.pos.setLocation(this.pos.getX(),
                        GameCanvas.floor.getPos().getY() + GameCanvas.floor.getSize().height);
                // System.out.println("corrected bottom Y!");
            } else {
                this.pos.setLocation(this.pos.getX(), this.pos.getY() + deltaY);
            }

        } else {
            if (this.pos.getX() + deltaX < 0) {
                this.pos.setLocation(0, pos.getY());
                // bounce when you hit a wall
                if (this.xVelocity < 0) {
                    this.xVelocity *= -0.8;
                }
            } else if (this.pos.getX() + deltaX + characterSize.getWidth() > GameCanvas.size.getWidth()) {
                this.pos.setLocation(GameCanvas.size.getWidth() - characterSize.getWidth(), pos.getY());
                // bounce when you hit a wall
                if (this.xVelocity > 0) {
                    this.xVelocity *= -0.8;
                }
            } else {
                this.pos.setLocation(pos.getX() + deltaX, pos.getY());
            }

            if (this.pos.getY() + deltaY < 0) {
                this.pos.setLocation(pos.getX(), 0);
                // prevent yVelocity form becoming insane while hitting the ceiling by bouncing off of it
                if (this.yVelocity < 0) {
                    this.yVelocity *= -1;
                }
            } else if (this.pos.getY() + deltaY + characterSize.getHeight() > GameCanvas.size.getHeight()) {
                this.pos.setLocation(pos.getX(), GameCanvas.size.getHeight() - characterSize.getHeight());
                // prevent yVelocity from becoming insane while stuck on the floor
                if (this.yVelocity > 0) {
                    this.yVelocity = 0;
                }
            } else {
                this.pos.setLocation(pos.getX(), pos.getY() + deltaY);
            }
        }

    }

    public Point2D getPos() {
        return pos;
    }

    public Point2D getCenter() {
        double centerX = this.pos.getX() + this.getSize().width / 2;
        double centerY = this.pos.getY() + this.getSize().height / 2;

        return new Point2D.Double(centerX, centerY);
    }

    public void takeDamage(int damage, CharacterBase fromObj) {
        damage = Math.abs(damage);

        float objAng = (float) Math.toRadians(MathUtil.clampAng(Math.toDegrees(Math.atan2(fromObj.getCenter().getY() - this.getCenter().getY(),
                fromObj.getCenter().getX() - this.getCenter().getX()) + Math.PI)));

        double xAccelMod = damage * (health / 100) * Math.cos(objAng) / 3;
        double yAccelMod = damage * (health / 100) * Math.sin(objAng) * 2;

        if (xAccelMod < 2 && xAccelMod > -2) {
            if (Math.toDegrees(objAng) <= 90 && Math.toDegrees(objAng) >= -90) {
                xAccelMod = 2;
            } else {
                xAccelMod = -2;
            }
        }
        if (yAccelMod < 2 && yAccelMod > -2) {
            if (Math.toDegrees(objAng) >= 0 && Math.toDegrees(objAng) <= 180) {
                yAccelMod = 4;
            } else {
                yAccelMod = -4;
            }
        }

        xAccelModifiers.add(xAccelMod);
        yAccelModifiers.add(yAccelMod);

        System.out.println("x/y accel mods: " + xAccelModifiers.get(xAccelModifiers.size() - 1) + "/" + yAccelModifiers.get(yAccelModifiers.size() - 1));
        System.out.println("angle: " + Math.toDegrees(objAng));

        health += damage;
    }

    public void healBy(int amount) {
        amount = Math.abs(amount);
        if (health - amount <= 0) {
            health = 0;
        } else {
            health -= amount;
        }
    }

    public int getHealth() {
        return health;
    }

    public Rectangle getSize() {
        return this.characterSize;
    }

    public double getXVelocity() {
        return this.xVelocity;
    }

    public double getYVelocity() {
        return this.yVelocity;
    }

    public boolean getOnGround() {
        return this.onGround;
    }

    public FacingDir getCurrentFacingDir() {
        return this.currentFacingDir;
    }

    public boolean charCanAttack() {
        return canAttack;
    }

    public short getLives() {
        return lives;
    }

    public void jump() {
        if (curConsecutiveJumps < 2) {
            yAccelModifiers.add(-10.0);
            curConsecutiveJumps++;
            lastJumpTime = curTime;
        }
    }

    public void lightAttack() {
        for (CharacterBase charBase : GameCanvas.characters) {
            final double range = 23;

            double myX = this.pos.getX();
            double myY = this.pos.getY();

            double enemyX = charBase.getPos().getX();
            double enemyY = charBase.getPos().getY();

            // make sure you can't hit yourself
            if (charBase != this && Math.abs(myY - enemyY) <= characterSize.getHeight()/2) {
                // handle hits when on the left of the enemy
                if (enemyX > myX && enemyX < myX + range*2 && this.currentFacingDir == FacingDir.RIGHT) {
                    charBase.takeDamage(20, this);
                }
                // handle hits when on the right of the enemy
                else if (enemyX <= myX && enemyX + charBase.getSize().width > myX - range/3 && this.currentFacingDir == FacingDir.LEFT) {
                    charBase.takeDamage(20, this);
                }
            }
        }
    }

    public void heavyAttack() {
        for (CharacterBase charBase : GameCanvas.characters) {
            if (this.currentFacingDir == FacingDir.LEFT) {
                xAccelModifiers.add(-5.0);
            } else {
                xAccelModifiers.add(5.0);
            }

            while (curTime - lastHeavyAttackTime < 20) {

            }

            final double range = 40;

            double myX = this.pos.getX();
            double myY = this.pos.getY();

            double enemyX = charBase.getPos().getX();
            double enemyY = charBase.getPos().getY();

            // make sure you can't hit yourself
            if (charBase != this && Math.abs(myY - enemyY) <= characterSize.getHeight()/2) {
                // handle hits when on the left of the enemy
                if (enemyX > myX && enemyX < myX + range*2 && this.currentFacingDir == FacingDir.RIGHT) {
                    charBase.takeDamage(50, this);
                }
                // handle hits when on the right of the enemy
                else if (enemyX <= myX && enemyX + charBase.getSize().width > myX - range/3 && this.currentFacingDir == FacingDir.LEFT) {
                    charBase.takeDamage(50, this);
                }
            }
        }
    }

}
