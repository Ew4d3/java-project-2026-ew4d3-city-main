package game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PlayerController extends KeyAdapter {

    private Player player;

    //track keys being held
    private boolean movingLeft = false;
    private boolean movingRight = false;

    public final MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            //left click = jump
            if (e.getButton() == MouseEvent.BUTTON1) {  //button 1 = left click
                player.jump();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //right click = speed x2
            if (e.getButton() == MouseEvent.BUTTON3) {  //btn 3 = rightclick
                player.setSpeedMultiplier(2);  //2x speed on sprint
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {  //stop applying whe  released
            //release right click = normal speed
            if (e.getButton() == MouseEvent.BUTTON3) {
                player.setSpeedMultiplier(1);
            }
        }
    };

    public PlayerController(Player player) {
        this.player = player;
    }

    @Override      //key press sets boolean val
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                movingLeft = true;
                break;

            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                movingRight = true;
                break;

            //case KeyEvent.VK_A && KeyEvent.VK_SHIFT:   - tried if shift and press A , speed x2 (run). didnt work maybe fix for submission 2
            //    movingLeft = true;
            //    startWalking(speed*2);

            //    break;

            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_SPACE:

                player.jump();
                break;
        }
    }


    //key release = false
    @Override
    public void keyReleased(KeyEvent e) {  //on key release, update error checking var
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                movingLeft = false;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                movingRight = false;
                break;
        }
    }

    //makes sure both keys arent pressed, only moves left if a held and not d etc
    public void applyMovement() {
        if (movingLeft && !movingRight) {
            player.moveLeft();
        } else if (movingRight && !movingLeft) {
            player.moveRight();
        } else {
            player.stopMoving();
        }
    }

    public void setPlayer(Player player) {  //assign mov to crrect player on level switch
        this.player = player;
        this.movingLeft = false;
        this.movingRight = false;
    }
}