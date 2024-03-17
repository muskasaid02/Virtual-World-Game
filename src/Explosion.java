import processing.core.PImage;

import java.util.List;

public class Explosion extends Actions {
    public static final String EXPLOSION_KEY = "explosion";

    /**
     * Constructs an Entity with specified characteristics.
     * In the base program, this is not called directly.
     * Instead, the encapsulated 'create' methods are used to create specific types of entities.
     *
     * @param id       The entity's identifier.
     * @param position The entity's x/y position in the world.
     * @param images   The entity's inanimate (singular) or animation (multiple) images.
     */
    public Explosion(String id, Point position, List<PImage> images) {
        super(id, position, images, .1, .1);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduleAnimation(scheduler, world, imageLibrary);
        scheduleBehavior(scheduler, world, imageLibrary);
    }

    @Override
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        if (getImageIndex() >= 9) {
            world.removeEntity(scheduler, this);
        } else {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    @Override
    public void updateImage(){
        setImageIndex(getImageIndex() + 1);
    }
}
