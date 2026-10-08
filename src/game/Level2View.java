package game;


import org.jbox2d.common.Vec2;
import java.awt.*;
import java.awt.geom.Ellipse2D;

//level 2 view
public class Level2View extends GameView {  //inhertits gameview

    private Level2 world;
    private Image princess;
    private Image background;
    private Runnable onGameOver;
    private Image stack1;
    private Image stack2;

    private boolean princessWave = false;
    private Image princessWave2;

    //overlay fields
    private boolean showStartOverlay = true;
    private long overlayStartTime = System.currentTimeMillis();
    private int overlayDuration = 3000; // match movement lock

    private boolean winSoundFired = false;

    private static final String BGM = "data/Temp_level2_BKG.wav";


    public Level2View(Level2 world, int width, int height,
                      Runnable onGameOver,
                      int totalScore,
                      SoundPlayer soundPlayer,
                      Runnable onLevel1,
                      Runnable onLevel2,
                      Runnable onLevel3,
                      Runnable onLevel4){  //level 2 view params from view abst class

        super(world, width, height,  //assign
                soundPlayer,
                totalScore,
                onLevel1, onLevel2, onLevel3, onLevel4);
        this.world = world;
        this.onGameOver = onGameOver;

        setLevelBGM(BGM); //tells GameView which BGM to resume after unpause

        setFocusable(true);
        //prevent Swing swallowing arrow keys for focus traversal
        setFocusTraversalKeysEnabled(false);


        //all locally defined images
        princess = new javax.swing.ImageIcon("data/rastaJoe.png").getImage();
        background = new javax.swing.ImageIcon("data/L2_Bkg2.png").getImage();
        stack1 = new javax.swing.ImageIcon("data/liferingstack1.png").getImage();
        stack2 = new javax.swing.ImageIcon("data/lifering_stack2.png").getImage();

        princess = new javax.swing.ImageIcon("data/rastajoe.png").getImage();
        princessWave2 = new javax.swing.ImageIcon("data/rastajoe_wave2.png").getImage();

        javax.swing.Timer waveTimer = new javax.swing.Timer(400, e -> {
            princessWave = !princessWave;
            repaint();
        });
        waveTimer.start();
    }

    @Override
    protected void paintBackground(Graphics2D g) { //all backround images init, height size etc
        g.setColor(new Color(20, 20, 30));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(new Color(35, 35, 50));
        for (int x = 0; x < getWidth(); x += 40) g.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += 40) g.drawLine(0, y, getWidth(), y);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        g.drawImage(princessWave ? princessWave2 : princess, 155, -14, 110, 130, this); //condition ? valueIfTrue : valueIfFalse

        g.drawImage(stack1, -6, 105, 70, 90, this);  //neat stack
    }

    @Override
    protected void paintForeground(Graphics2D g) { //all foreground image inits
        Player player = world.getPlayer();

        drawHUD(g); //shared hearts + score + shake from GameView

        //Shield bubble
        if (player.hasShield()) {
            //convert player world position to screen pixels
            Vec2 pos = player.getPosition();
            float scale = 20f;
            float screenX = getWidth()  / 2f + pos.x * scale;
            float screenY = getHeight() / 2f - pos.y * scale;
            float radius  = 38f;

            Graphics2D g2 = (Graphics2D) g.create();
            //filled bubble
            g2.setColor(new Color(100, 180, 255, 80));
            g2.fill(new Ellipse2D.Float(screenX - radius, screenY - radius, radius * 2, radius * 2));
            //outline
            g2.setColor(new Color(180, 220, 255, 200));
            g2.setStroke(new BasicStroke(2.5f));
            g2.draw(new Ellipse2D.Float(screenX - radius, screenY - radius , radius * 2, radius * 2));
            g2.dispose();
        }

        //shield status badge
        if (player.hasShield()) {
            g.setColor(new Color(80, 160, 255));
            g.setFont(new Font("Arial", Font.BOLD, 16));
            int iconX = getWidth() - 210;
            g.drawString("SHIELD ACTIVE", iconX - 20, 105);
        }

        g.drawImage(stack2, 22, 697, 100, 100, this);  //messy pile to be shaken on life loss

        //level
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("LEVEL 2 - On the Beach", 275, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("Save Rastaman Joe!", 330, 50);

        //win / lose
        if (world.isLevelComplete()) {
            g.setColor(Color.CYAN);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("LEVEL 2 COMPLETE! Loading Level 3...", 100, 400);

            if (!winSoundFired) {
                winSoundFired = true;
                soundPlayer.stopBGM();
                soundPlayer.playSFX("data/sfx_levelcomplete.wav");
            }
        }

        if (player.getLives() <= 0 && !world.isLevelComplete()) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            g.drawString("YOU DIED", 270, 380);
            handleGameOver(onGameOver); // shared handler from GameView
        }

        if (showStartOverlay) {
            long elapsed = System.currentTimeMillis() - overlayStartTime;

            if (elapsed > overlayDuration) {
                showStartOverlay = false;
            }
            else {
                // dark transparent background
                g.setColor(new Color(0, 0, 0, 210));
                g.fillRect(0, 0, getWidth(), getHeight());

                // text
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("HOW TO PLAY", 250, 175);


                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawString("LEVEL 2 - More Barrels, Gaps in Platforms!", 180, 210);

                g.setColor(Color.GREEN);
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                int y = 260;

                g.drawString("Move: WASD / Left+Right-Click / Space / Arrow Keys", 165, y);
                y += 35;

                g.drawString("Hold Right-Click for Sprint", 300, y);

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
                g.drawString("There is a 3s delay to allow LifeRings to spawn", 190, y);
                y += 50;


                g.setFont(new Font("Arial", Font.BOLD, 35));
                y += 60;
                g.drawString("You can play when this disappears!", 120, y);
            }
        }
    }
}