package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;


//player class same as sub 1
//just added shield + moudularised
//NOW WITH HORIZONTAL FLIPPING WHEN CHANGING DIRECTION

public class Player extends Walker {

    private static final Shape shapl1 = new BoxShape(0.5f, 1f);   //level 1 original
    //private static final Shape SHAPE_L2 = new BoxShape(0.5f, 1.8f);   //level 2 a bit bigger

    private int lives;
    private int score = 0;
    private boolean shielded = false;   //when true = next barrel hit is absorbed
    //private boolean hearted = false;   //when true = +1 heart

    private float speedMultiplier = 1;

    private float imageSize;

    //jump fields
    private int jumpCount = 0;
    private boolean jumpCooldown = false;

    //movement lock field
    private boolean movementLocked = false;

    //animation fields
    private String idleImage;
    private String idleImageFlipped;  //flipped version
    private String[] walkFrames;
    private String[] walkFramesFlipped;  //flipped versions
    private int walkFrame = 0;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private javax.swing.Timer walkAnimTimer;
    private boolean facingLeft = false;

    //default constructor
    //public Player(World world) {
    //    this(world, "data/playerNoBKG.png", 5);
    //}

    //custom image + default 5 lives
    //public Player(World world, String imagePath) {
    //    this(world, imagePath, 5, shapl1);
    //}


    //default
    public Player(World world, String imagePath, int startingLives, Shape shape) {
        this(world, imagePath, startingLives, shape, 3.5f); //default size
    }

    //level 3 default player const
    public Player(World world, String imagePath, int startingLives) {
        this(world, imagePath, startingLives, shapl1, 3.0f); //default size 3.0f
    }

    //level 1 + 2 custom size + walk anim
    public Player(World world, String imagePath, int startingLives, Shape shape, float imageSize) {
        super(world, shape);
        this.lives = startingLives;
        this.idleImage = imagePath;
        this.imageSize = imageSize; //store new size

        for (Fixture f : getFixtureList()) {
            if (f instanceof SolidFixture) {
                SolidFixture sf = (SolidFixture) f;
                sf.setFriction(3);
                sf.setDensity(2f);
                sf.setRestitution(0.0f);  //player characterisirtcs set locally
            }
        }

        walkFrames = new String[]{  //gets playerimage _searches for image w playerimage+_walk1
                imagePath,
                deriveWalkFrame(imagePath, "_walk1"),
        };

        //flipped versions - expect files with _LEFT suffix
        idleImageFlipped = deriveFlippedPath(imagePath);
        walkFramesFlipped = new String[]{
                idleImageFlipped,
                deriveWalkFrame(idleImageFlipped, "_walk1"),
        };

        addImage(new BodyImage(idleImage, imageSize));

        walkAnimTimer = new javax.swing.Timer(200, e -> updateWalkFrame());
        walkAnimTimer.start();
    }

    //builds walk frame path from base image path
    //eg data/level2_character2.png = data/level2_character2_walk1.png
    private String deriveWalkFrame(String base, String suffix) {
        int dot = base.lastIndexOf('.');
        if (dot == -1) return base;
        return base.substring(0, dot) + suffix + base.substring(dot);
    }

    //builds flipped image path
    //eg data/playerNoBKG.png = data/playerNoBKG_LEFT.png
    private String deriveFlippedPath(String base) {
        int dot = base.lastIndexOf('.');
        if (dot == -1) return base;
        return base.substring(0, dot) + "_LEFT" + base.substring(dot);
    }

    private void updateWalkFrame() {  //swap img when walking or not walking
        boolean isMoving = isMovingLeft || isMovingRight;  //only anim when walking, not when still

        if (!isMoving) {
            if (walkFrame != 0) {
                walkFrame = 0;
                applyFrame();
            }
            return;
        }

        walkFrame = (walkFrame + 1) % walkFrames.length;
        applyFrame();
    }

    private void applyFrame() {  //apply frame with flipping based on direction
        removeAllImages();
        String imagePath = facingLeft ? walkFramesFlipped[walkFrame] : walkFrames[walkFrame];
        addImage(new BodyImage(imagePath, imageSize));
    }


    //general methods
    public void jump() {
        if (movementLocked) return;
        if (jumpCooldown) return;

        if (jumpCount < 2) {
            float impulse = boosted ? 220 : 150;   // boots give a stronger jump - condition ? valueIfTrue : valueIfFalse
            applyImpulse(new Vec2(0, impulse));
            jumpCount++;

            if (jumpCount == 2) {
                jumpCooldown = true;
                javax.swing.Timer t = new javax.swing.Timer(750, e -> {
                    jumpCount = 0;
                    jumpCooldown = false;
                });
                t.setRepeats(false);
                t.start();
            }
        }
    }

    // public void resetJumps() {
    //     if (!jumpCooldown) jumpCount = 0;
    // }

    public void moveLeft() {  //if moving left
        if (movementLocked) return;
        isMovingLeft = true;
        isMovingRight = false;
        facingLeft = true;
        startWalking(-8 * speedMultiplier);
    }

    public void moveRight() {  //move right
        if (movementLocked) return;
        isMovingRight = true;
        isMovingLeft = false;
        facingLeft = false;
        startWalking(8 * speedMultiplier);
    }

    public void stopMoving() {  //stop moving
        isMovingLeft = false;
        isMovingRight = false;
        stopWalking();
    }

    public void setMovementLocked(boolean locked) {  //mov locked for start of level
        movementLocked = locked;
        if (locked) {
            isMovingLeft = false;
            isMovingRight = false;
            stopWalking(); //stop immediately when locked
        }
    }

    public void setSpeedMultiplier(float multiplier) {
        this.speedMultiplier = multiplier;
    }  //sprit multiplier

    public void loseLife() {
        lives--;
    }  //loselife
    public int getLives() {
        return lives;
    } //return lives

    public void addScore(int value) {
        score += value;
    } //add score
    public int getScore() {
        return score;
    }  //return score

    //shield methods from l2 - if still got shield
    public void activateShield() {
        shielded = true;
    }
    public boolean hasShield() {
        return shielded;
    }

    public boolean consumeShield() {
        if (shielded) { shielded = false; return true; }
        return false;
    }

    public void activateHeart() {
        lives++;
    }

    private boolean boosted = false;

    public void activateBoots() {
        boosted = true;
    }

    public boolean hasBoot() {
        return boosted;
    }
}
