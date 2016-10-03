package ru.icc.cells.utils.content;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.Ruling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Андрей on 26.09.2016.
 */
public class PageLayoutAlgorithm {
    public static Comparator<Rectangle> RECTANGLE_COMPARATOR = (rect1, rect2) -> {
        if (rect1.getTop() > rect2.getTop()) {
            return -1;
        } else if (rect1.getTop() == rect2.getTop()) {
            if (rect1.getLeft() < rect2.getLeft()) {
                return -1;
            } else if (rect1.getLeft() == rect2.getLeft()) {
                return 0;
            }
        }
        return 1;
    };

    private static Comparator<Ruling> lineComparator = (line1, line2) -> {
        double x1  = line1.getStartLocation().getX();
        double x2  = line2.getStartLocation().getX();
        double yt1 = line1.getEndLocation().getY();
        double yt2 = line2.getEndLocation().getY();
        if (x1 < x2) {
            return -1;
        } else if (x1 == x2) {
            if (yt1 > yt2) {
                return -1;
            } else if (yt1 == yt2) {
                return 0;
            }
        }
        return 1;
    };

    public static List<Rectangle> getAllGaps(List<? extends Rectangle> obstacles) {
        List<Rectangle> result = getVerticalGaps(obstacles);
        result.addAll(getHorizontalGaps(obstacles));
        return result;
    }

    /**
     * Just exchange X, Y axis and apply 'getVerticalGaps', then exchange axis back
     */
    public static List<Rectangle> getHorizontalGaps(List<? extends Rectangle> obstacles) {
        List<Rectangle> mappedRectangles;
        mappedRectangles = obstacles.stream()
                                    .map(obstacle -> new Rectangle(obstacle.getBottom(), obstacle.getLeft(),
                                                                   obstacle.getTop(), obstacle.getRight()))
                                    .collect(Collectors.toList());
        mappedRectangles = getVerticalGaps(mappedRectangles);
        return mappedRectangles.stream()
                               .map(obstacle -> new Rectangle(obstacle.getBottom(), obstacle.getLeft(),
                                                              obstacle.getTop(), obstacle.getRight()))
                               .collect(Collectors.toList());
    }

    /**
     * A 'Simple Algorithm Page Layout Analysis' implementation with some distinctions.
     */
    public static List<Rectangle> getVerticalGaps(List<? extends Rectangle> obstacles) {
        Rectangle boundary = getBoundingRectangle(obstacles);
        Rectangle rTop     =
                new Rectangle(boundary.getLeft(), boundary.getTop(), boundary.getRight(), boundary.getTop());
        Rectangle rBtm =
                new Rectangle(boundary.getLeft(), boundary.getBottom(), boundary.getRight(), boundary.getBottom());
        List<Rectangle> r = new ArrayList<>();
        r.add(rTop);
        r.addAll(obstacles);
        r.add(rBtm);
        r.sort(RECTANGLE_COMPARATOR);

        List<Ruling> leftRulings  = new ArrayList<>();
        List<Ruling> rightRulings = new ArrayList<>();
        for (int i = 1; i < r.size() - 1; i++) {
            Float yTopLeft = null, yTopRight = null, yBtmLeft = null, yBtmRight = null;
            for (int j = i - 1; j >= 0; j--) {
                if (r.get(i).getTop() <= r.get(j).getBottom()) {
                    if (yTopLeft == null && r.get(j).getLeft() <= r.get(i).getLeft() &&
                        r.get(i).getLeft() <= r.get(j).getRight()) {
                        yTopLeft = r.get(j).getBottom();
                    }
                    if (yTopRight == null && r.get(j).getLeft() <= r.get(i).getRight() &&
                        r.get(i).getRight() <= r.get(j).getRight()) {
                        yTopRight = r.get(j).getBottom();
                    }
                    if (yTopLeft != null && yTopRight != null) break;
                }
            }
            for (int j = i + 1; j < r.size(); j++) {
                if (r.get(i).getBottom() >= r.get(j).getTop()) {
                    if (yBtmLeft == null && r.get(j).getLeft() <= r.get(i).getLeft() &&
                        r.get(i).getLeft() <= r.get(j).getRight()) {
                        yBtmLeft = r.get(j).getTop();
                    }
                }
                if (yBtmRight == null && r.get(j).getLeft() <= r.get(i).getRight() &&
                    r.get(i).getRight() <= r.get(j).getRight()) {
                    yBtmRight = r.get(j).getTop();
                }
                if (yBtmLeft != null && yBtmRight != null) break;
            }
            Ruling leftRuling  = new Ruling(r.get(i).getLeft(), yBtmLeft, r.get(i).getLeft(), yTopLeft);
            Ruling rightRuling = new Ruling(r.get(i).getRight(), yBtmRight, r.get(i).getRight(), yTopRight);
            if (!leftRulings.contains(leftRuling)) {
                leftRulings.add(leftRuling);
            }
            if (!rightRulings.contains(rightRuling)) {
                rightRulings.add(rightRuling);
            }
        }
        Ruling leftRuling = new Ruling(boundary.getLeft(), boundary.getBottom(), boundary.getLeft(), boundary.getTop());
        Ruling rightRuling =
                new Ruling(boundary.getRight(), boundary.getBottom(), boundary.getRight(), boundary.getTop());
        leftRulings.add(rightRuling);
        rightRulings.add(leftRuling);
        leftRulings.sort(lineComparator);
        rightRulings.sort(lineComparator);

        List<Rectangle> gaps = new ArrayList<>();
        for (int i = 0; i < rightRulings.size(); i++) {
            double xRight    = rightRulings.get(i).getEndLocation().getX();
            double yTopRight = rightRulings.get(i).getEndLocation().getY();
            double yBtmRight = rightRulings.get(i).getStartLocation().getY();
            for (int j = 0; j < leftRulings.size(); j++) {
                double xLeft    = leftRulings.get(j).getEndLocation().getX();
                double yTopLeft = leftRulings.get(j).getEndLocation().getY();
                double yBtmLeft = leftRulings.get(j).getStartLocation().getY();
                if (xRight < xLeft && yTopRight == yTopLeft && yBtmRight == yBtmLeft) {

                    List<Ruling> allRulings = new ArrayList<>(leftRulings);
                    allRulings.addAll(rightRulings);
                    allRulings.remove(rightRulings.get(i));
                    allRulings.remove(leftRulings.get(j));
                    long count = allRulings.stream()
                                           .filter(ruling -> (ruling.getStartLocation().getX() >= xRight &&
                                                              ruling.getStartLocation().getX() <= xLeft) &&
                                                             ((ruling.getStartLocation().getY() <= yTopLeft &&
                                                               ruling.getStartLocation().getY() >= yBtmLeft) ||
                                                              (ruling.getEndLocation().getY() <= yTopLeft &&
                                                               ruling.getEndLocation().getY() >= yBtmLeft)))
                                           .count();
                    double right = leftRulings.get(j).getStartLocation().getX();
                    if (count == 0) {
                        Rectangle gap =
                                new Rectangle((float) xRight, (float) yBtmRight, (float) right, (float) yTopLeft);
                        gaps.add(gap);
                        rightRulings.remove(i--);
                        leftRulings.remove(j);
                        break;
                    }
                }
            }
        }
        return gaps;
    }

    private static Rectangle getBoundingRectangle(List<? extends Rectangle> obstacles) {
        Float left   = obstacles.stream().map(Rectangle::getLeft).min(Float::compare).orElse(0f) - 10;
        Float bottom = obstacles.stream().map(Rectangle::getBottom).min(Float::compare).orElse(0f);
        Float right  = obstacles.stream().map(Rectangle::getRight).max(Float::compare).orElse(0f) + 10;
        Float top    = obstacles.stream().map(Rectangle::getTop).max(Float::compare).orElse(0f);
        return new Rectangle(left, bottom, right, top);
    }
}
