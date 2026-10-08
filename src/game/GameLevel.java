package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;
import java.util.ArrayList;

public abstract class GameLevel extends World {  //main class that gets inhertited per level - abstract class, same purpose as gameview

    protected Player player;
    protected SoundPlayer soundPlayer;
    protected ArrayList<Barrel> barrels = new ArrayList<>();
    protected boolean levelComplete = false;
    protected boolean respawning = false;
    private LevelCompleteListener levelCompleteListener;

    //each level defines its own threshold and respawn point
    protected abstract float getFallThreshold();
    protected abstract Vec2 getRespawnPoint();
    protected abstract String getWinSFX();

    //shared fall detection — called from each level's step listener
    protected void checkFall() {
        if (player.getPosition().y < getFallThreshold() && !respawning) {
            respawning = true;
            player.loseLife();
            soundPlayer.playSFX("data/lose_life.wav");

            if (player.getLives() <= 0) {
                soundPlayer.stopBGM();

            }
            else {
                player.setPosition(getRespawnPoint());
                player.setLinearVelocity(new Vec2(0, 0));
                javax.swing.Timer t = new javax.swing.Timer(500, ev -> respawning = false);
                t.setRepeats(false);
                t.start();
            }
        }
    }

    //shared barrel cleanup
    protected void cleanBarrels() {
        for (int i = barrels.size() - 1; i >= 0; i--) {
            Barrel b = barrels.get(i);
            if (b.getPosition().y < getFallThreshold() + 2) {
                b.destroy();
                barrels.remove(i);
                player.addScore(10);
            }
        }
    }

    //win condition check — each level passes its own bounds
    protected void checkWin(float xMin, float xMax, float yMin) {
        if (!levelComplete) {
            float px = player.getPosition().x;
            float py = player.getPosition().y;
            if (px >= xMin && px <= xMax && py > yMin) {
                levelComplete = true;
                soundPlayer.playSFX(getWinSFX());
                if (levelCompleteListener != null) levelCompleteListener.onLevelComplete();
            }
        }
    }

    public void setLevelCompleteListener(LevelCompleteListener l) {
        this.levelCompleteListener = l;
    }

    public Player getPlayer() { return player; }
    public abstract boolean isComplete();
    public abstract String getBackgroundImage();
    public abstract String getPrincessImage();
    public boolean isLevelComplete() { return levelComplete; }
}