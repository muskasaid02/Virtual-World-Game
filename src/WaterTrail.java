import processing.core.PImage;

import java.util.List;

public class WaterTrail extends Actions implements Transformable{
    public static final String WATER_TRAIL_KEY = "water_trail";

    /**
     * Constructs an Entity with specified characteristics.
     * In the base program, this is not called directly.
     * Instead, the encapsulated 'create' methods are used to create specific types of entities.
     *
     * @param id       The entity's identifier.
     * @param position The entity's x/y position in the world.
     * @param images   The entity's inanimate (singular) or animation (multiple) images.
     */
    public WaterTrail(String id, Point position, List<PImage> images) {
        super(id, position, images, .1, .1);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduleAnimation(scheduler, world, imageLibrary);
        scheduleBehavior(scheduler, world, imageLibrary);
    }

    @Override
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        if (getImageIndex() >= 5) {
            this.transform(world, imageLibrary, scheduler);
        } else {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    @Override
    public void updateImage(){
        setImageIndex(getImageIndex() + 1);
    }

    @Override
    public boolean transform(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        Point position = this.getPosition();
        world.removeEntity(scheduler, this);
        Water water = new Water(Water.WATER_KEY, position, imageLibrary.get("water_tile"));
        world.addEntity(water);
        return true;
    }
}
