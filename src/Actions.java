import processing.core.PImage;

import java.util.List;

public abstract class Actions extends Entity{
    /** Positive (non-zero) time delay between the entity's animations. */
    private double animationPeriod;

    /** Positive (non-zero) time delay between the entity's behaviors. */
    private double behaviorPeriod;

    public Actions(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod){
        super(id, position, images);
        this.animationPeriod = animationPeriod;
        this.behaviorPeriod = behaviorPeriod;
    }

    public abstract void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary);

    public abstract void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler);

    public void scheduleAnimation(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, new Animation(this, 0), animationPeriod);
    }

    public void scheduleBehavior(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, new Behavior(this, world, imageLibrary), behaviorPeriod);
    }

    public double getAnimationPeriod() {return animationPeriod; }
    public double getBehaviorPeriod() {return behaviorPeriod; }

     public abstract void updateImage();
}
