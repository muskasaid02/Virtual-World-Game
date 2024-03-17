import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class BadDude extends Actions implements Movable, Transformable {

    public static final String BAD_DUDE_KEY = "bad_dude";

    private boolean hasExplosive;

    private int dudesKilled;

    private static final PathingStrategy pathingStrategy = new AStarPathingStrategy();

    public BadDude(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod, boolean hasExplosive) {
        super(id, position, images, animationPeriod, behaviorPeriod);
        this.hasExplosive = hasExplosive;
    }

    @Override
    public void updateImage() {
        setImageIndex(getImageIndex() + 1);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduleAnimation(scheduler, world, imageLibrary);
        scheduleBehavior(scheduler, world, imageLibrary);
    }

    @Override
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        Optional<Entity> dudeTarget = findTarget(world);

        dudeTarget.ifPresent(entity -> {
            if (moveTo(world, entity, scheduler)) {
                transform(world, imageLibrary, scheduler);
            }
        });

        scheduleBehavior(scheduler, world, imageLibrary);
    }

    public Optional<Entity> findTarget(World world) {
        List<Class<? extends Entity>> potentialTargets;

        if (hasExplosive) {
            potentialTargets = List.of(House.class);
        } else {
            potentialTargets = List.of(Dude.class);
        }
        return world.findNearest(getPosition(), potentialTargets);
    }

    @Override
    public boolean transform(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
         Point explodedPoint = PathingStrategy.CARDINAL_NEIGHBORS
                .apply(getPosition())
                .filter(world::inBounds)
                .map(world::getOccupant)
                 .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(entity -> entity instanceof House)
                .peek(x -> {
                    Explosion explosion = new Explosion(Explosion.EXPLOSION_KEY, x.getPosition(), imageLibrary.get(Explosion.EXPLOSION_KEY));

                    world.setBackgroundCell(x.getPosition(), new Background("exploded", imageLibrary.get("exploded"), 0));
                    long unused = PathingStrategy.CARDINAL_NEIGHBORS.apply(x.getPosition()).filter(world::inBounds).peek(point -> {
                        world.setBackgroundCell(point, new Background("road", imageLibrary.get("road"), 0));
                    }).count();
                    world.removeEntity(scheduler, x);
                    world.addEntity(explosion);

                    explosion.scheduleActions(scheduler, world, imageLibrary);

                })
                 .map(Entity::getPosition)
                 .toList()
                 .getFirst();

         return true;
    }

    @Override
    public boolean moveTo(World world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacentTo(target.getPosition())) {
            if (target instanceof House) {
                hasExplosive = false;
                return true;
            } else if (target instanceof Dude) {
                dudesKilled++;
                world.removeEntity(scheduler, target);
            }
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
        }
        return false;
    }

    @Override
    public Point nextPosition(World world, Point destination) {
        Predicate<Point> canPassThrough = point -> world.inBounds(point) && (!world.isOccupied(point) || (world.getOccupant(point).get() instanceof Stump));
        BiPredicate<Point, Point> withinReach = Point::adjacentTo;
        List<Point> path = pathingStrategy.computePath(getPosition(), destination, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);

        return !path.isEmpty() ? path.getFirst() : getPosition();
    }
}
