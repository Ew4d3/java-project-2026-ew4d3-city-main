package game;

import org.jbox2d.common.Vec2;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.util.List;

//level 3 view
public class Level3View extends GameView {   //gameview main abs class inherit

    private Level3 world;
    private Image background;
    private Image princess;
    private Runnable onGameOver;
    private boolean winFired = false;
    private JButton playAgainBtn;
    private ImageIcon backgroundGif;

    private boolean princessWave = false;
    private  Image princessWave2;

    //overlay fieldss
    private boolean showStartOverlay = true;
    private long overlayStartTime = System.currentTimeMillis();
    private int overlayDuration = 3000; // match movement lock

    private boolean winSoundFired = false;

    private static final String BGM = "data/PR_dk_Space_Lvl3.wav";


    private String playerName = "Player"; //default until they type name
    private boolean scoreSaved = false;
    private javax.swing.JTextField nameField;
    private javax.swing.JButton submitBtn;
    private List<ScoreManager.ScoreEntry> leaderboard;


    private Runnable onLevel4;
    public Level3View(Level3 world, int width, int height,  //level3 view oparams passed in fom abs class
                      Runnable onGameOver,
                      int totalScore,
                      SoundPlayer soundPlayer,
                      Runnable onLevel1,
                      Runnable onLevel2,
                      Runnable onLevel3,
                      Runnable onLevel4){


        super(world, width, height,  //assign
                soundPlayer,
                totalScore,
                onLevel1, onLevel2, onLevel3, onLevel4);
        this.world = world;
        this.onGameOver = onGameOver;
        this.onLevel4  = onLevel4;

        setLevelBGM(BGM); //tells GameView which BGM to resume after unpause

        setFocusable(true);
        //prevent Swing swallowing arrow keys for focus traversal
        setFocusTraversalKeysEnabled(false);

        //image locations / references
        background = new javax.swing.ImageIcon("data/black.png").getImage();
        princess = new javax.swing.ImageIcon("data/space_joe.png").getImage();

        //GIF background dir
        backgroundGif = new ImageIcon("data/lvl3_gif.gif");       //originial 8bit hyperspacd
        //backgroundGif = new ImageIcon("data/gif2.gif");     //8bit circles
        //backgroundGif = new ImageIcon("data/gif3.gif");     //blue horizontal 8bit

        princess = new javax.swing.ImageIcon("data/astro_joe1.png").getImage();
        princessWave2 = new javax.swing.ImageIcon("data/astro_joe2.png").getImage();

        javax.swing.Timer waveTimer = new javax.swing.Timer(400, e -> {
            princessWave = !princessWave;
            repaint();
        });
        waveTimer.start();

        //repaint timer for GIF animation
        new javax.swing.Timer(16, e -> repaint()).start();
    }


    @Override  //bkg override
    protected void paintBackground(Graphics2D g) {  //everything needed in bkg, same as prev levels but different imgs
        //background with dark red overlay to make it feel more dangerous
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        //gif background
        g.drawImage(backgroundGif.getImage(), 0, 0, getWidth(), getHeight(), this);

        //red tint - got rid but might bring back
        //g.setColor(new Color(180, 0, 0, 55));      // subtle red tint
        //g.fillRect(0, 0, getWidth(), getHeight());    //rect to be set red


        //swaps between 1+2 for wavin anim
        g.drawImage(princessWave ? princessWave2 : princess, 155, -2, 110, 130, this); //condition ? valueIfTrue : valueIfFalse
    }

    @Override  //foregorund info override
    protected void paintForeground(Graphics2D g) {
        Player player = world.getPlayer();

        drawHUD(g); //shared hearts + score + shake from GameView

        //shield bubble
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


        if (world.getPlayer().hasBoot()) {
            g.setColor(new Color(255, 180, 0));
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("BOOTS ACTIVE", 340, 75);
        }

        g.setFont(new Font("Arial", Font.BOLD, 18));

        //level label
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("LEVEL 3 - Space!", 310, 30);
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("Save Astro-Joe!", 340, 50);





        //win + game complete
        if (world.isLevelComplete()) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            g.drawString("YOU WIN!", 250, 180);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            int finalScore = totalScore + world.getPlayer().getScore();
            g.drawString("Your Score: " + finalScore, 290, 380);

            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.setColor(Color.WHITE);
            g.drawString("(save score first!)", 330, 330);

            if (!winFired) {
                winFired = true;
                soundPlayer.stopBGM();
                soundPlayer.playSFX("data/sfx_levelcomplete.wav");
                world.stop();
                setLayout(null);



                // all new added score write to file :::

                // name entry field
                nameField = new javax.swing.JTextField("Enter name...");
                nameField.setBounds(250, 410, 200, 35);
                nameField.setFont(new Font("Arial", Font.PLAIN, 16));
                add(nameField);

                //submit score button
                submitBtn = new javax.swing.JButton("SAVE SCORE");
                submitBtn.setBounds(460, 410, 140, 35);
                submitBtn.setFont(new Font("Arial", Font.BOLD, 14));
                submitBtn.addActionListener(e -> {
                    playerName = nameField.getText().trim();
                    if (playerName.isEmpty() || playerName.equals("Enter name..."))
                        playerName = "Player";
                    ScoreManager.saveScore(playerName, finalScore);
                    leaderboard = ScoreManager.loadScores(playerName);
                    scoreSaved = true;
                    remove(nameField);
                    remove(submitBtn);
                    revalidate();
                    repaint();
                });
                add(submitBtn);

                //play again btn
                playAgainBtn = new javax.swing.JButton("PLAY AGAIN");
                playAgainBtn.setBounds(300, 200, 200, 50);
                playAgainBtn.setFont(new Font("Arial", Font.BOLD, 22));
                playAgainBtn.addActionListener(e -> onGameOver.run());
                add(playAgainBtn);



                revalidate();
                repaint();

                JButton level4Btn = new JButton("or... RISK LEVEL 4?");
                level4Btn.setBounds(310, 270, 180, 45);
                level4Btn.setFont(new Font("Arial", Font.BOLD, 16));
                level4Btn.setForeground(Color.RED);
                level4Btn.addActionListener(e -> {
                    if (onLevel4 != null) onLevel4.run();
                });
                add(level4Btn);
                revalidate();
                repaint();





            }

            //draw leaderboard once score is saved
            if (scoreSaved && leaderboard != null) {
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.setColor(Color.YELLOW);
                g.drawString("HIGH SCORES", 330, 440);

                g.setFont(new Font("Arial", Font.PLAIN, 17));
                int y = 468;
                for (int i = 0; i < leaderboard.size(); i++) {
                    ScoreManager.ScoreEntry entry = leaderboard.get(i);

                    if (entry.isPlayer) {
                        //highlight recent player's entry
                        g.setColor(new Color(255, 220, 50));
                        g.fillRoundRect(230, y - 18, 340, 24, 6, 6);
                        g.setColor(Color.BLACK);
                        g.setFont(new Font("Arial", Font.BOLD, 17));
                        g.drawString((i + 1) + ".  " + entry.name + "  —  " + entry.score + "  <- YOU", 240, y);
                        g.setFont(new Font("Arial", Font.PLAIN, 17));
                    }
                    else {
                        g.setColor(Color.WHITE);
                        g.drawString((i + 1) + ".  " + entry.name + "  —  " + entry.score, 240, y);
                    }
                    y += 28;
                }
            }
        }


        //game over
        if (player.getLives() <= 0 && !world.isLevelComplete()) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("YOU DIED", 270, 380);
            handleGameOver(onGameOver); //shared handler from gameview
        }

        if (showStartOverlay) {
            long elapsed = System.currentTimeMillis() - overlayStartTime;

            if (elapsed > overlayDuration) {
                showStartOverlay = false;
            } else {
                //dark transparent background
                g.setColor(new Color(0, 0, 0, 210));
                g.fillRect(0, 0, getWidth(), getHeight());

                //text
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("HOW TO PLAY", 250, 175);


                g.setFont(new Font("Arial", Font.BOLD, 20));

                g.drawString("LEVEL 3 - Moving Platforms, More collectables!", 165, 210);

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

                y += 30;


                g.drawString("Dont forget - a random platform breaks after 3s!", 200, y);
                y += 60;

                g.setColor(Color.WHITE);

                g.drawString("You cannot move yet because the level is loading", 180, y);
                y += 35;


                g.drawString("There is a 3s delay to allow Magma Rocks to spawn", 190, y);

                y += 50;

                g.setFont(new Font("Arial", Font.BOLD, 35));
                y += 60;

                g.drawString("You can play when this disappears!", 120, y);
            }
        }
    }
}