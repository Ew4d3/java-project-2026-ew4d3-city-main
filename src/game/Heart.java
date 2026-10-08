package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

public class Heart extends StaticBody {   //very similaer class to shield but different methid+img

    private static final Shape SHAPE = new CircleShape(0.7f);  //same size as shield
    private static final BodyImage IMAGE = new BodyImage("data/heartNoBKG.png", 2f);

    public Heart(World world, float x, float y) {  //heat meth + actions
        super(world, SHAPE);
        setPosition(new Vec2(x, y));
        addImage(IMAGE);

        //destroy on player contact and +1 life
        addCollisionListener(event -> {
            if (event.getOtherBody() instanceof Player) {
                Player player = (Player) event.getOtherBody();
                player.activateHeart();  //+1 life immediately
                destroy();  //remove heart from world
            }
        });
    }
}