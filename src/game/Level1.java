package game;
import city.cs.engine.*;
import org.jbox2d.common.Vec2;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;


//original level 1 submission, just everything priv so modular for other levels.
public class Level1 extends GameLevel {

    //priv declared local images. so can be overrided in level 2 , 3 etc
    private String playerImage = "data/playerNoBKG.png";
    private String donkeyImage = "data/homelessNoBKG.png";
    private String princessImage = "data/business_joe.png";
    private String barrelImage = "data/barrelNoBKG.png";
    private String backgroundImage = "data/skyscraperBKG.PNG";

    //sound per level
    private static final String BGM = "data/fullBKG_DK_Main.wav";
    private static final String SFX_WIN = "data/level_complete.wav";

    //game init
    private int startingLives = 5;

    private static final float WIN_X_MIN = -12.5f;
    private static final float WIN_X_MAX = -6.5f;
    private static final float WIN_Y_MIN = 14.5f;

    //constructors
    public Level1(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
        buildWorld();
    }

    public Level1(String playerImage, String donkeyImage, String princessImage,
                  String barrelImage, String backgroundImage, SoundPlayer soundPlayer) {  //level 1 params needed passed
        this.playerImage = playerImage;
        this.donkeyImage = donkeyImage;
        this.princessImage = princessImage;
        this.barrelImage = barrelImage;
        this.backgroundImage = backgroundImage;
        this.soundPlayer = soundPlayer;
        buildWorld();
    }

    private void buildWorld() {  //create world + all characterisics
        setGravity(30);

        //music call
        soundPlayer.playBKG_music(BGM);

        //StaticBody ground = new StaticBody(this, new BoxShape(13.5f, 0.1f));
        //ground.setPosition(new Vec2(0, -15));


        //all platforms, same as original submission

        createPlatform(16.5f,  -13.8f,20,4); //slide
        createPlatform(-16.5f, -8.9f,-12,4); //plat1
        createPlatform(-1,-10,-1.6f,12); //slide
        createPlatform(3,-4.5f,2.5f); //plat2
        createPlatform(-3,1,-0.85f); //plat3
        createPlatform(3,6,0.85f); //plat4
        createPlatform(-6,11,-0.85f); //top plat 5
        createPlatform(-20,3,90);  //side walls
        createPlatform(20f,3,90); //side walls
        createPlatform(3,20f,0);  //part of celing
        createPlatform(-3,20f,0);  //2nd part of celing
        createPlatform(-9.5f, 14.5f,0,2.5f); //princess platform
        createPlatform(-16.2f,-18.5f,180,2.5f); //bottle smash platform

        createPlatform(0f,-15.05f,180,12.5f); //spawn platform player


        player = new Player(this, "data/playerNoBKG.png", startingLives, new BoxShape(0.5f, 1), 2.3f);
        player.setPosition(new Vec2(-10, -13));

        player.setMovementLocked(true); //lock on spawn - barrels only spawn once you 50% finished level
        javax.swing.Timer unlockTimer = new javax.swing.Timer(13000, e -> {
            player.setMovementLocked(false); //unlock after 5s
        });
        unlockTimer.setRepeats(false);
        unlockTimer.start();



        player.addCollisionListener(new PlayerCollision(player, soundPlayer));

        StaticBody donkey = new StaticBody(this, new BoxShape(2f, 2f));
        donkey.setPosition(new Vec2(-15.5f, 13.5f));
        donkey.addImage(new BodyImage(donkeyImage, 4.5f));

        addStepListener(new StepListener() {
            int counter = 0;
            @Override
            public void preStep(StepEvent e) {
                counter++;
                if (counter % 300 == 0) spawnBarrel();  //every 6ish seconds

                checkFall();
                cleanBarrels();
                checkWin(WIN_X_MIN, WIN_X_MAX, WIN_Y_MIN);
            }
            @Override public void postStep(StepEvent e) {}
        });

    }



    //all methods
    private void spawnBarrel(){  //spanw barrel from barrel class + set pos+img etc. add to arr
        Barrel b = new Barrel(this,new Vec2(-14.5f, 13), barrelImage);
        float speed = 40;
        b.setLinearVelocity(new Vec2(speed, 0));
        barrels.add(b);
    }

    //create platform method
    private void createPlatform(float x, float y, float angle, float halfWidth) {
        StaticBody p = new StaticBody(this, new BoxShape(halfWidth, 0.1f));
        p.setPosition(new Vec2(x, y)); p.setAngleDegrees(angle);

        try {
            //load and stretch image to match platform width
            BufferedImage original = ImageIO.read(new File("data/level1_wood_1.png"));  //original img
            int pixelWidth = (int)(halfWidth * 2 * 24); //calc size needed for plat , 25pixels per world
            int pixelHeight = (int)(0.3f * 25); //reduced from 3.5f thinner than level 2 x 25 pixels
            java.awt.Image scaled = original.getScaledInstance(pixelWidth, pixelHeight, java.awt.Image.SCALE_SMOOTH);  //built in meth to resize given mages using params, calculated widths from above line
            BufferedImage resized = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB); //create empty img so file can be saved
            resized.getGraphics().drawImage(scaled, 0, 0, null); //draw resize img into empty omg above

            //save temp file and load as bodyimage in data
            File temp = new File("data/Lvl1_platform_resizeHW" + halfWidth + ".png"); //save as file+suffix
            ImageIO.write(resized, "png", temp);  //as a png
            p.addImage(new BodyImage(temp.getPath(), 0.3f)); //match the height above - attach to platforms
        } catch (Exception e) {  //if sum wrong, error msg
            System.out.println("resize not workin");
        }


    }

    //set size create platform meth
    private void createPlatform(float x, float y, float angle) {
        createPlatform(x, y, angle, 17);
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

    public String getPrincessImage()   {
        return princessImage;
    }

    public boolean isLevelComplete()   {
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

    @Override protected float getFallThreshold() {
        return -20f;

    }
    @Override protected Vec2 getRespawnPoint() {
        return new Vec2(-10, -12.5f);
    }
    @Override protected String getWinSFX() {
        return SFX_WIN;
    }
}