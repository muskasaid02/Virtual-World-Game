import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy {

    /**
     * Return a list containing a single point representing the next step toward a goal
     * If the start is within reach of the goal, the returned list is empty.
     *
     * @param start the point to begin the search from
     * @param end the point to search for a point within reach of
     * @param canPassThrough a function that returns true if the given point is traversable
     * @param withinReach a function that returns true if both points are within reach of each other
     * @param potentialNeighbors a function that returns the neighbors of a given point, as a stream
     */
    public List<Point> computePath(
            Point start,
            Point end,
            Predicate<Point> canPassThrough,
            BiPredicate<Point, Point> withinReach,
            Function<Point, Stream<Point>> potentialNeighbors
    ) {
        Map<Point, Point> cameFrom = new HashMap<>();
        Map<Point, Integer> gScore = new HashMap<>();
        Map<Point, Integer> hScore = new HashMap<>();
        Queue<Point> openSet = new PriorityQueue<>(Comparator.comparingInt(x -> gScore.getOrDefault(x, 1000) + hScore.getOrDefault(x, 1000)));
        List<Point> path = new LinkedList<>();
        Set<Point> closedSet = new HashSet<>();

        openSet.add(start);
        gScore.put(start, 0);
        hScore.put(start, start.manhattanDistanceTo(end));

        while (!openSet.isEmpty()) {
            Point current = openSet.poll();
            if (withinReach.test(end, current)) {
                path = buildPath(cameFrom, current);
                break;
            }
            closedSet.add(current);
            List<Point> neighbors = potentialNeighbors.apply(current)
                    .filter(canPassThrough)
                    .filter(x -> !closedSet.contains(x))
                    .toList();
            for (Point p : neighbors) {
                if (gScore.getOrDefault(p, 1000) > gScore.getOrDefault(current, 1000) + 1){
                    cameFrom.put(p, current);
                    gScore.put(p, gScore.getOrDefault(current, 1000) + 1);
                    hScore.put(p, p.manhattanDistanceTo(end));
                    if (!openSet.contains(p)){
                        openSet.add(p);
                    }
                }
            }
        }
        if (path.size() <= 1){
            return new LinkedList<>();
        }
        path.removeFirst();
        return path;
    }

    public List<Point> buildPath(Map<Point, Point> cameFrom, Point current){
        List<Point> output = new LinkedList<>();
        output.add(current);
        while (cameFrom.containsKey(current)){
            current = cameFrom.get(current);
            output.addFirst(current);
        }
        return output;
    }


}
