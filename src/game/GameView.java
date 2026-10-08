package game;

import city.cs.engine.UserView;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

public abstract class GameView extends UserView {  //main class that gets inhertited per level - abstract class

    //useful for polymophism - set meth then override later in child class

    protected GameLevel world;
    protected SoundPlayer soundPlayer;
    protected Image heartIcon;
    protected Image coinIcon;
    protected int totalScore;
    protected boolean gameOverFired = false;
    protected javax.swing.Timer gameOverTimer;
    protected Runnable onGameOver;
    protected Runnable onLevel1, onLevel2, onLevel3, onLevel4;


    //shake fields shared across all views
    protected int shakeDuration = 0;
    protected int shakeIntensity = 8;
    protected int lastLives;

    //pause state
    private boolean paused = false;
    private JButton pauseBtn;
    private JPanel pauseOverlay;
    private static final String MENU_BGM = "data/dk_Lvl_supCel.wav";
    private String levelBGM; //stored so can resume on unpause

    public GameView(GameLevel world, int width, int height, SoundPlayer soundPlayer, int totalScore, Runnable onLevel1,
                    Runnable onLevel2, Runnable onLevel3, Runnable onLevel4) {  //all parmams needed for gameviews

        super(world, width, height);  //parent class assingment - super
        this.world = world;
        this.soundPlayer = soundPlayer;
        this.totalScore = totalScore;
        this.onLevel1 = onLevel1;
        this.onLevel2 = onLevel2;
        this.onLevel3 = onLevel3;
        this.onLevel4 = onLevel4;

        heartIcon = new javax.swing.ImageIcon("data/heartNoBKG.png").getImage();
        coinIcon  = new javax.swing.ImageIcon("data/pointsNoBKG.png").getImage();
        lastLives = world.getPlayer().getLives();

        //pause button — small cog button top centre
        setLayout(null);
        pauseBtn = new JButton("⚙") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0, 0, 0, 140));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pauseBtn.setForeground(Color.WHITE);
        pauseBtn.setFont(new Font("Arial", Font.BOLD, 14));
        pauseBtn.setBounds(510, 8, 60, 30); // top centre
        pauseBtn.setContentAreaFilled(false);
        pauseBtn.setBorderPainted(false);
        pauseBtn.setFocusPainted(false);
        pauseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pauseBtn.addActionListener(e -> togglePause());
        add(pauseBtn);
    }

    //called by each views constructor to store the level BGM path for resume
    protected void setLevelBGM(String bgmPath) {
        this.levelBGM = bgmPath;
    }

    private void togglePause() {
        if (paused) {
            unpause();
        } else {
            pause();
        }
    }

    private void pause() {  //pause button - stop everything + apply overlay+bkg music
        paused = true;
        world.stop();
        pauseBtn.setText("▶");

        //switch to menu music
        soundPlayer.stopBGM();
        soundPlayer.playBKG_music(MENU_BGM);

        //semi-transparent pause overlay panel
        pauseOverlay = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(10, 10, 30, 200)); //dark semi-transparet
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        pauseOverlay.setOpaque(false);
        pauseOverlay.setBounds(0, 0, getWidth(), getHeight());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 30, 0);

        JLabel title = new JLabel("PAUSED", SwingConstants.CENTER);
        title.setFont(new Font("Arial Black", Font.BOLD, 52));
        title.setForeground(new Color(255, 200, 50));
        pauseOverlay.add(title, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 15, 0);
        JButton resumeBtn = makePauseMenuButton("▶  RESUME");
        resumeBtn.addActionListener(e -> unpause());
        pauseOverlay.add(resumeBtn, gbc);


        gbc.gridy = 2;

        SettingsPanel settings = new SettingsPanel(
                soundPlayer,
                () -> { unpause(); onLevel1.run(); },  //lambdas - if unpause button pressed, run current level
                () -> { unpause(); onLevel2.run(); },
                () -> { unpause(); onLevel3.run(); },
                () -> { unpause(); onLevel4.run(); }
        );

        pauseOverlay.add(settings, gbc);

        add(pauseOverlay);
        setComponentZOrder(pauseOverlay, 0); // on top
        revalidate();
        repaint();



    }

    private void unpause() {  //unpuse - resume level - remove overlat + resume original music
        paused = false;
        world.start();
        pauseBtn.setText("II");

        soundPlayer.stopBGM();
        if (levelBGM != null) soundPlayer.playBKG_music(levelBGM);

        if (pauseOverlay != null) {
            remove(pauseOverlay);
            pauseOverlay = null;
        }
        revalidate();
        repaint();
        setFocusable(true);  //sets kbm to game again, after pause wouldnt be abe to move
        requestFocusInWindow();
    }

    public boolean isPaused() {
        return paused;
    }

    private JButton makePauseMenuButton(String text) {  //pause button - same as btn in main menu just for pause instead
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Color.BLUE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setPreferredSize(new Dimension(260, 52));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void triggerShake() {
        shakeDuration = 20;
    }   //shake on life loss

    //shared HUD — all views call this in paintForeground
    //totalScore includes score from all prev levels passed in from Game class
    protected void drawHUD(Graphics2D g) {
        Player player = world.getPlayer();

        //shake detection
        if (player.getLives() < lastLives) triggerShake();
        lastLives = player.getLives();

        if (shakeDuration > 0) {
            int ox = (int)(Math.random() * shakeIntensity * 2) - shakeIntensity;
            int oy = (int)(Math.random() * shakeIntensity * 2) - shakeIntensity;
            g.translate(ox, oy);
            g.setColor(new Color(255, 0, 0, 60));
            g.fillRect(-shakeIntensity, -shakeIntensity,
                    getWidth() + shakeIntensity * 2, getHeight() + shakeIntensity * 2);
            shakeDuration--;
        }

        //hearts draw
        int iconX = getWidth() - 210, scoreY = 75, iconSize = 40;
        for (int i = 0; i < player.getLives(); i++)
            g.drawImage(heartIcon, iconX + i * (iconSize + 1), 5, iconSize, iconSize, this);

        //totalScore = previous levels + current level score
        g.drawImage(coinIcon, iconX, scoreY - 25, iconSize, iconSize, this);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString(" x " + (totalScore + player.getScore()), iconX + 35, scoreY);
    }

    //shared game over handler — all views call this in paintForeground
    protected void handleGameOver(Runnable onGameOver) {
        if (!gameOverFired) {
            gameOverFired = true;
            soundPlayer.stopBGM();
            javax.swing.Timer soundDelay = new javax.swing.Timer(300,
                    ev -> soundPlayer.playSFX("data/sfx_death.wav"));
            soundDelay.setRepeats(false);
            soundDelay.start();
            world.getPlayer().stopWalking();
            world.stop();
            gameOverTimer = new javax.swing.Timer(3000,
                    ev -> javax.swing.SwingUtilities.invokeLater(onGameOver));
            gameOverTimer.setRepeats(false);
            gameOverTimer.start();
        }
    }
}