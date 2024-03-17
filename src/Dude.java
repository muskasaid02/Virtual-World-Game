import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Dude extends Actions implements Movable, Transformable{

    public static final String DUDE_KEY = "dude";

    public static final int DUDE_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX = 0;
    public static final int DUDE_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX = 1;
    public static final int DUDE_PARSE_PROPERTY_RESOURCE_LIMIT_INDEX = 2;
    public static final int DUDE_PARSE_PROPERTY_COUNT = 3;

    private int resourceCount;
    private int resourceLimit;

    private static final PathingStrategy pathingStrategy = new AStarPathingStrategy();

    public Dude(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod, int resourceCount, int resourceLimit) {
        super(id, position, images, animationPeriod, behaviorPeriod);
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
    }

    public int getResourceCount(){return resourceCount; }
    public int getResourceLimit(){return resourceLimit; }

    @Override
    public void updateImage(){
        setImageIndex(getImageIndex() + 1);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduleAnimation(scheduler, world, imageLibrary);
        scheduleBehavior(scheduler, world, imageLibrary);
    }

    @Override
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler){
        Optional<Entity> dudeTarget = findTarget(world);
        if (dudeTarget.isEmpty() || !moveTo(world, dudeTarget.get(), scheduler) || !transform(world, imageLibrary, scheduler)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    public Optional<Entity> findTarget(World world) {
        List<Class<? extends Entity>> potentialTargets;

        if (resourceCount == resourceLimit) {
            potentialTargets = List.of(House.class);
        } else {
            potentialTargets = List.of(Tree.class, Sapling.class);
        }
        return world.findNearest(getPosition(), potentialTargets);
    }

    @Override
    public boolean transform(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        if (resourceCount < resourceLimit) {
            resourceCount += 1;
            if (resourceCount == resourceLimit) {
                Dude dude = new Dude(getId(), getPosition(), imageLibrary.get(Dude.DUDE_KEY + "_carry"), getAnimationPeriod(), getBehaviorPeriod(), resourceCount, resourceLimit);

                world.removeEntity(scheduler, this);

                world.addEntity(dude);
                dude.scheduleActions(scheduler, world, imageLibrary);

                return true;
            }
        } else {
            Dude dude = new Dude(getId(), getPosition(), imageLibrary.get(Dude.DUDE_KEY), getAnimationPeriod(), getBehaviorPeriod(), 0, resourceLimit);

            world.removeEntity(scheduler, this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageLibrary);

            return true;
        }

        return false;
    }

    @Override
    public boolean moveTo(World world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacentTo(target.getPosition())) {
            if (target instanceof Tree) {
                ((Tree) target).setHealth(((Tree) target).getHealth() - 1);
            } else if (target instanceof Sapling) {
                ((Sapling) target).setHealth(((Sapling) target).getHealth() - 1);
            }
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }

            return false;
        }
    }

    @Override
    public Point nextPosition(World world, Point destination) {
        Predicate<Point> canPassThrough = point -> world.inBounds(point) && (!world.isOccupied(point) || (world.getOccupant(point).get() instanceof Stump));
        BiPredicate<Point, Point> withinReach = Point::adjacentTo;
        List<Point> path = pathingStrategy.computePath(getPosition(), destination, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);

        return !path.isEmpty() ? path.getFirst() : getPosition();
//        // Differences between the destination and current position along each axis
//        int deltaX = destination.x - getPosition().x;
//        int deltaY = destination.y - getPosition().y;
//
//        // Positions one step toward the destination along each axis
//        Point horizontalPosition = new Point(getPosition().x + Integer.signum(deltaX), getPosition().y);
//        Point verticalPosition = new Point(getPosition().x, getPosition().y + Integer.signum(deltaY));
//
//        // Assumes all destinations are within bounds of the world
//        // If this is not the case, also check 'World.inBounds()'
//        boolean horizontalOccupied = world.isOccupied(horizontalPosition) && !(world.getOccupant(horizontalPosition).get() instanceof Stump);
//        boolean verticalOccupied = world.isOccupied(verticalPosition) && !(world.getOccupant(verticalPosition).get() instanceof Stump);
//
//        // Move along the farther direction, preferring horizontal
//        if (Math.abs(deltaX) >= Math.abs(deltaY)) {
//            if (!horizontalOccupied) {
//                return horizontalPosition;
//            } else if (!verticalOccupied) {
//                return verticalPosition;
//            }
//        } else {
//            if (!verticalOccupied) {
//                return verticalPosition;
//            } else if (!horizontalOccupied) {
//                return horizontalPosition;
//            }
//        }
//
//        return getPosition();
    }




}
