package game;

import java.awt.*;

//level 1 view, same as gameview first submission

public class Level1View extends GameView {

    private Level1 world;
    private Image background;
    private Image princess;
    private Image bottlePile;
    private Image bottleSmashed;
    private Runnable onGameOver;

    private boolean princessWave = false;
    private Image princessWave2;

    private boolean winSoundFired = false;

    //overlay fielsd
    private boolean showStartOverlay = true;
    private long overlayStartTime = System.currentTimeMillis();
    private int overlayDuration = 13000; // match movement lock

    private static final String BGM = "data/fullBKG_DK_Main.wav";


    public Level1View(Level1 world, int width, int height,
                      Runnable onGameOver,
                      int totalScore,
                      SoundPlayer soundPlayer,
                      Runnable onLevel1,
                      Runnable onLevel2,
                      Runnable onLevel3,
                      Runnable onLevel4){  //level 1 view params needed from abst class


        super(world, width, height, soundPlayer, totalScore, onLevel1, onLevel2, onLevel3, onLevel4);
        this.world = world;
        this.onGameOver = onGameOver;
        this.world = world;
        this.onGameOver = onGameOver;  //param assing

        setLevelBGM(BGM); //tells GameView which BGM to resume after unpause

        setFocusable(true);
        //prevent Swing swallowing arrow keys for focus traversal
        setFocusTraversalKeysEnabled(false);

        //image locations / references
        background = new javax.swing.ImageIcon(world.getBackgroundImage()).getImage();
        bottlePile = new javax.swing.ImageIcon("data/BeerBottlePile2.png").getImage();
        bottleSmashed = new javax.swing.ImageIcon("data/smashedBottleSide.png").getImage();

        princess = new javax.swing.ImageIcon("data/joe1.png").getImage();
        princessWave2 = new javax.swing.ImageIcon("data/joe2.png").getImage();

        javax.swing.Timer waveTimer = new javax.swing.Timer(400, e -> {
            princessWave = !princessWave;
            repaint();
        });
        waveTimer.start();
    }


    @Override //bkg override
    protected void paintBackground(Graphics2D g) { //all backround images init, height size etc
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        g.drawImage(princessWave ? princessWave2 : princess, 150, -2, 110, 130, this); //condition ? valueIfTrue : valueIfFalse
        g.drawImage(bottlePile, -5, 67, 80, 135, this);
    }

    @Override //foreground info override
    protected void paintForeground(Graphics2D g) { //all foreground image inits

        drawHUD(g); //shared hearts + score + shake from GameView

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 15));

        //tip
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("LEVEL 1 - London", 320, 30);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("Save Businessman Joe!", 320, 50);

        g.drawImage(bottleSmashed, 14, 660, 140, 140, this);

        //win text
        if (world.isLevelComplete()) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("LEVEL 1 Complete! Loading Level 2...", 100, 400);

            if (!winSoundFired) {
                winSoundFired = true;
                soundPlayer.stopBGM();
                soundPlayer.playSFX("data/sfx_levelcomplete.wav");
            }
        }

        if (world.getPlayer().getLives() <= 0 && !world.isLevelComplete()) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            g.drawString("YOU DIED", 270, 380);
            handleGameOver(onGameOver); // shared handler from GameView
        }

        //start overlay dis instrcutions etc on mvmnt lock
        if (showStartOverlay) {
            long elapsed = System.currentTimeMillis() - overlayStartTime;

            if (elapsed > overlayDuration) {
                showStartOverlay = false;
            } else {
                //dark transparent background
                g.setColor(new Color(0, 0, 0, 210));
                g.fillRect(0, 0, getWidth(), getHeight());

                // text
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("HOW TO PLAY", 250, 175);

                g.setColor(Color.GREEN);

                g.setFont(new Font("Arial", Font.PLAIN, 20));
                int y = 240;

                g.drawString("Move: WASD / Left+Right-Click / Space / Arrow Keys", 165, y);

                y += 35;
                g.drawString("Hold Right-Click to Sprint", 300, y);
                y += 60;


                g.setColor(Color.RED);

                g.drawString("Avoid objects and reach the top platform", 220, y);
                y += 35;
                g.drawString("Single jump = dodge", 300, y);
                y += 30;
                g.drawString("Double jump = reach platforms", 265, y);
                y += 60;

                g.setColor(Color.WHITE);

                g.drawString("You cannot move yet because the level is loading", 180, y);
                y += 35;
                g.drawString("There is a 13s delay to allow bottles to spawn", 190, y);
                y += 50;

                g.setFont(new Font("Arial", Font.BOLD, 35));
                y += 60;
                g.drawString("You can play when this disappears!", 120, y);
            }
        }
    }
}