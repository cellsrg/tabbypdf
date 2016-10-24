package ru.icc.cells.detectors;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.TableBox;
import ru.icc.cells.common.TableRegion;
import ru.icc.cells.common.TextLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects the bounding of table
 */
class TableBoxDetector implements Detector<TableBox, TableRegion> {

    private List<TextLine> pageTextLines;
    private int            maxNonTableLinesBetweenRegions;
    private int            minProjectionIntersection;
    private double         gapThreshold;

    public TableBoxDetector(List<TextLine> pageTextLines) {
        this(pageTextLines, 2, 0, 0.8);
    }

    public TableBoxDetector(List<TextLine> pageTextLines, int maxNonTableLinesBetweenRegions,
                            int minProjectionIntersection, double gapThreshold) {
        if (minProjectionIntersection < 0)
            throw new IllegalArgumentException("minProjectionIntersection should be greater or equal 0");
        if (maxNonTableLinesBetweenRegions < 0)
            throw new IllegalArgumentException("maxNonTableLinesBetweenRegions should be greater or equal 0");
        if (gapThreshold <= 0 || gapThreshold > 1) throw new IllegalArgumentException(
                "gapThreshold should be in the range from 0 to 1, excluding left border");
        this.pageTextLines = pageTextLines;
        this.maxNonTableLinesBetweenRegions = maxNonTableLinesBetweenRegions;
        this.minProjectionIntersection = minProjectionIntersection;
        this.gapThreshold = gapThreshold;
    }

    public int getMaxNonTableLinesBetweenRegions() {
        return maxNonTableLinesBetweenRegions;
    }

    public void setMaxNonTableLinesBetweenRegions(int maxNonTableLinesBetweenRegions) {
        this.maxNonTableLinesBetweenRegions = maxNonTableLinesBetweenRegions;
    }

    public int getMinProjectionIntersection() {
        return minProjectionIntersection;
    }

    public void setMinProjectionIntersection(int minProjectionIntersection) {
        this.minProjectionIntersection = minProjectionIntersection;
    }

    public double getGapThreshold() {
        return gapThreshold;
    }

    public void setGapThreshold(double gapThreshold) {
        this.gapThreshold = gapThreshold;
    }

    @Override
    public List<TableBox> detect(List<TableRegion> tableRegions) {
        List<TableRegion> regions    = new ArrayList<>(tableRegions);
        List<TableBox>    tableBoxes = new ArrayList<>();
        TableBox          tableBox   = null;
        TableRegion       prevRegion = null;

        if (regions.size() == 1) {
            tableBox = new TableBox();
            tableBox.add(regions.get(0));
            tableBoxes.add(tableBox);
            return tableBoxes;
        } else if (regions.size() == 0) {
            return tableBoxes;
        }

        for (int i = 1; i < regions.size(); i++) {
            if (tableBox == null) {
                tableBox = new TableBox();
            }
            prevRegion = regions.get(i - 1);
            TableRegion nextRegion = regions.get(i);
            tableBox.add(prevRegion);

            if (!(TableRegionDetector.getCountOfTextLinesBetween(pageTextLines, prevRegion, nextRegion) <=
                  maxNonTableLinesBetweenRegions &&
                  tcorr(prevRegion, nextRegion) / prevRegion.getGaps().size() >= gapThreshold)) {
                tableBoxes.add(tableBox);
                tableBox = null;
            }
        }


        if (tableBox == null) {
            tableBox = new TableBox();
        }
        tableBox.add(regions.get(regions.size() - 1));
        tableBoxes.add(tableBox);

        tableBoxes.removeIf(
                tb -> tb.getTableRegions().size() < 2 && tb.getTableRegions().get(0).getTextLines().size() < 2);

        return tableBoxes;
    }

    private int gcorr(Rectangle gap, TableRegion region) {
        for (Rectangle regionGap : region.getGaps()) {
            if (TableRegionDetector.wp(gap, regionGap) > minProjectionIntersection) return 1;
        }
        return 0;
    }

    private int tcorr(TableRegion region1, TableRegion region2) {
        int result = 0;
        for (Rectangle firstRegionGap : region1.getGaps()) {
            result += gcorr(firstRegionGap, region2);
        }
        return result;
    }
}
