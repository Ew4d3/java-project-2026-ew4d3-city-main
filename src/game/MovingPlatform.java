package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

public class MovingPlatform extends StaticBody implements StepListener {

    private final float startX;
    private final float startY;
    private final float range;
    private final float speed;
    private final boolean horizontal;

    private boolean forward = true;

    public MovingPlatform(World world, float x, float y, float angle,   //all params needed for moving plat
                          float range, float speed, boolean horizontal) {  //all passed in by createmovingplat meth called lvl3
        super(world, new BoxShape(2f, 0.2f));
        setPosition(new Vec2(x, y));
        setAngle(angle);
        addImage(new BodyImage("data/lvl3_platforms_1.png", 0.7f));
        this.startX = x;
        this.startY = y;
        this.range = range;
        this.speed = speed;
        this.horizontal = horizontal;

        world.addStepListener(this);




    }

    @Override
    public void preStep(StepEvent e) {
        Vec2 pos = getPosition();

        // so can change horizontal to vertical movement
        if (horizontal) {    //horizobntal
            if (forward) {
                setPosition(new Vec2(pos.x + speed, pos.y));  // x + speed = increase x = right
                if (pos.x >= startX + range) forward = false; //until range reached
            } else {
                setPosition(new Vec2(pos.x - speed, pos.y)); // x - speed = decrease x = left
                if (pos.x <= startX - range) forward = true; //until range reached
            }
        }
        else {  //vertical
            if (forward) {
                setPosition(new Vec2(pos.x, pos.y + speed));  // y + speed = increase y = up
                if (pos.y >= startY + range) forward = false; //until range reached
            } else {
                setPosition(new Vec2(pos.x, pos.y - speed));   // y - speed = decrease y = down
                if (pos.y <= startY - range) forward = true;  //until range reached
            }
        }
    }

    @Override
    public void postStep(StepEvent e) {
    }
}