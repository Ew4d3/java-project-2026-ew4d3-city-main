package game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SettingsPanel extends JPanel {

    private static final Color ACCENT = new Color(255,200,50);

    //same as original in main menu but wanted to access setting sfrom in game ause so copied to new file + call on btn press.
    //now modular and can be reused elsewhere, and has.

    public SettingsPanel(SoundPlayer soundPlayer, Runnable onLevel1, Runnable onLevel2, Runnable onLevel3,
                         Runnable onLevel4) {

        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(14, 0, 14, 0);

        //volume controls same as main menu class oroginally
        JLabel volLabel = new JLabel("🔊  Volume");
        volLabel.setFont(new Font("Arial", Font.BOLD, 18));
        volLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        add(volLabel, gbc);

        JSlider slider = new JSlider(0, 100, (int)(soundPlayer.getVolume() * 100));
        slider.setPreferredSize(new Dimension(320, 45));
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setForeground(ACCENT);
        slider.setBackground(new Color(30, 30, 50));
        slider.setOpaque(false);

        JLabel volPct = new JLabel(slider.getValue() + "%");
        volPct.setFont(new Font("Arial", Font.BOLD, 16));
        volPct.setForeground(ACCENT);

        slider.addChangeListener(e -> {
            float v = slider.getValue() / 100f;
            soundPlayer.setVolume(v);
            volPct.setText(slider.getValue() + "%");
        });

        JPanel sliderRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        sliderRow.setOpaque(false);
        sliderRow.add(slider);
        sliderRow.add(volPct);

        gbc.gridy = 1;
        add(sliderRow, gbc);

        JLabel hint = new JLabel("Drag the slider to adjust music and sound-effect volume.");
        hint.setFont(new Font("Arial", Font.ITALIC, 13));
        hint.setForeground(new Color(160, 150, 200));
        gbc.gridy = 2;
        add(hint, gbc);

        //level jump buttons, just coppied into new class from main manu
        JLabel levelLabel = new JLabel("Jump to Level");
        levelLabel.setFont(new Font("Arial", Font.BOLD, 18));
        levelLabel.setForeground(Color.WHITE);
        gbc.gridy = 3;
        gbc.insets = new Insets(24, 0, 8, 0);
        add(levelLabel, gbc);

        JPanel levelRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        levelRow.setOpaque(false);

        JButton lvl1 = makeSmallButton("Level 1");
        lvl1.addActionListener(e -> onLevel1.run());
        levelRow.add(lvl1);

        JButton lvl2 = makeSmallButton("Level 2");
        lvl2.addActionListener(e -> onLevel2.run());
        levelRow.add(lvl2);

        JButton lvl3 = makeSmallButton("Level 3");
        lvl3.addActionListener(e -> onLevel3.run());
        levelRow.add(lvl3);

        JButton lvl4 = makeSmallButton("Level 4");
        lvl4.addActionListener(e -> onLevel4.run());
        levelRow.add(lvl4);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 14, 0);
        add(levelRow, gbc);
    }

    //coppied from main menu so can be re used in pause menu
    private JButton makeSmallButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Color.gray);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 16, 16));
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
}