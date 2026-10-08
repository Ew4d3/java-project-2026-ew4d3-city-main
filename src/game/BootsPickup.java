package game;

import city.cs.engine.*;

public class BootsPickup extends StaticBody {

    private static final Shape SHAPE = new CircleShape(0.8f);   //boots circle shape

    public BootsPickup(World world, float x, float y) {  //boots level+pos   , img
        super(world, SHAPE);
        setPosition(new org.jbox2d.common.Vec2(x, y));
        addImage(new BodyImage("data/boots.png", 2f));
    }
}