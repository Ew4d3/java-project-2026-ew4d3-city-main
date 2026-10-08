package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class Level4 extends GameLevel {  //inherits main abst class same as every level

    private String playerImage= "data/level3_playerNoBKG.png";
    private String backgroundImage = "data/black.png";
    private Shield shield;
    private Heart heart;

    private ArrayList<Heart> hearts = new ArrayList<>();  //tracks all hearts, replaces single heart field
    private BootsPickup boots; //jump boost boots lvl3

    private static final String BGM   = "data/level4_bkgm.wav"; //new lvl4 music
    private static final String SFX_WIN = "data/level_complete.wav";

    private static final float WIN_X_MIN = -12.5f;
    private static final float WIN_X_MAX = -6.5f;
    private static final float WIN_Y_MIN  = 14.5f;

    public Level4(int startingLives, SoundPlayer soundPlayer) {  //level 4 init w params
        this.soundPlayer = soundPlayer;
        buildWorld(startingLives);
    }

    private void buildWorld(int startingLives) {  //create world
        setGravity(35); // heavier gravity than L3

        soundPlayer.playBKG_music(BGM);


        //createPlatform(16.5f,  -13.8f,20,4); //slide
        //createPlatform(-16.5f, -8.9f,-12,4); //slide plat2



        //createPlatform(-1,-10,-1.6f,5); //plat1
        //createPlatform(4,-10,-1.6f,5); //plat1
        new MovingPlatform(this,  -7f,  -11, 0, 5.5f, 0.11f, true);//mov plat1 left

        new MovingPlatform(this,  5.5f,  -11.5f, 0, 3f, 0.09f, true);//mov plat2 right


        createPlatform(-17,-9,0,2); //plat2
        new MovingPlatform(this,  -2f,  -5.5f, 0, 10f, 0.13f, true);
        createPlatform(17,-3f,0,2); //plat2




        new MovingPlatform(this,  -2f,  0, 0, 10f, 0.10f, true);

        createPlatform(-17,1,-0.85f,2); //plat3


        new MovingPlatform(this,  0,  5, 0, 10f, 0.07f, true);

        createPlatform(17,5,0.85f,2); //plat4



        createPlatform(-17f,9.5f,0,2); //top plat 5 -  donkey 1

        createPlatform(-3,9.5f,0,2); //top plat 5 - donkey 2

        createPlatform(10.5f,9.5f,0,2); //top plat 5 - donkey 3










        //createPlatform(-30,3,90,20);  //side walls
        //createPlatform(30f,3,90,20); //side walls
        createPlatform(0,20f,0,20);  //part of celing
        //createPlatform(-3,20f,0);  //2nd part of celing
        createPlatform(-11, 13.5f,0,2.5f); //princess alien platform

        createPlatform(2.5f, 13.5f,0,2.5f); //princess lifeguard platform
        createPlatform(16.5f, 13.5f,0,2.5f); //princess homeless platform


        createPlatform(-11f,-15.5f,0,2.5f); //bottle smash platform
        createPlatform(16,-15.05f,0,2f); //spawn platform player

        // moving platforms — faster than L3
        //new MovingPlatform(this,  3f,  -5f, 0, 4f, 0.09f, true);
        //new MovingPlatform(this, -5f,   1f, 0, 4f, 0.085f, false);
        //new MovingPlatform(this, 12f,   5f, 0, 3f, 0.095f, true);

        // collectibles
        //shield = new Shield(this, 4f, -14f);
        //heart  = new Heart(this, -10f, 3f);

        // player

        shield = new Shield(this, -8.5f, 6.5f);

        hearts.add(new Heart(this, 17f, 7f));  // top right heart
        hearts.add(new Heart(this, -17f, -7.5f));  //second btm left  heart
        hearts.add(new Heart(this, 6, 1.5f));  //middle heart

        boots = new BootsPickup(this, 6f, -10); //boots




        player = new Player(this, "data/level4_player.png", startingLives, new BoxShape(0.75f, 1.5f), 2.7f);
        player.setPosition(new Vec2(16, -13));

        SolidFixture sf = new SolidFixture(player, new BoxShape(0.75f, 1.5f), 0.5f);
        sf.setFriction(3);  //friction
        player.addCollisionListener(new PlayerCollision(player, soundPlayer));

        player.setMovementLocked(true); //same as lvl1 etc
        javax.swing.Timer unlockTimer = new javax.swing.Timer(1500, e -> {  //2.5s ish
            player.setMovementLocked(false); //unlock after 5s
        });
        unlockTimer.setRepeats(false);
        unlockTimer.start();


        //player.addCollisionListener(new PlayerCollision(player, soundPlayer));

        // donkey 1 - alien lvl3
        StaticBody donkey = new StaticBody(this, new BoxShape(1.5f, 2.5f));
        donkey.setPosition(new Vec2(-17, 12.5f));
        donkey.addImage(new BodyImage("data/alien_level3_.png", 6.65f)); //lvl3

        // donkey 2 -lifeguard lvl2
        StaticBody donkey2 = new StaticBody(this, new BoxShape(1.5f, 2.5f));
        donkey2.setPosition(new Vec2(-3f, 12.5f));
        donkey2.addImage(new BodyImage("data/lifeguard2_lvl2.png", 5)); //lvl2


        // donkey 3 - homless lvl1
        StaticBody donkey3 = new StaticBody(this, new BoxShape(1.5f, 2.5f));
        donkey3.setPosition(new Vec2(10.5f, 12.5f));
        donkey3.addImage(new BodyImage("data/homelessNoBKG.png", 5)); // lvl1


        addStepListener(new StepListener() {
            int counter = 0;
            @Override
            public void preStep(StepEvent e) {
                counter++;
                //barrels every ~0.75s from BOTH sides, faster than L3
                if (counter % 75 == 0)  spawnBarrelRight();
                if (counter % 110 == 0) spawnBarrelLeft();

                checkFall();
                cleanBarrels();
                //check all three princess platforms
                checkWin(-13.5f, -8.5f, 14.0f);   //left platform (alien)
                checkWin(0.0f, 5.0f, 14.0f);       //middle platform (lifeguard)
                checkWin(14.0f, 19.0f, 14.0f);     //right platform (homeless)
            }
            @Override public void postStep(StepEvent e) {}
        });
    }

    private void spawnBarrelRight() {  //only middle and left donkeys throw right, no point in left throwing right as nust straight out of map
        //barrel 1 - alien
        Barrel b1 = new Barrel(this, new Vec2(-15, 13), "data/magma2_lvl3_.png");
        b1.setLinearVelocity(new Vec2(15 + (float)(Math.random() * 30), -15));
        barrels.add(b1);

        //barrel 2 - middle lifeguard
        Barrel b2 = new Barrel(this, new Vec2(-5, 13));
        //b2.addImage(new BodyImage("data/lifering_lv2.png", 1.5f));
        b2.setLinearVelocity(new Vec2(15 + (float)(Math.random() * 30), -17));
        barrels.add(b2);

    }

    private void spawnBarrelLeft() { //only middle and right donkeys throw left, no point in right throwing left as just straight out of map

        //barrel 2 - middle lifeguard
        Barrel b2 = new Barrel(this, new Vec2(0, 13));
        //b2.addImage(new BodyImage("data/lifering_lv2.png", 1.25f)); // adjust size
        b2.setLinearVelocity(new Vec2(15 + (float)(Math.random() * 30), -15));
        barrels.add(b2);

        //barrel 3  - right homeless
        Barrel b3 = new Barrel(this, new Vec2(10, 13), "data/barrelNoBKG.png");
        b3.setLinearVelocity(new Vec2(15 + (float)(Math.random() * 30), -15));
        barrels.add(b3);
    }

    private void createPlatform(float x, float y, float angle, float halfWidth) {  //create plat w fully custom params
        StaticBody p = new StaticBody(this, new BoxShape(halfWidth, 0.1f));
        p.setPosition(new Vec2(x, y));
        p.setAngleDegrees(angle);
        try {
            BufferedImage original = ImageIO.read(new File("data/lvl3_platforms.png")); //reuse L3 image
            int pw = (int)(halfWidth * 2 * 25); //calc size needed for plat , 25pixels per world
            int ph = (int)(2f * 25);//reduced from 3.5f thinner than level 2 x 25 pixels
            java.awt.Image scaled = original.getScaledInstance(pw, ph, java.awt.Image.SCALE_SMOOTH);//built in meth to resize given mages using params, calculated widths from above line
            BufferedImage resized = new BufferedImage(pw, ph, BufferedImage.TYPE_INT_ARGB);//create empty img so file can be saved
            resized.getGraphics().drawImage(scaled, 0, 0, null);//draw resize img into empty omg above

            //save temp file and load as BodyImage
            File temp = new File("data/Lv4_platform_" + halfWidth + ".png");//save as file+suffix
            ImageIO.write(resized, "png", temp);//as a png
            p.addImage(new BodyImage(temp.getPath(), 2.5f));//match the height above - attach to platforms
        } catch (Exception e) {//if sum wrong, error msg
            e.printStackTrace();
        }



    }

    @Override public boolean isComplete()          { return levelComplete; }
    @Override public String  getBackgroundImage()  { return backgroundImage; }
    @Override public String  getPrincessImage()    { return "data/astro_joe1.png"; }
    @Override protected float getFallThreshold()   { return -22f; }
    @Override protected Vec2  getRespawnPoint()    { return new Vec2(16, -15f); }
    @Override protected String getWinSFX()         { return SFX_WIN; }
}