package game;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;




//main menu , contains play instructions and settings buttons
//instrcutiosn displays text on how to play
//settings includes vlume slider for overall volume control
//play loads level 1

public class MainMenu extends JPanel {

    private static final String MENU_BGM = "data/dk_Lvl_supCel.wav";  //bkg music


    //menu colours
    private static final Color BG_TOP = new Color(10,10,30);
    private static final Color BG_BOT = new Color(30,10,50);
    private static final Color ACCENT = new Color(255,200,50);
    private static final Color BTN_TEXT = Color.WHITE;

    //menu init
    private String currentPanel = "MAIN";
    private SoundPlayer soundPlayer;
    private Runnable onPlay;
    private Runnable onLevel1;
    private Runnable onLevel2;
    private Runnable onLevel3;
    private Runnable onLevel4;


    //volume slider ref so Game can read value
    private JSlider volumeSlider;


    //params w assing
    public MainMenu(SoundPlayer soundPlayer, Runnable onPlay, Runnable onLevel1, Runnable onLevel2, Runnable onLevel3, Runnable onLevel4) {
        this.soundPlayer = soundPlayer;
        this.onPlay = onPlay;
        this.onLevel1 = onLevel1;
        this.onLevel2 = onLevel2;
        this.onLevel3 = onLevel3;
        this.onLevel4 = onLevel4;

        setPreferredSize(new Dimension(800, 800));
        setLayout(new BorderLayout());
        //start menu music on load

        soundPlayer.playBKG_music(MENU_BGM);
        showMain();
    }

    //buttons init
    private void showMain() {
        currentPanel = "MAIN";
        removeAll();
        add(buildMainPanel(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    //instructions btn
    private void showInstructions() {
        currentPanel = "INSTRUCTIONS";
        removeAll();
        add(buildInstructionsPanel(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }
//settings btn
    private void showSettings() {
        currentPanel = "SETTINGS";
        removeAll();
        add(buildSettingsPanel(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    //main panel init
    private JPanel buildMainPanel() {
        JPanel panel = new GradientPanel(BG_TOP, BG_BOT);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.insets = new Insets(12, 0, 12, 0);

        //title
        JLabel title = new JLabel("DODGE AND DASH", SwingConstants.CENTER);
        //JLabel title = new JLabel("FULL SEND", SwingConstants.CENTER);

        title.setFont(new Font("Arial Black", Font.BOLD, 52));
        title.setForeground(ACCENT);
        gbc.gridy = 0; gbc.insets = new Insets(40, 0, 8, 0);
        panel.add(title, gbc);

        //game tag
        JLabel subtitle = new JLabel("Four worlds. One goal. Don't get hit.", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.ITALIC, 20));
        subtitle.setForeground(new Color(200, 180, 255));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 50, 0);
        panel.add(subtitle, gbc);

        //button create
        gbc.insets = new Insets(10, 0, 10, 0);

        //play btn
        JButton play = makeButton("▶  PLAY");
        play.addActionListener(e -> onPlay.run());
        gbc.gridy = 2; panel.add(play, gbc);

        //inst btn
        JButton instructions = makeButton("INSTRUCTIONS");
        instructions.addActionListener(e -> showInstructions());
        gbc.gridy = 3; panel.add(instructions, gbc);

        //settings btn
        JButton settings = makeButton("⚙  SETTINGS");
        settings.addActionListener(e -> showSettings());
        gbc.gridy = 4; panel.add(settings, gbc);

        //footer text
        JLabel footer = new JLabel("Use A/D or ← → to move  |  W / ↑ / Left-Click / SPACE to jump | Hold Right-Click to Sprint", SwingConstants.CENTER);
        footer.setFont(new Font("Arial", Font.PLAIN, 13));
        footer.setForeground(new Color(140, 130, 180));
        gbc.gridy = 5; gbc.insets = new Insets(40, 0, 20, 0);
        panel.add(footer, gbc);

        return panel;
    }

    //instruction panel init
    private JPanel buildInstructionsPanel() {
        JPanel panel = new GradientPanel(BG_TOP, BG_BOT);
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(35, 50, 35, 50));

        JLabel title = new JLabel("HOW TO PLAY", SwingConstants.CENTER);
        title.setFont(new Font("Arial Black", Font.BOLD, 36));
        title.setForeground(ACCENT);
        panel.add(title, BorderLayout.NORTH);

        String[] lines = {  //all instruction text
                "CONTROLS",
                "       A / ← — Move left . D / → — Move right",
                "       W / ↑ / SPACE / LEFT MOUSE CLICK — Jump",
                "       RIGHT CLICK FOR SPRINT",
                "",
                "OBJECTIVE",
                "       Climb to the top platform to rescue Joe! Avoid objects thrown — each hit costs a life!",
                "",
                "LEVELS",
                "       Level 1 — London. 5 lives. Bottles every 6 seconds.",
                "       Level 2 — On the Beach. 5 Lives. Liferings every 3 seconds. Gaps in platforms.",
                "              A shield orb gives you one free hit!",
                "       Level 3 — Space. 5 Lives. Moving platforms!",
                "              A shield orb gives you one free hit; and a heart gives you one more life!",
                "                   Be careful, a random platform breaks after 3 seconds!",
                "       Level 4 — BOSS LEVEL. 3 enemies. 3 captured.",
        "                      3 enemies, 3 captured, all from previous 3 levels. Save 1 of them and you win!",
                "                   3x the amount of objects, full speed, watch out!",
                "",
                "SHIELD  (Level 2+3+4)",
                "       Collect the glowing blue orb. A blue bubble appears around you.",
                "       The next object that hits you destroys the shield instead of losing a life.",
                "",
                "Hearts  (Level 3+4)",
                "       Collect the hearts in space. You will receive an extra life for each heart collected.",
                "",
                "Boots  (Level 3+4)",
                "       Collect the Boots on the platform. You will have a jump boost to reach platforms easier.",
                "",
                "SCORE",
                "       +10 points for every object that falls off the map.",
                "       +15 points for collecting a heart.",
                "       +25 points for collecting a shield.",
                "           Submit your score at the end of level 3 / 4 to see your place on the leaderboard! (if you've made it!)",
                "",
                "SETTINGS / PAUSE MENU",
                "       Use pause / settings menu in-game to pause the game or navigate to other levels.",



        };

        JTextArea text = new JTextArea(String.join("\n", lines));
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        text.setForeground(Color.WHITE);
        text.setBackground(new Color(20, 20, 40));
        text.setEditable(false);
        text.setBorder(new EmptyBorder(20, 20, 20, 20));
        text.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(text);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 140), 1));
        scroll.getViewport().setBackground(new Color(20, 20, 40));
        panel.add(scroll, BorderLayout.CENTER);

        JButton back = makeButton("← BACK");
        back.addActionListener(e -> showMain());
        JPanel south = new JPanel(new FlowLayout());
        south.setOpaque(false);
        south.add(back);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    //settings panel init
    private JPanel buildSettingsPanel() {
        JPanel panel = new GradientPanel(BG_TOP, BG_BOT);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        JLabel title = new JLabel("SETTINGS");
        gbc.gridy = 0;
        panel.add(title, gbc);

        SettingsPanel settings = new SettingsPanel(
                soundPlayer, onLevel1, onLevel2, onLevel3, onLevel4
        );

        gbc.gridy = 1;
        panel.add(settings, gbc);

        JButton back = makeButton("← BACK");
        back.addActionListener(e -> showMain());
        gbc.gridy = 2;
        panel.add(back, gbc);

        return panel;
    }

        //button creation method
        private JButton makeButton (String text){
            JButton btn = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(Color.blue);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 16, 16));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setForeground(BTN_TEXT);
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.setPreferredSize(new Dimension(280, 52));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return btn;
        }

        //smaller button for level select row - pretty much same as above but smaller dimensiosns
        private JButton makeSmallButton (String text){
            JButton btn = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(Color.gray);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 16, 16));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setForeground(ACCENT);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setPreferredSize(new Dimension(120, 44));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return btn;
        }

        //gradient bkg panel
        private static class GradientPanel extends JPanel {
            private final Color top, bot;

            GradientPanel(Color top, Color bot) {
                this.top = top;
                this.bot = bot;
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bot));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }