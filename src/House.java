import processing.core.PImage;

import java.util.List;

public class House extends Entity implements Transformable {
    public static final String HOUSE_KEY = "house";
    public static final int HOUSE_PARSE_PROPERTY_COUNT = 0;

    public House(String id, Point position, List<PImage> images) {
        super(id, position, images);
    }

    @Override
    public boolean transform(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        boolean shouldTransform = PathingStrategy.CARDINAL_NEIGHBORS.apply(getPosition()).map(point -> (world.getOccupant(point).isPresent() && world.getOccupant(point).get() instanceof BadDude)).reduce(false, (a,b) -> a || b);
        if (shouldTransform) {
            Explosion explosion = new Explosion(Explosion.EXPLOSION_KEY, getPosition(), imageLibrary.get(Explosion.EXPLOSION_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(explosion);
            //explosion.scheduleAction(scheduler, world, imageLibrary);
        }
        return false;
    }
}
