package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

public class Barrel extends DynamicBody {

    private static final Shape shape = new CircleShape(0.695f);  //barrel shape

    //default constructor
    public Barrel(World world) {
        this(world, new Vec2(-14.5f, 13), "data/barrelNoBKG.png");
    }
    //constructor with custom img + pos
    public Barrel(World world, Vec2 spawnPos, String imagePath) {
        super(world);
        SolidFixture fixture = new SolidFixture(this, shape);
        fixture.setRestitution(0.56f);
        fixture.setFriction(0.2f);
        //setPosition(new Vec2(-14.5f, 13));
        setPosition(spawnPos);
        addImage(new BodyImage(imagePath, 1.7f));
    }

    public Barrel(World world, Vec2 spawnPos) {  //level2 hardcoded barrel
        this(world, spawnPos, "data/lifering_lv2.png"); //const for level4
        SolidFixture fixture = new SolidFixture(this, shape);
        fixture.setRestitution(0.56f);
        fixture.setFriction(0.2f);
        //setPosition(new Vec2(-14.5f, 13));
        setPosition(spawnPos);
        //addImage(new BodyImage("data/lifering_lv2.png", 1.3f));
    }
}