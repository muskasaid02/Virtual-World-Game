import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Fairy extends Actions implements Movable {
    public static final String FAIRY_KEY = "fairy";
    public static final int FAIRY_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX = 0;
    public static final int FAIRY_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX = 1;
    public static final int FAIRY_PARSE_PROPERTY_COUNT = 2;

    private static final PathingStrategy pathingStrategy = new AStarPathingStrategy();

    public Fairy(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod) {
        super(id, position, images, animationPeriod, behaviorPeriod);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduleAnimation(scheduler, world, imageLibrary);
        scheduleBehavior(scheduler, world, imageLibrary);
    }

    @Override
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(getPosition(), new ArrayList<>(List.of(Stump.class)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (moveTo(world, fairyTarget.get(), scheduler)) {
                Sapling sapling = new Sapling(Sapling.SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos, imageLibrary.get(Sapling.SAPLING_KEY));

                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageLibrary);
            }
        }

        scheduleBehavior(scheduler, world, imageLibrary);
    }

    @Override
    public void updateImage() {
        setImageIndex(getImageIndex() + 1);
    }

    @Override
    public boolean moveTo(World world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacentTo(target.getPosition())) {
            world.removeEntity(scheduler, target);
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
        Predicate<Point> canPassThrough = point -> world.inBounds(point) && !world.isOccupied(point);
        BiPredicate<Point, Point> withinReach = Point::adjacentTo;
        List<Point> path = pathingStrategy.computePath(getPosition(), destination, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);

        return !path.isEmpty() ? path.getFirst() : getPosition();
    }
//    @Override
//    public Point nextPosition(World world, Point destination) {
//        // Differences between the destination and current position along each axis
//        int deltaX = destination.x - getPosition().x;
//        int deltaY = destination.y - getPosition().y;
//
//        // Positions one step toward the destination along each axis
//        Point horizontalPosition = new Point(getPosition().x + Integer.signum(deltaX), getPosition().y);
//        Point verticalPosition = new Point(getPosition().x, getPosition().y + Integer.signum(deltaY));
//
//        // Assumes all destinations are within bounds of the world
//        // If this is not the case, also check 'World.withinBounds()'
//        boolean horizontalOccupied = world.isOccupied(horizontalPosition);
//        boolean verticalOccupied = world.isOccupied(verticalPosition);
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
//    }}
}
