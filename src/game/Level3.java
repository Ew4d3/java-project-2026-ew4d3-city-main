package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

//Level 3 — Moving platforms carries lives from Level 2

public class Level3 extends GameLevel {

    //priv declared local images. so can be overrided in level 2 , 3 etc
    private String playerImage = "data/level3_playerNoBKG.png";
    private String donkeyImage = "data/level3_donkeyNoBKG.png";
    private String princessImage = "data/adil_in_distressNoBKG.png";
    private String barrelImage = "data/lvl3_magma.png";
    private String backgroundImage = "data/black.png";
    private Shield shield;
    private ArrayList<Heart> hearts = new ArrayList<>();  //tracks all hearts, replaces single heart field
    private BootsPickup boots; //jump boost boots lvl3

    private boolean shieldAlive = true;
    private boolean heartAlive = true;

    //sound per level
    //private static final String BGM = "data/dk_Lvl_supCel.wav";
    //private static final String BGM = "data/HS_bkgM.wav";
    private static final String BGM = "data/PR_dk_Space_Lvl3.wav";
    private static final String SFX_WIN = "data/level_complete.wav";

    //game init
    private int startingLives;

    private ArrayList<DynamicBody> movingPlatforms = new ArrayList<>();
    private ArrayList<Float> movingPlatformSpeeds = new ArrayList<>();
    private ArrayList<Float> movingPlatformMinY = new ArrayList<>();
    private ArrayList<Float> movingPlatformMaxY = new ArrayList<>();

    private static final float WIN_X_MIN = -10.5f;
    private static final float WIN_X_MAX = -8.5f;
    private static final float WIN_Y_MIN = 14.5f;

    //constructors
    public Level3(String playerImage, String donkeyImage, String princessImage,
                  String barrelImage, String backgroundImage, int startingLives,
                  SoundPlayer soundPlayer) {
        this.playerImage = playerImage;
        this.donkeyImage = donkeyImage;
        this.princessImage = princessImage;
        this.barrelImage = barrelImage;
        this.backgroundImage = backgroundImage;
        this.startingLives = 5;
        this.soundPlayer = soundPlayer;
        buildWorld();
    }

    private void buildWorld() {  //build world + charactersitcs again - plats etc
        setGravity(30);

        //music call
        soundPlayer.playBKG_music(BGM);

        createStaticPlatform(-20,3,90,25);//wall
        createStaticPlatform(20f,3, 90,25);//wall
        createStaticPlatform(3,20f,0);//celingh
        createStaticPlatform(-3,20f,0);//celing
        createStaticPlatform(-9.5f, 14.5f, 0, 1.7f);  //princess

        createStaticPlatform(-12f,-18,0,2.5f); //spawn platform player
        createStaticPlatform(-15.2f,12f,0,1.5f); //donkey platform


        new MovingPlatform(this, -6, 5.5f, 0, 3.5f, 0.05f, false); //1st moving next to donkey - top
        new MovingPlatform(this, 0, -2f, 0, 3.5f, 0.065f, false);  //2nd mov plat - middle
        new MovingPlatform(this, 6, -13, 0, 4, 0.06f, false); //3rd mov plat - bottom by player

        new MovingPlatform(this, 6.5f, 6.5f, 0, 3.5f, 0.055f, true); //1st hor plat - top
        MovingPlatform crumblingPlat = new MovingPlatform(this, 13.5f, 0, 0, 3.5f, 0.065f, true); //2nd hor plat - far right


        crumblingPlat.addCollisionListener(new CollisionListener() {
            private javax.swing.Timer destroyTimer = null;

            @Override
            public void collide(CollisionEvent e) {  //if player tocuhes platform, breaks after 3s and plays sound
                if (e.getOtherBody() == player && destroyTimer == null) {
                    destroyTimer = new javax.swing.Timer(3000, evt -> {
                        soundPlayer.playSFX("data/rock_break.wav");
                        crumblingPlat.destroy();
                    });
                    destroyTimer.setRepeats(false);
                    destroyTimer.start();
                }
            }
        });
        new MovingPlatform(this, -11, -2, 0, 3f, 0.07f, true); //3rd hor plat - far left

        createStaticPlatform(-18f,-15,356.5f,2.5f); //far left bottom
        createStaticPlatform(-18f,-7,330,2.5f); //angled above far left middle
        //createStaticPlatform(-17f,6.5f,330,2.5f); //angled above far left top
        //createStaticPlatform(-18f,10f,320,2.6f); //larger angle left top
        createStaticPlatform(-17.5f,2f,320,2.65f); //larger angle left middle
        createStaticPlatform(-11.5f,10.2f,320,2.65f); //larger angle left top by donkey
        createStaticPlatform(-5f,-14,20,2.5f); //right of spawn angled
        createStaticPlatform(13f,-9,45,2.5f); //steep angle bopttom far right
        createStaticPlatform(18f,-5,3.5f,2.5f); //flat above far right middle
        createStaticPlatform(5.5f,0.5f,45,1.5f); //angled down end of right hor mov plat v ery small one
        createStaticPlatform(16.5f,6.5f,15,3f); //far right slanted against wall
        createStaticPlatform(17.8f,13.5f,45,3f); //top right corner angle
        createStaticPlatform(6f,14.5f,315,2.5f); //opposite top right coner nagle
        createStaticPlatform(-11f,-8,20,2.5f); //triangle 1 right - left plat
        createStaticPlatform(-8.2f,-8.5f,285,1.5f); //trianle 1 right plat
        createStaticPlatform(0,-19.75f,0,20); //bottom platform

        player = new Player(this, "data/level3_playerNoBKG.png", 5);
        player.setPosition(new Vec2(-12, -13));
        player.setMovementLocked(true); //lock on spawn
        javax.swing.Timer unlockTimer = new javax.swing.Timer(2500, e -> {
            player.setMovementLocked(false); //unlock after 2.5f s
        });

        shield = new Shield(this, 5.25f, 1.7f);

        hearts.add(new Heart(this, 16.75f, 9f));  // top right heart
        hearts.add(new Heart(this, -17f, -4f));  //second btm left  heart
        hearts.add(new Heart(this, 6, -15.5f));  //second btm left  heart

        boots = new BootsPickup(this, -5f, -12f); //boots

        unlockTimer.setRepeats(false);
        unlockTimer.start();

        SolidFixture sf = new SolidFixture(player, new BoxShape(0.85f, 1.7f), 1.50f);
        sf.setFriction(3);  //friction
        player.addCollisionListener(new PlayerCollision(player, soundPlayer));
        //player.removeAllImages();
        //player.addImage(new BodyImage("data/level3_playerNoBKG.png", 3f));


        //create donkey
        StaticBody donkey = new StaticBody(this, new BoxShape(1.5f, 2f));
        donkey.setPosition(new Vec2(-15.5f, 14.48f));
        //donkey.addImage(new BodyImage("data/level3_donkeyNoBKG_1.png", 4));
        donkey.addImage(new BodyImage("data/alien_level3_.png", 6.65f));


        addStepListener(new StepListener() {
            int counter = 0;
            @Override
            public void preStep(StepEvent e) {
                counter++;
                if (counter % 100 == 0) spawnBarrel();  //thorws barrels every 1ish seconds, same as level 2

                checkFall();
                cleanBarrels();

                if (shieldAlive && shield != null) shieldAlive = false;

                //if (heartAlive && hearts.stream().noneMatch(Body::isActive)) heartAlive = false;

                //animate moving platforms
                for (int i = 0; i < movingPlatforms.size(); i++) {
                    DynamicBody mp = movingPlatforms.get(i);
                    float speed = movingPlatformSpeeds.get(i);
                    float minY = movingPlatformMinY.get(i);
                    float maxY = movingPlatformMaxY.get(i);
                    float cy = mp.getPosition().y;

                    if (cy <= minY && speed < 0) {
                        movingPlatformSpeeds.set(i, Math.abs(speed));
                        speed = Math.abs(speed);
                    } else if (cy >= maxY && speed > 0) {
                        movingPlatformSpeeds.set(i, -Math.abs(speed));
                        speed = -Math.abs(speed);
                    }
                }

                checkWin(WIN_X_MIN, WIN_X_MAX, WIN_Y_MIN);
            }
            @Override public void postStep(StepEvent e) {}
        });
    }

    //moving platty init
    //private void addMovingPlatform(float x, float centerY, float minY, float maxY, float halfWidth) {
    //    DynamicBody mp = new DynamicBody(this, new BoxShape(halfWidth, 0.15f));
    //    mp.setPosition(new Vec2(x, centerY));
    //    mp.setGravityScale(0);
    //    mp.setLinearVelocity(new Vec2(0, 1.5f));
    //    mp.setAngleDegrees(0); mp.setAngularVelocity(0);
    //    mp.setFillColor(new java.awt.Color(200, 120, 40));
    //    mp.setLineColor(new java.awt.Color(100, 60, 20));
    //    movingPlatforms.add(mp);
    //     movingPlatformSpeeds.add(1.5f);//    movingPlatformMinY.add(minY);
    //    movingPlatformMaxY.add(maxY);
    //}

    //game meths
    private void spawnBarrel(){  //barrel spawn w custom shape + img + speed etc
        //Barrel b = new Barrel(this, "data/lvl3_magma.png"); //10 minimum, 100 max, random between 1 and 90 added onto min 10
        Barrel b = new Barrel(this);

        SolidFixture sf = new SolidFixture(b, new CircleShape(1f));
        sf.setRestitution(0.56f);
        sf.setFriction(0.2f);

        b.addImage(new BodyImage("data/magma2_lvl3_.png", 2.5f));

        float randomSpeed = 10 + (float)(Math.random() * 60);
        float direction = Math.random() < 0.75 ? 1 : -1;        //want more right than left so 75% go right - condition ? valueIfTrue : valueIfFalse
        b.setLinearVelocity(new Vec2(randomSpeed * direction, 0));
        barrels.add(b);
    }

    private void createStaticPlatform(float x, float y, float angle, float halfWidth) {  //main statci plat meth
        StaticBody p = new StaticBody(this, new BoxShape(halfWidth, 0.1f));
        p.setPosition(new Vec2(x, y)); p.setAngleDegrees(angle);

        try {  //same as lvl 1+2
            //load and stretch image to match platform width

            //BufferedImage original = ImageIO.read(new File("data/mainplat2.png"));   //bamboo plat
            BufferedImage original = ImageIO.read(new File("data/lvl3_platforms.png"));  //palm tree trunk plat

            int pixelWidth = (int)(halfWidth * 2 * 25); //calc size needed for plat , 25pixels per world
            int pixelHeight = (int)(2f * 25);//reduced from 3.5f thinner than level 2 x 25 pixels
            java.awt.Image scaled = original.getScaledInstance(pixelWidth, pixelHeight, java.awt.Image.SCALE_SMOOTH);//built in meth to resize given mages using params, calculated widths from above line
            BufferedImage resized = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);//create empty img so file can be saved
            resized.getGraphics().drawImage(scaled, 0, 0, null);//draw resize img into empty omg above

            //save temp file and load as BodyImage
            File temp = new File("data/Lv3_platform_resizeHW" + halfWidth + ".png"); //save as file+suffix
            ImageIO.write(resized, "png", temp);//as a png
            p.addImage(new BodyImage(temp.getPath(), 2.5f));//match the height above - attach to platforms
        } catch (Exception e) {//if sum wrong, error msg
            e.printStackTrace();
        }

    }

    //set size static plat
    private void createStaticPlatform(float x, float y, float angle) {  // plats for world barriers
        createStaticPlatform(x, y, angle, 17);
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isComplete() {
        return levelComplete;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }
    public String getPrincessImage(){
        return princessImage;
    }
    public boolean isLevelComplete() {
        return levelComplete;
    }
    public static float getWinXMin() {
        return WIN_X_MIN;
    }
    public static float getWinXMax() {
        return WIN_X_MAX;
    }
    public static float getWinYMin() {
        return WIN_Y_MIN;
    }
    public boolean isShieldAlive(){
        return shieldAlive;
    }
    public boolean isHeartAlive(){
        return heartAlive;
    }

    @Override protected float getFallThreshold() {
        return -17.5f;
    }
    @Override protected Vec2 getRespawnPoint() {
        return new Vec2(-12, -15.5f);
    }
    @Override protected String getWinSFX() {
        return SFX_WIN;
    }
}