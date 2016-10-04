package ru.icc.cells.detectors;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.TableBounding;
import ru.icc.cells.common.TableRegion;
import ru.icc.cells.common.TextLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects the bounding of table
 */
public class TableBoundingDetector implements Detector<TableBounding,TableRegion> {

    private List<TextLine> pageTextLines;
    private int            maxNonTableLinesBetweenRegions;
    private int            minProjectionIntersection;
    private double         gapThreshold;

    public TableBoundingDetector(List<TextLine> pageTextLines) {
        this(pageTextLines, 2, 0, 0.8);
    }

    public TableBoundingDetector(List<TextLine> pageTextLines, int maxNonTableLinesBetweenRegions,
                                 int minProjectionIntersection, double gapThreshold) {
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
    public List<TableBounding> detect(List<TableRegion> regions) {
        List<TableBounding> tableBoundings = new ArrayList<>();
        TableBounding       tableBounding  = null;
        TableRegion         prevRegion     = null;

        if (regions.size() == 1) {
            tableBounding = new TableBounding();
            tableBounding.add(regions.get(0));
            tableBoundings.add(tableBounding);
            return tableBoundings;
        }else if (regions.size()==0){
            return tableBoundings;
        }

        for (int i = 1; i < regions.size(); i++) {
            if (tableBounding == null) {
                tableBounding = new TableBounding();
            }
            prevRegion = regions.get(i - 1);
            TableRegion nextRegion = regions.get(i);
            tableBounding.add(prevRegion);

            if (!(TableRegionDetector.getCountOfTextLinesBetween(pageTextLines, prevRegion, nextRegion) <=
                  maxNonTableLinesBetweenRegions &&
                  tcorr(prevRegion, nextRegion) / prevRegion.getGaps().size() >= gapThreshold)) {
                if (!(tableBounding.getTableRegions().size() == 1 &&
                      tableBounding.getTableRegions().get(0).getTextLines().size() == 1)) {
                    tableBoundings.add(tableBounding);
                }
                tableBounding = null;
            }
        }


        if (tableBounding == null) {
            tableBounding = new TableBounding();
        }
        tableBounding.add(regions.get(regions.size() - 1));
        tableBoundings.add(tableBounding);

        return tableBoundings;
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
