package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.*;
import java.util.ArrayList;

//level 2 , similar to level 1 just set same bkg, player, donkey etc for now as havent designed images yet. juts made thow faster and less lives. different music too
// just added shield as a collectable


public class Level2 extends GameLevel {  //extends game level abstract class

    //priv declared local images. so can be overrided in level 2 , 3 etc
    private String playerImage = "data/level2_character2.png";
    private String donkeyImage = "data/lifeguard2_lvl2.png";
    private String princessImage = "data/rastaJoe.png";
    private String barrelImage = "data/lifering_lv2.png";
    private String backgroundImage = "data/skyscraperBKG.PNG";

    //sound per level
    private static final String BGM = "data/Temp_level2_BKG.wav";
    private static final String SFX_WIN = "data/level_complete.wav";

    //game init
    private Shield shield;
    private boolean shieldAlive = true;
    private int startingLives;  //= player.getLives();
    //= 5;

    private static final float WIN_X_MIN = -12.5f;
    private static final float WIN_X_MAX = -6.5f;
    private static final float WIN_Y_MIN = 14.5f;

    //constructors
   // public Level2(SoundPlayer soundPlayer) {
   //     this.soundPlayer = soundPlayer;
   //     buildWorld();
   // }

    public Level2(String playerImage1, String donkeyImage, String princessImage,
                  String barrelImage, SoundPlayer soundPlayer, String backgroundImage, int startingLives) {
        this.playerImage = playerImage;
        this.donkeyImage = donkeyImage;
        this.princessImage = princessImage;
        this.barrelImage = barrelImage;
        this.soundPlayer = soundPlayer;
        this.backgroundImage = backgroundImage;
        this.startingLives = startingLives;
        buildWorld();
    }  //level 2 params from abst class

    private void buildWorld() {  //all things in world
        setGravity(30);

        //music call
        soundPlayer.playBKG_music(BGM);

        //StaticBody ground = new StaticBody(this, new BoxShape(13.5f, 0.1f));
        //ground.setPosition(new Vec2(0, -15));

        createPlatform(0f,-15.05f,180,12.5f); //spawn platform player
        createPlatform(16.5f,-13.8f,20,4); //spawn plat slide

        createPlatform(-1,-10,-1.6f,12); // 2nd plat
        createPlatform(-15f,-9.15f,-13.4f,2.8f); //slide 2nd plat  - shorter gap at start of slide

        createPlatform(3,-4.5f,2.5f, 15); // 3rd plat   -small gap at far right
        createPlatform(-2.8f,1,-3f, 15); // 4th plat - small gap far left

        createPlatform(3,6,0.85f, 13); // 5th plat  - left large
        createPlatform(20,6.2f,0.85f, 1.5f); // 5th plat  - mini plat forming gap far right


        createPlatform(-14f,11,-0.85f, 6); //top plat dk stand small
        createPlatform(3.15f,10.8f,-0.85f, 8.5f); //top larger plat - w gap soon after barrel spawn for slow ones



        createPlatform(-20,3,90);  //side walls
        createPlatform(20f,3,90); //side walls
        createPlatform(3,20f,0);  //part of celing
        createPlatform(-3,20f,0);  //2nd part of celing
        createPlatform(-9.5f, 14.5f,0,2.5f); //princess platform
        createPlatform(-16.2f,-18.5f,180,2.5f); //bottle smash platform


        //new added shield pickup - sits on  middle platform
        shield = new Shield(this, 2f, -1.5f);

        player = new Player(this, playerImage, 5, new BoxShape(0.5f, 1.3f));
        player.setPosition(new Vec2(-10, -12.5f));

        player.setMovementLocked(true); //lock on spawn - barrels only spawn once you 50% finished level
        javax.swing.Timer unlockTimer = new javax.swing.Timer(3000, e -> {
            player.setMovementLocked(false); //unlock after 5s
        });
        unlockTimer.setRepeats(false);
        unlockTimer.start();


        SolidFixture sf = new SolidFixture(player, new BoxShape(0.5f, 1.3f), 2f);
        sf.setFriction(3);  //friction
        player.addCollisionListener(new PlayerCollision(player, soundPlayer));

        //player.removeAllImages();
        //player.addImage(new BodyImage(playerImage, 3.3f));

        StaticBody donkey = new StaticBody(this, new BoxShape(2f, 2f));
        donkey.setPosition(new Vec2(-15.5f, 13.5f));
        donkey.addImage(new BodyImage("data/lifeguard2_lvl2.png", 4.8f));


        addStepListener(new StepListener() {
            int counter = 0;
            @Override
            public void preStep(StepEvent e) {
                counter++;
                if (counter % 100 == 0) spawnBarrel(); //spawn barrels every 1ish secnd

                checkFall();
                cleanBarrels();

                //if shield was collected
                if (shieldAlive && player.hasShield()) shieldAlive = false;

                checkWin(WIN_X_MIN, WIN_X_MAX, WIN_Y_MIN);  //if player in cirtain x+y coords
            }
            @Override public void postStep(StepEvent e) {}
        });
    }

    //game meths

    private void spawnBarrel(){  //barrel spanw, same as lvl1 but differetn pos + img + speed
        Barrel b = new Barrel(this, new Vec2(-14.5f, 13), "data/lifering_lv2.png");
        float randomSpeed = 10 + (float)(Math.random() * 90); //10 minimum, 100 max, random between 1 and 90 added onto min 10
        b.setLinearVelocity(new Vec2(-randomSpeed, 0));
        barrels.add(b);
    }

    private void createPlatform(float x, float y, float angle, float halfWidth) {  //main create plat meth
        StaticBody p = new StaticBody(this, new BoxShape(halfWidth, 0.1f));
        p.setPosition(new Vec2(x, y)); p.setAngleDegrees(angle);

        try {  // below all same as level 1 + 3
            //load and stretch image to match platform width

            //BufferedImage original = ImageIO.read(new File("data/mainplat2.png"));   //bamboo plat
            BufferedImage original = ImageIO.read(new File("data/lvl2_platforms.png"));  //palm tree trunk plat

            int pixelWidth = (int)(halfWidth * 2 * 25); // 25 pixels per world unit, tweak if needed
            int pixelHeight = (int)(2f * 25);
            java.awt.Image scaled = original.getScaledInstance(pixelWidth, pixelHeight, java.awt.Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
            resized.getGraphics().drawImage(scaled, 0, 0, null);

            //save temp file and load as BodyImage
            File temp = new File("data/Lv2_platform_resizeHW" + halfWidth + ".png");
            ImageIO.write(resized, "png", temp);
            p.addImage(new BodyImage(temp.getPath(), 2f));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPlatform(float x, float y, float angle){  //set sixze create plat meth
        StaticBody p = new StaticBody(this, new BoxShape(17, 0.1f));
        p.setPosition(new Vec2(x, y)); p.setAngleDegrees(angle);
        //createPlatform(x, y, angle, 17);

        try {  //same as above
            //load and stretch image to match platform widt h
            BufferedImage original = ImageIO.read(new File("data/lvl2_platforms.png")); //original img
            int pixelWidth = (int)(17 * 2 * 25);//calc size needed for plat , 25pixels per world
            int pixelHeight = (int)(2f * 25); //reduced from 3.5f thinner than level 2 x 25 pixels
            java.awt.Image scaled = original.getScaledInstance(pixelWidth, pixelHeight, java.awt.Image.SCALE_SMOOTH);   //built in meth to resize given mages using params, calculated widths from above line
            BufferedImage resized = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);//create empty img so file can be saved
            resized.getGraphics().drawImage(scaled, 0, 0, null);//draw resize img into empty omg above

            //save temp file and load as BodyImage
            File temp = new File("data/platform_temp_" + 17 + ".png"); //save as file+suffix
            ImageIO.write(resized, "png", temp);//as a png
            p.addImage(new BodyImage(temp.getPath(), 2f));//match the height above - attach to platforms
        } catch (Exception e) {//if sum wrong, error msg
            e.printStackTrace();
        }



    }


    public Player getPlayer(){
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
    public boolean isLevelComplete(){
        return levelComplete;
    }
    public boolean isShieldAlive(){
        return shieldAlive;
    }
    public static float getWinXMin() {
        return WIN_X_MIN;
    }
    public static float getWinXMax(){
        return WIN_X_MAX;
    }
    public static float getWinYMin(){
        return WIN_Y_MIN;
    }

    @Override protected float getFallThreshold() { return -20f; }
    @Override protected Vec2 getRespawnPoint()   { return new Vec2(-10, -12.5f); }
    @Override protected String getWinSFX()       { return SFX_WIN; }
}