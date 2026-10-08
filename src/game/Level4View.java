package game;

import org.jbox2d.common.Vec2;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.util.List;

//level 3 view
public class Level4View extends GameView {  //inherits mian abs gameview class, same as evey other gameview class


    //var inits for lvl4
    private Level4 world;
    private Image background;
    private Image princess;
    private Image princess1;
    private Image princess2;

    private Runnable onGameOver;
    private boolean winFired = false;
    private JButton playAgainBtn;
    private ImageIcon backgroundGif;

    private boolean princessWave = false;
    private  Image princessWave2;

    private boolean princessWave_1 = false;
    private  Image princessWave2_1;

    private boolean princessWave_2 = false;
    private  Image princessWave2_2;


    //overlay fieldss
    private boolean showStartOverlay = true;
    private long overlayStartTime = System.currentTimeMillis();
    private int overlayDuration = 1500; // match movement lock

    private boolean winSoundFired = false;

    private static final String BGM = "data/PR_dk_Space_Lvl3.wav";


    private String playerName = "Player"; //default until they type name
    private boolean scoreSaved = false;
    private javax.swing.JTextField nameField;
    private javax.swing.JButton submitBtn;
    private List<ScoreManager.ScoreEntry> leaderboard;

    public Level4View(Level4 world, int width, int height,  //level 4 parmams
                      Runnable onGameOver,
                      int totalScore,
                      SoundPlayer soundPlayer,
                      Runnable onLevel1,
                      Runnable onLevel2,
                      Runnable onLevel3,
                      Runnable onLevel4){


        super(world, width, height,  //assing
                soundPlayer,
                totalScore,
                onLevel1, onLevel2, onLevel3, onLevel4);
        this.world = world;
        this.onGameOver = onGameOver;

        setLevelBGM(BGM);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        backgroundGif = new ImageIcon("data/level4.gif"); // new lvl4 gif


        princess = new ImageIcon("data/astro_joe1.png").getImage();
        princessWave2 = new ImageIcon("data/astro_joe2.png").getImage();

        princess1 = new ImageIcon("data/rastaJoe.png").getImage();
        princessWave2_1 = new ImageIcon("data/rastaJoe_wave2.png").getImage();

        princess2 = new ImageIcon("data/joe1.png").getImage();
        princessWave2_2 = new ImageIcon("data/joe2.png").getImage();

        //3 princesses waving timers
        new javax.swing.Timer(400, e -> { princessWave = !princessWave; repaint(); }).start();
        new javax.swing.Timer(16,  e -> repaint()).start();

        new javax.swing.Timer(400, e -> { princessWave_1 = !princessWave_1; repaint(); }).start();
        new javax.swing.Timer(16,  e -> repaint()).start();

        new javax.swing.Timer(400, e -> { princessWave_2 = !princessWave_2; repaint(); }).start();
        new javax.swing.Timer(16,  e -> repaint()).start();
    }

    @Override
    protected void paintBackground(Graphics2D g) {  //eveything in bkg
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(backgroundGif.getImage(), 0, 0, getWidth(), getHeight(), this);

        g.drawImage(princessWave ? princessWave2 : princess, 135, 40, 90, 100, this);

        g.drawImage(princessWave_1 ? princessWave2_1 : princess1, 410, 30, 70, 100, this);
        g.drawImage(princessWave_2 ? princessWave2_2 : princess2, 680, 40, 90, 100, this);

    }

    @Override
    protected void paintForeground(Graphics2D g) {  //all foreground elements
        Player player = world.getPlayer();
        drawHUD(g);

        // shield bubble — same as L3
        if (player.hasShield()) {
            Vec2 pos = player.getPosition();
            float scale = 20f;
            float sx = getWidth()  / 2f + pos.x * scale;
            float sy = getHeight() / 2f - pos.y * scale;
            float r  = 38f;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(100, 180, 255, 80));
            g2.fill(new Ellipse2D.Float(sx - r, sy - r, r * 2, r * 2));
            g2.setColor(new Color(180, 220, 255, 200));
            g2.setStroke(new BasicStroke(2.5f));
            g2.draw(new Ellipse2D.Float(sx - r, sy - r, r * 2, r * 2));
            g2.dispose();
        }

        // level label
        g.setColor(new Color(255, 80, 80));
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("LEVEL 4 - BOSS ROUND", 270, 20);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("3 Enemies. 3 Captured. Good luck.", 270, 40);

        //win + game complete
        if (world.isLevelComplete()) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            g.drawString("YOU WIN!", 250, 200);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            int finalScore = totalScore + world.getPlayer().getScore();
            g.drawString("Your Score: " + finalScore, 290, 380);

            if (!winFired) {
                winFired = true;
                soundPlayer.stopBGM();
                soundPlayer.playSFX("data/sfx_levelcomplete.wav");
                world.stop();
                setLayout(null);


                //all new added score write to file :::

                //name entry field
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
                playAgainBtn = new javax.swing.JButton("BACK TO MAiN MENU");
                playAgainBtn.setBounds(270, 280, 250, 50);
                playAgainBtn.setFont(new Font("Arial", Font.BOLD, 22));
                playAgainBtn.addActionListener(e -> onGameOver.run());
                add(playAgainBtn);

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
            }}

        // game over
            if (player.getLives() <= 0 && !world.isLevelComplete()) {
                g.setColor(new Color(0, 0, 0, 160));
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 50));
                g.drawString("YOU DIED", 270, 380);

                handleGameOver(onGameOver);
            }

        // start overlay
        if (showStartOverlay) {
            long elapsed = System.currentTimeMillis() - overlayStartTime;
            if (elapsed > overlayDuration) {
                showStartOverlay = false;
            } else {
                g.setColor(new Color(0, 0, 0, 210));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(255, 80, 80));
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("LEVEL 4 - BOSS ROUND", 180, 200);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.drawString("All 3 enemies, all 3 captured.", 270, 260);
                g.drawString("Moving platforms. Full speed. 3x objects.", 230, 295);
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.setColor(Color.YELLOW);
                g.drawString("Good luck.", 320, 380);
            }
        }
    }
}