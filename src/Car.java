import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Car extends Actions implements Movable, Transformable{

    public static final String CAR_KEY = "car";

    private boolean full;

    private Dude passenger;

    private int cooldown;

    private static final PathingStrategy pathingStrategy = new AStarPathingStrategy();

    public Car(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod) {
        super(id, position, images, animationPeriod, behaviorPeriod);
        full = false;
        passenger = null;
        cooldown = 0;
    }

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
        Optional<Entity> carTarget = findTarget(world);
        boolean moveTrue = carTarget.filter(entity -> moveTo(world, entity, scheduler)).isPresent();
        if (moveTrue) {
            transform(world, imageLibrary, scheduler);
        }
        scheduleBehavior(scheduler, world, imageLibrary);
    }

    public Optional<Entity> findTarget(World world) {
        List<Class<? extends Entity>> potentialTargets;

        if (full) {
            potentialTargets = List.of(Mushroom.class);
        } else if (cooldown > 0) {
            cooldown--;
            potentialTargets = List.of(House.class);
        } else {
            potentialTargets = List.of(Dude.class);
        }
        return world.findNearest(getPosition(), potentialTargets);
    }

    @Override
    public boolean transform(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        if (full) {
            Dude dude = new Dude(passenger.getId(), passenger.getPosition(), imageLibrary.get(Dude.DUDE_KEY), passenger.getAnimationPeriod(), passenger.getBehaviorPeriod(), passenger.getResourceCount(), passenger.getResourceLimit());

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageLibrary);
            passenger = null;
            full = false;
            cooldown = (int) (1/getBehaviorPeriod()) * 5;

            return true;
        }
        return false;
    }

    @Override
    public boolean moveTo(World world, Entity target, EventScheduler scheduler) {
        List<Point> clearPositions = PathingStrategy.CARDINAL_NEIGHBORS.apply(this.getPosition()).filter(point -> world.inBounds(point) && (!world.isOccupied(point) || (world.getOccupant(point).get() instanceof Stump))).toList();
        if (getPosition().adjacentTo(target.getPosition())) {
            if (target instanceof Dude && !full) {
                full = true;
                passenger = (Dude) target;
                world.removeEntity(scheduler, target);
                return false;
            } else if (target instanceof Mushroom && full && !clearPositions.isEmpty()) {
                passenger.setPosition(clearPositions.getFirst());
                return true;
            }
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }

            return false;
        }
        return false;
    }

    @Override
    public Point nextPosition(World world, Point destination) {
        Predicate<Point> canPassThrough = point -> world.inBounds(point) && (!world.isOccupied(point) || (world.getOccupant(point).get() instanceof Fairy));
        BiPredicate<Point, Point> withinReach = Point::adjacentTo;
        List<Point> path = pathingStrategy.computePath(getPosition(), destination, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);

        return !path.isEmpty() ? path.getFirst() : getPosition();
    }
}