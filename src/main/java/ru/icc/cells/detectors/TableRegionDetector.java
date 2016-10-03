package ru.icc.cells.detectors;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.TableRegion;
import ru.icc.cells.common.TextLine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Андрей on 03.10.2016.
 */
public class TableRegionDetector implements Detector<TableRegion> {
    @Override
    public List<TableRegion> detect(List<? extends Rectangle> textLines) {
        List<TableRegion> tableRegions = new ArrayList<>();
        TableRegion       tableRegion  = null;
        TextLine          previousLine = null;

        outer1:
        for (int i = 1; i < textLines.size(); i++) {
            if (tableRegion == null) {
                tableRegion = new TableRegion();
            }
            previousLine = (TextLine) textLines.get(i - 1);
            TextLine currLine = (TextLine) textLines.get(i);
            if (isTableLine(previousLine)) {
                tableRegion.add(previousLine);
            }else {
                continue;
            }
            adjustTextLineWidth(previousLine, currLine);
            if (isTableLine(currLine)) {
                outer2:
                for (Rectangle prevLineGap : previousLine.getGaps()) {
                    for (Rectangle currLineTopGap : topGaps(currLine)) {
                        if (wp(prevLineGap, currLineTopGap) > 0) {
                            continue outer2;
                        }
                    }
                    tableRegions.add(tableRegion);
                    tableRegion = null;
                    continue outer1;
                }
            }
        }

        TextLine lastLine = (TextLine) textLines.get(textLines.size() - 1);
        if (tableRegion != null) {
            tableRegions.add(tableRegion);
        }
        if (isTableLine(lastLine)) {
            if (tableRegion == null) {
                tableRegion = new TableRegion();
                tableRegion.add(lastLine);
                tableRegions.add(tableRegion);
            } else {
                tableRegion.add(lastLine);
            }
        }
        return tableRegions;
    }

    private boolean isTableLine(TextLine textLine) {
        if (textLine.getGaps().size() < 3) return false;
        for (Rectangle rectangle : textLine.getGaps()) {
            if (rectangle.getBottom() != textLine.getBottom()) {
                return false;
            }
        }
        return true;
    }

    private float wp(Rectangle gap1, Rectangle gap2) {
        return Float.min(gap1.getRight(), gap2.getRight()) - Float.max(gap1.getLeft(), gap2.getLeft());
    }

    private List<Rectangle> topGaps(TextLine textLine) {
        return textLine.getGaps()
                       .stream()
                       .filter(rectangle -> rectangle.getTop() == textLine.getTop())
                       .collect(Collectors.toList());
    }

    private void adjustTextLineWidth(TextLine textLine1, TextLine textLine2) {
        if (textLine1.getLeft() > textLine2.getLeft()) {
            textLine1.getGaps()
                     .stream()
                     .filter(rectangle -> rectangle.getLeft() == textLine1.getLeft())
                     .collect(Collectors.toList())
                     .get(0)
                     .setLeft(textLine2.getLeft());
            textLine1.setLeft(textLine2.getLeft());
        } else if (textLine1.getLeft() < textLine2.getLeft()) {
            textLine2.getGaps()
                     .stream()
                     .filter(rectangle -> rectangle.getLeft() == textLine2.getLeft())
                     .collect(Collectors.toList())
                     .get(0)
                     .setLeft(textLine1.getLeft());
            textLine2.setLeft(textLine1.getLeft());
        }

        if (textLine1.getRight() > textLine2.getRight()) {
            textLine2.getGaps()
                     .stream()
                     .filter(rectangle -> rectangle.getRight() == textLine2.getRight())
                     .collect(Collectors.toList())
                     .get(0)
                     .setRight(textLine1.getRight());
            textLine2.setRight(textLine1.getRight());
        } else if (textLine1.getRight() < textLine2.getRight()) {
            textLine1.getGaps()
                     .stream()
                     .filter(rectangle -> rectangle.getRight() == textLine1.getRight())
                     .collect(Collectors.toList())
                     .get(0)
                     .setRight(textLine2.getRight());
            textLine1.setRight(textLine2.getRight());
        }
    }
}
