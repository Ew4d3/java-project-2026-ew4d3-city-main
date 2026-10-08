package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

//shield pickup in level 2
public class Shield extends StaticBody {

    private static final Shape SHAPE = new CircleShape(0.7f);  //shape of shield

    public Shield(World world, float x, float y) {  //init shield
        super(world, SHAPE);
        setPosition(new Vec2(x, y));
        setFillColor(new java.awt.Color(80, 160, 255, 200));
        setLineColor(new java.awt.Color(180, 220, 255));
    }
}
