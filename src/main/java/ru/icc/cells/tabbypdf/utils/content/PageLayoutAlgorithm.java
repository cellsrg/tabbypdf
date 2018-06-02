package ru.icc.cells.tabbypdf.utils.content;

import ru.icc.cells.tabbypdf.entities.Rectangle;
import ru.icc.cells.tabbypdf.entities.Ruling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author aaltaev
 * @since 0.1
 */
public class PageLayoutAlgorithm {
    public static final Comparator<Rectangle> RECTANGLE_COMPARATOR = Comparator.comparing(Rectangle::getTop).reversed()
        .thenComparing(Rectangle::getLeft);

    public static final Comparator<Ruling> LINE_COMPARATOR = Comparator
        .comparing((Ruling ruling) -> ruling.getStartLocation().getX())
        .reversed()
        .thenComparing((Ruling ruling) -> ruling.getEndLocation().getY()).reversed();


    private static class Tuple<T> {
        T left;
        T right;

        Tuple(T left, T right) {
            this.left = left;
            this.right = right;
        }
    }


    public static List<Rectangle> getAllGaps(List<? extends Rectangle> obstacles) {
        List<Rectangle> result = getVerticalGaps(obstacles);
        result.addAll(getHorizontalGaps(obstacles));
        return result;
    }

    /**
     * Just exchange X, Y axis and apply 'getVerticalGaps', then exchange axis back
     */
    public static List<Rectangle> getHorizontalGaps(List<? extends Rectangle> obstacles) {
        return getVerticalGaps(obstacles.stream()
            .map(obstacle -> new Rectangle(
                obstacle.getBottom(),
                obstacle.getLeft(),
                obstacle.getTop(),
                obstacle.getRight())
            )
            .collect(Collectors.toList())
        ).stream()
            .map(obstacle -> new Rectangle(
                obstacle.getBottom(),
                obstacle.getLeft(),
                obstacle.getTop(),
                obstacle.getRight())
            )
            .collect(Collectors.toList());
    }

    /**
     * A 'Simple Algorithm Page Layout Analysis' implementation with some distinctions.
     */
    public static List<Rectangle> getVerticalGaps(List<? extends Rectangle> obstacles) {
        if (obstacles.size() == 0) { // no gaps
            return Collections.emptyList();
        }

        if (obstacles.size() == 1) { // only left & right gaps
            Rectangle boundary = getBoundingRectangle(obstacles);
            List<Rectangle> gaps = new ArrayList<>();
            Rectangle obstacle = obstacles.get(0);
            gaps.add(new Rectangle(boundary.getLeft(), boundary.getBottom(), obstacle.getLeft(), obstacle.getTop()));
            gaps.add(new Rectangle(obstacle.getRight(), obstacle.getBottom(), boundary.getRight(), boundary.getTop()));
            return gaps;
        }

        return getGaps(findRulings(obstacles));
    }

    private static List<Rectangle> getGaps(Tuple<List<Ruling>> rulings) {
        List<Rectangle> gaps = new ArrayList<>();
        for (Iterator<Ruling> rightRulingsIterator = rulings.right.iterator(); rightRulingsIterator.hasNext(); ) {
            Ruling rightRuling = rightRulingsIterator.next();
            for (Iterator<Ruling> leftRulingsIterator = rulings.left.iterator(); leftRulingsIterator.hasNext(); ) {
                Ruling leftRuling = leftRulingsIterator.next();
                if (isRightRulingSameAsLeftRuling(leftRuling, rightRuling)
                    && countRulingsBetweenLeftAndRight(leftRuling, rightRuling, rulings) == 0) {
                    gaps.add(
                        new Rectangle(
                            rightRuling.getEndLocation().getX(),
                            rightRuling.getStartLocation().getY(),
                            leftRuling.getStartLocation().getX(),
                            leftRuling.getEndLocation().getY()
                        )
                    );
                    rightRulingsIterator.remove();
                    leftRulingsIterator.remove();
                    break;
                }
            }
        }
        return gaps;
    }

    private static long countRulingsBetweenLeftAndRight(Ruling left, Ruling right, Tuple<List<Ruling>> rulings) {
        List<Ruling> allRulings = new ArrayList<>(rulings.left);
        allRulings.addAll(rulings.right);
        allRulings.remove(right);
        allRulings.remove(left);
        return allRulings.stream()
            .filter(ruling -> (ruling.getStartLocation().getX() >= right.getEndLocation().getX()
                && ruling.getStartLocation().getX() <= left.getEndLocation().getX())
                && ((ruling.getStartLocation().getY() <= left.getEndLocation().getY()
                && ruling.getStartLocation().getY() >= left.getStartLocation().getY())
                || (ruling.getEndLocation().getY() <= left.getEndLocation().getY()
                && ruling.getEndLocation().getY() >= left.getStartLocation().getY()))
            )
            .count();
    }

    private static boolean isRightRulingSameAsLeftRuling(Ruling left, Ruling right) {
        double xRight    = right.getEndLocation().getX();
        double yTopRight = right.getEndLocation().getY();
        double yBtmRight = right.getStartLocation().getY();
        double xLeft     = left.getEndLocation().getX();
        double yTopLeft  = left.getEndLocation().getY();
        double yBtmLeft  = left.getStartLocation().getY();
        return xRight < xLeft && yTopRight == yTopLeft && yBtmRight == yBtmLeft;
    }

    private static Rectangle getBoundingRectangle(List<? extends Rectangle> obstacles) {
        return new Rectangle(
            getMin(obstacles, Rectangle::getLeft) - 10,
            getMin(obstacles, Rectangle::getBottom),
            getMax(obstacles, Rectangle::getRight) + 10,
            getMax(obstacles, Rectangle::getTop)
        );
    }

    private static Tuple<List<Ruling>> findRulings(List<? extends Rectangle> obstacles) {
        Rectangle       boundary          = getBoundingRectangle(obstacles);
        List<Rectangle> preparedObstacles = getPreparedObstacles(obstacles);

        List<Ruling> leftRulings  = new ArrayList<>();
        List<Ruling> rightRulings = new ArrayList<>();

        // iterate through rectangles from top to bottom (excluding the upper & lower rectangles)
        for (int i = 1; i < preparedObstacles.size() - 1; i++) {
            Rectangle     currentRect = preparedObstacles.get(i);
            Tuple<Double> top         = findUpperRulingsCoordinates(i, preparedObstacles);
            Tuple<Double> btm         = findLowerRulingsCoordinates(i, preparedObstacles);
            if (top != null && btm != null) {
                Ruling leftRuling  = new Ruling(currentRect.getLeft(), btm.left, currentRect.getLeft(), top.left);
                Ruling rightRuling = new Ruling(currentRect.getRight(), btm.right, currentRect.getRight(), top.right);
                if (!leftRulings.contains(leftRuling)) {
                    leftRulings.add(leftRuling);
                }
                if (!rightRulings.contains(rightRuling)) {
                    rightRulings.add(rightRuling);
                }
            }
        }
        leftRulings.add(new Ruling(
            boundary.getRight(),
            boundary.getBottom(),
            boundary.getRight(),
            boundary.getTop()
        ));
        rightRulings.add(new Ruling(
            boundary.getLeft(),
            boundary.getBottom(),
            boundary.getLeft(),
            boundary.getTop()
        ));
        leftRulings.sort(LINE_COMPARATOR);
        rightRulings.sort(LINE_COMPARATOR);
//        List<Ruling> rulings = new ArrayList<>();
//        rulings.addAll(leftRulings);
//        rulings.addAll(rightRulings);
        return new Tuple<>(leftRulings, rightRulings);
    }

    /**
     * Creates sorted list of obstacles with top and bottom boundary rectangles
     */
    private static List<Rectangle> getPreparedObstacles(List<? extends Rectangle> obstacles) {
        Rectangle boundary = getBoundingRectangle(obstacles);
        Rectangle rTop = new Rectangle(boundary.getLeft(), boundary.getTop(), boundary.getRight(), boundary.getTop());
        Rectangle rBtm = new Rectangle(
            boundary.getLeft(),
            boundary.getBottom(),
            boundary.getRight(),
            boundary.getBottom()
        );
        List<Rectangle> r = new ArrayList<>();
        r.add(rTop);
        r.addAll(obstacles);
        r.add(rBtm);
        r.sort(RECTANGLE_COMPARATOR);
        return r;
    }

    private static Tuple<Double> findUpperRulingsCoordinates(int currentRulingIndex, List<Rectangle> rectangles) {
        Rectangle currentRect = rectangles.get(currentRulingIndex);
        Double    yTopLeft    = null;
        Double    yTopRight   = null;
        for (int j = currentRulingIndex - 1; j >= 0; j--) {
            Rectangle upperRect = rectangles.get(j);
            if (currentRect.getTop() <= upperRect.getBottom()) { // upperRect is really upper then currentRect
                if (yTopLeft == null && projectionsIntersectAndTopIsOnTheLeft(upperRect, currentRect)) {
                    yTopLeft = upperRect.getBottom();
                }
                if (yTopRight == null && projectionsIntersectAndTopIsOnTheRight(upperRect, currentRect)) {
                    yTopRight = upperRect.getBottom();
                }
                if (yTopLeft != null && yTopRight != null) {  // found upper rulings coordinates
                    return new Tuple<>(yTopLeft, yTopRight);
                }
            }
        }
        return null;
    }

    private static Tuple<Double> findLowerRulingsCoordinates(int currentRulingIndex, List<Rectangle> rectangles) {
        Rectangle currentRect = rectangles.get(currentRulingIndex);
        Double    yBtmLeft    = null;
        Double    yBtmRight   = null;
        for (int j = currentRulingIndex + 1; j < rectangles.size(); j++) {
            Rectangle lowerRect = rectangles.get(j);
            if (currentRect.getBottom() >= lowerRect.getTop()) { // lowerRect is really lower then currentRect
                if (yBtmLeft == null && projectionsIntersectAndBottomIsOnTheLeft(currentRect, lowerRect)) {
                    yBtmLeft = lowerRect.getTop();
                }
                if (yBtmRight == null && projectionsIntersectAndBottomIsOnTheRight(currentRect, lowerRect)) {
                    yBtmRight = lowerRect.getTop();
                }
            }
            if (yBtmLeft != null && yBtmRight != null) { // found lower rulings coordinates
                return new Tuple<>(yBtmLeft, yBtmRight);
            }
        }
        return null;
    }

    private static boolean projectionsIntersectAndTopIsOnTheLeft(Rectangle top, Rectangle bottom) {
        return top.getLeft() < bottom.getLeft() && bottom.getLeft() < top.getRight();
    }

    private static boolean projectionsIntersectAndTopIsOnTheRight(Rectangle top, Rectangle bottom) {
        return top.getLeft() < bottom.getRight() && bottom.getRight() < top.getRight();
    }

    private static boolean projectionsIntersectAndBottomIsOnTheLeft(Rectangle top, Rectangle bottom) {
        return bottom.getLeft() < top.getLeft() && top.getLeft() < bottom.getRight();
    }

    private static boolean projectionsIntersectAndBottomIsOnTheRight(Rectangle top, Rectangle bottom) {
        return bottom.getLeft() < top.getRight() && top.getRight() < bottom.getRight();
    }

    private static double getMin(List<? extends Rectangle> rectangles, Function<Rectangle, Double> mapper) {
        return rectangles.stream().map(mapper).min(Double::compare).orElse(0.0);
    }

    private static double getMax(List<? extends Rectangle> rectangles, Function<Rectangle, Double> mapper) {
        return rectangles.stream().map(mapper).max(Double::compare).orElse(0.0);
    }
}
