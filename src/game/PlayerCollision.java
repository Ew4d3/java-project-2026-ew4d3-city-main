package game;

import city.cs.engine.*;


public class PlayerCollision implements CollisionListener {

    private Player player;
    private SoundPlayer soundPlayer;

    //paths to sound effects
    private static final String SFX_HIT = "data/lose_life.wav";




    private static final String SFX_SHIELD = "data/sfx_shield.wav";
    private static final String SFX_HEART = "data/heart_pickup.wav";


    public PlayerCollision(Player player, SoundPlayer soundPlayer) {  //assigning player + sound per level
        this.player = player;
        this.soundPlayer = soundPlayer;
    }

    @Override
    public void collide(CollisionEvent e) {  //on collision - action depends on wht collision eg instance of heart

        //shield pickup l2
        if (e.getOtherBody() instanceof Shield) {  //if collided w shuld, activate
            e.getOtherBody().destroy();
            player.activateShield();
            player.addScore(25);
            soundPlayer.playSFX(SFX_SHIELD);
        }

        //shield pickup l2
        if (e.getOtherBody() instanceof Heart) {  //if collided w hear, lives++
            e.getOtherBody().destroy();
            player.addScore(15);
            player.activateHeart();
            soundPlayer.playSFX(SFX_HEART);
        }

        //barrel collision
        if (e.getOtherBody() instanceof Barrel) {  //if colided w barrrel , lose life
            e.getOtherBody().destroy();
            if (!player.consumeShield()) {
                //if shield not active — lose life
                player.loseLife();
                soundPlayer.playSFX(SFX_HIT);
            } else {
                //shield took the hit
                soundPlayer.playSFX(SFX_SHIELD);
            }
        }


        if (e.getOtherBody() instanceof BootsPickup) {  //if collided w boots , activate boots
            e.getOtherBody().destroy();
            player.activateBoots();
            soundPlayer.playSFX("data/boots_pickup.wav"); // or reuse an existing SFX
        }
    }
}
