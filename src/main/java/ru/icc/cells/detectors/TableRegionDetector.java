package ru.icc.cells.detectors;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.TableRegion;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.common.TextLine;
import ru.icc.cells.utils.content.PageLayoutAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Detects table regions from text lines
 */
class TableRegionDetector implements Detector<TableRegion, TextLine> {

    /**
     * Threshold value for X-intersection of gaps
     */
    private int   minProjectionIntersection;
    private float minWhitespaceWidth;

    public TableRegionDetector() {
        this(0, 0.1f);
    }

    public TableRegionDetector(int minProjectionIntersection, float minWhitespaceWidth) {
        if (minProjectionIntersection < 0)
            throw new IllegalArgumentException("minProjectionIntersection should be greater or equal 0");
        if (minWhitespaceWidth <= 0 || minWhitespaceWidth >= 1) throw new IllegalArgumentException(
                "minWhitespaceWidth should be in the range from 0 to 1, excluding borders");
        this.minProjectionIntersection = minProjectionIntersection;
        this.minWhitespaceWidth = minWhitespaceWidth;
    }

    /**
     * @return threshold value for X-intersection of gaps
     */
    public int getMinProjectionIntersection() {
        return minProjectionIntersection;
    }

    /**
     * Sets threshold value for X-intersection of gaps
     */
    public void setMinProjectionIntersection(int minProjectionIntersection) {
        this.minProjectionIntersection = minProjectionIntersection;
    }

    public float getMinWhitespaceWidth() {
        return minWhitespaceWidth;
    }

    public void setMinWhitespaceWidth(float minWhitespaceWidth) {
        this.minWhitespaceWidth = minWhitespaceWidth;
    }

    @Override
    public List<TableRegion> detect(List<TextLine> textLines) {
        List<TableRegion> tableRegions = new ArrayList<>();
        TableRegion       tableRegion  = null;
        TextLine          previousLine = null;
        List<TextLine>    tableLines   = textLines.stream().filter(this::isTableLine).collect(Collectors.toList());

        if (tableLines.size() == 0) {
            return new ArrayList<>();
        }
        for (int i = 1; i < tableLines.size(); i++) {
            if (tableRegion == null) {
                tableRegion = new TableRegion();
            }
            previousLine = tableLines.get(i - 1);
            TextLine nextLine = tableLines.get(i);
            tableRegion.add(previousLine);
            if (getCountOfTextLinesBetween(textLines, previousLine, nextLine) != 0) {
                tableRegions.add(tableRegion);
                tableRegion = null;
                continue;
            }
            adjustTextLineWidth(previousLine, nextLine);
            if (!hasGapsIntersections(previousLine, nextLine)/* &&
                TextChunkProcessor.isDistanceLessEqualsHeight(previousLine, nextLine)*/) {
                tableRegions.add(tableRegion);
                tableRegion = null;
            }
        }
        if (tableRegion == null) {
            tableRegion = new TableRegion();
        }
        tableRegion.add(tableLines.get(tableLines.size() - 1));
        tableRegions.add(tableRegion);
        //        restoreTextLinesWidth(tableRegions);
        for (TableRegion region : tableRegions) {
            List<TextBlock> blocks = new ArrayList<>();
            region.getTextLines().forEach(textLine -> blocks.addAll(textLine.getTextBlocks()));
            List<TextBlock> allBlocks =
                    region.getTextLines().stream().map(TextLine::getTextBlocks).reduce((textBlocks, textBlocks2) -> {
                        ArrayList<TextBlock> rectangles = new ArrayList<>();
                        rectangles.addAll(textBlocks);
                        rectangles.addAll(textBlocks2);
                        return rectangles;
                    }).orElse(new ArrayList<>());
            region.getGaps().addAll(/*g(region.getTextLines())*/PageLayoutAlgorithm.getVerticalGaps(allBlocks));
        }
        return tableRegions;
    }

    private boolean whitespaceThreshold(TextLine textLine) {
        float gapWidthSum =
                textLine.getGaps().stream().map(gap -> gap.getRight() - gap.getLeft()).reduce(Float::sum).orElse(0f);
        return gapWidthSum / (textLine.getRight() - textLine.getLeft()) >= minWhitespaceWidth;
    }

    /**
     * Finds count of text lines between two rectangle
     *
     * @param textLines    text lines which will be used for counting
     * @param previousLine upper rectangle
     * @param nextLine     lower rectangle
     */
    public static long getCountOfTextLinesBetween(List<? extends Rectangle> textLines, Rectangle previousLine,
                                                  Rectangle nextLine) {
        List<Rectangle> allTextLines = new ArrayList<>(textLines);
        allTextLines.remove(previousLine);
        allTextLines.remove(nextLine);
        return allTextLines.stream()
                           .filter(rectangle -> rectangle.getTop() <= previousLine.getBottom() &&
                                                rectangle.getBottom() >= nextLine.getTop())
                           .count();
    }

    /**
     * Checks if the text line satisfy table conditions
     */
    private boolean isTableLine(TextLine textLine) {
        if (textLine.getGaps().size() < 3) return false;
        if (!whitespaceThreshold(textLine)) return false;
        for (Rectangle rectangle : textLine.getGaps()) {
            if (rectangle.getBottom() != textLine.getBottom()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the gaps of first text line have X-projection
     * intersections with top gaps of second text line
     */
    private boolean hasGapsIntersections(TextLine textLine1, TextLine textLine2) {
        first_text_line_gap:
        for (Rectangle gap1 : textLine1.getGaps()) {
            for (Rectangle gap2 : topGaps(textLine2)) {
                if (wp(gap1, gap2) >= minProjectionIntersection) {
                    continue first_text_line_gap;
                }
            }
            return false;
        }
        return true;
    }


    /**
     * @return positive if gaps has intersection, negative otherwise
     */
    public static float wp(Rectangle gap1, Rectangle gap2) {
        return Float.min(gap1.getRight(), gap2.getRight()) - Float.max(gap1.getLeft(), gap2.getLeft());
    }

    /**
     * @return gaps which top coordinates are the same as text line top coordinate
     */
    private List<Rectangle> topGaps(TextLine textLine) {
        return textLine.getGaps()
                       .stream()
                       .filter(rectangle -> rectangle.getTop() == textLine.getTop())
                       .collect(Collectors.toList());
    }

    /**
     * Adjusts text lines widths
     */
    private void adjustTextLineWidth(TextLine textLine1, TextLine textLine2) {
        textLine1.getGaps().sort(PageLayoutAlgorithm.RECTANGLE_COMPARATOR);
        textLine2.getGaps().sort(PageLayoutAlgorithm.RECTANGLE_COMPARATOR);

        if (textLine1.getLeft() > textLine2.getLeft()) {
            textLine1.getGaps().get(0).setLeft(textLine2.getLeft());
            textLine1.setLeft(textLine2.getLeft());
        } else if (textLine1.getLeft() < textLine2.getLeft()) {
            textLine2.getGaps().get(0).setLeft(textLine1.getLeft());
            textLine2.setLeft(textLine1.getLeft());
        }

        if (textLine1.getRight() > textLine2.getRight()) {
            textLine2.getGaps().get(textLine2.getGaps().size() - 1).setRight(textLine1.getRight());
            textLine2.setRight(textLine1.getRight());
        } else if (textLine1.getRight() < textLine2.getRight()) {
            textLine1.getGaps().get(textLine1.getGaps().size() - 1).setRight(textLine2.getRight());
            textLine1.setRight(textLine2.getRight());
        }
    }

    /**
     * Restores text lines widths
     */
    private void restoreTextLinesWidth(List<TableRegion> tableRegions) {
        for (TableRegion region : tableRegions) {
            for (TextLine textLine : region.getTextLines()) {
                if (textLine.getLeft() != region.getLeft()) {
                    textLine.setLeft(region.getLeft());
                }
                if (textLine.getRight() != region.getRight()) {
                    textLine.setRight(region.getRight());
                }
                textLine.getGaps().sort(PageLayoutAlgorithm.RECTANGLE_COMPARATOR);
                if (textLine.getGaps().get(0).getLeft() != region.getLeft()) {
                    textLine.getGaps().get(0).setLeft(region.getLeft());
                }
                if (textLine.getGaps().get(textLine.getGaps().size() - 1).getRight() != region.getRight()) {
                    textLine.getGaps().get(textLine.getGaps().size() - 1).setRight(region.getRight());
                }
            }
        }
    }

    /**
     * @return vertical gaps of table region represented by a list of text lines
     */
    private List<Rectangle> g(List<TextLine> lines) {
        if (lines.size() == 0) {
            return Collections.emptyList();
        }
        if (lines.size() == 1) {
            return lines.get(0).getGaps();
        } else {
            List<TextLine> body = lines.subList(0, lines.size() - 1);
            List<TextLine> last = lines.subList(lines.size() - 1, lines.size());
            return app(g(body), g(last), last.get(0));
        }
    }

    /**
     * Joins the gaps
     *
     * @param lineGaps     gap list to be joined
     * @param lastLineGaps gap list to be joined
     */
    private List<Rectangle> app(List<Rectangle> lineGaps, List<Rectangle> lastLineGaps, TextLine line) {
        List<Rectangle> result = new ArrayList<>();
        for (Rectangle lineGap : lineGaps) {
            List<Rectangle> replacement = lastLineGaps.stream()
                                                      .filter(gap -> gap.getTop() == line.getTop() &&
                                                                     wp(lineGap, gap) >= this.minProjectionIntersection)
                                                      .map(gap -> new Rectangle(
                                                              Float.max(lineGap.getLeft(), gap.getLeft()),
                                                              gap.getBottom(),
                                                              Float.min(lineGap.getRight(), gap.getRight()),
                                                              lineGap.getTop()))
                                                      .collect(Collectors.toList());
            if (replacement.isEmpty()) {
                result.add(lineGap);
            } else {
                result.addAll(replacement);
            }
        }
        return result;
    }

}
