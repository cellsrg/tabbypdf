package ru.icc.cells.tabbypdf.detectors;

import ru.icc.cells.tabbypdf.common.*;
import ru.icc.cells.tabbypdf.utils.processing.filter.bi.HeightBiHeuristic;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects the bounding of table
 */
class TableBoxDetector implements Detector<TableBox, TableRegion>
{

    private List<TextLine> pageTextLines;
    private int            maxNonTableLinesBetweenRegions = 2;
    private int            minProjectionIntersection = 0;
    private double         gapThreshold = 0.8;
    private double         maxDistanceBetweenRegions = 48;

    public TableBoxDetector(List<TextLine> pageTextLines)
    {
        this.pageTextLines = pageTextLines;
    }

    public TableBoxDetector(List<TextLine> pageTextLines, int maxNonTableLinesBetweenRegions,
                            int minProjectionIntersection, double gapThreshold, double maxDistanceBetweenRegions)
    {
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
        this.maxDistanceBetweenRegions = maxDistanceBetweenRegions;
    }

    public int getMaxNonTableLinesBetweenRegions()
    {
        return maxNonTableLinesBetweenRegions;
    }

    public void setMaxNonTableLinesBetweenRegions(int maxNonTableLinesBetweenRegions)
    {
        this.maxNonTableLinesBetweenRegions = maxNonTableLinesBetweenRegions;
    }

    public int getMinProjectionIntersection()
    {
        return minProjectionIntersection;
    }

    public void setMinProjectionIntersection(int minProjectionIntersection)
    {
        this.minProjectionIntersection = minProjectionIntersection;
    }

    public double getGapThreshold()
    {
        return gapThreshold;
    }

    public void setGapThreshold(double gapThreshold)
    {
        this.gapThreshold = gapThreshold;
    }

    @Override
    public List<TableBox> detect(List<TableRegion> tableRegions)
    {
        List<TableRegion> regions    = new ArrayList<>(tableRegions);
        List<TableBox>    tableBoxes = new ArrayList<>();
        TableBox          tableBox   = null;
        TableRegion       prevRegion = null;

        if (regions.size() == 1)
        {
            if (regions.get(0).getTextLines().size() > 1)
            {
                tableBox = new TableBox();
                tableBox.add(regions.get(0));
                tableBoxes.add(tableBox);
            }
            return tableBoxes;
        }
        else if (regions.size() == 0)
        {
            return tableBoxes;
        }

        for (int i = 1; i < regions.size(); i++)
        {
            if (tableBox == null)
            {
                tableBox = new TableBox();
            }
            prevRegion = regions.get(i - 1);
            TableRegion nextRegion = regions.get(i);
            tableBox.add(prevRegion);

            if ((!(TableRegionDetector.getCountOfTextLinesBetween(pageTextLines, prevRegion, nextRegion) <=
                   maxNonTableLinesBetweenRegions &&
                   tcorr(prevRegion, nextRegion) / (double) prevRegion.getGaps().size() >= gapThreshold)) ||
                Math.abs(prevRegion.getBottom() - nextRegion.getTop()) > maxDistanceBetweenRegions)
            {
                tableBoxes.add(tableBox);
                tableBox = null;
            }
        }


        if (tableBox == null)
        {
            tableBox = new TableBox();
        }
        tableBox.add(regions.get(regions.size() - 1));
        tableBoxes.add(tableBox);

        tableBoxes.removeIf(
                tb -> tb.getTableRegions().size() < 2 && tb.getTableRegions().get(0).getTextLines().size() < 2);

        mergeCloseLocatedBoxes(tableBoxes);

        return tableBoxes;
    }

    private void mergeCloseLocatedBoxes(List<TableBox> tableBoxes)
    {
        for (int i = 0; i < tableBoxes.size() - 1; i++)
        {
            TableBox currBox = tableBoxes.get(i);
            TableBox nextBox = tableBoxes.get(i + 1);

            TableRegion currReg = currBox.getTableRegions().get(currBox.getTableRegions().size() - 1);
            TableRegion nextReg = nextBox.getTableRegions().get(0);

            TextLine currLine = currReg.getTextLines().get(currReg.getTextLines().size() - 1);
            TextLine nextLine = nextReg.getTextLines().get(0);

            TextBlock currBlock = currLine.getTextBlocks().get(currLine.getTextBlocks().size() - 1);
            TextBlock nextBlock = nextLine.getTextBlocks().get(0);

            if (currReg.getTextLines().size() > 1)
            {
                TextLine currRegPrevLine = currReg.getTextLines().get(currReg.getTextLines().size() - 2);
                if (Math.abs(currRegPrevLine.getBottom() - currLine.getTop() -
                             (currLine.getBottom() - nextLine.getTop())) < 15)
                {
                    nextBox.getTableRegions().forEach(currBox::add);
                    tableBoxes.remove(i + 1);
                    i--;
                    continue;
                }
            }
            if (new HeightBiHeuristic().test(currBlock, nextBlock))
            {
                nextBox.getTableRegions().forEach(currBox::add);
                tableBoxes.remove(i + 1);
                i--;
            }
        }
    }

    private int gcorr(Rectangle gap, TableRegion region)
    {
        for (Rectangle regionGap : region.getGaps())
        {
            if (TableRegionDetector.wp(gap, regionGap) > minProjectionIntersection) return 1;
        }
        return 0;
    }

    private int tcorr(TableRegion region1, TableRegion region2)
    {
        if (region1.getLeft() > region2.getLeft())
        {
            region1.getGaps().get(0).setLeft(region2.getGaps().get(0).getLeft());
            region1.setLeft(region2.getLeft());
        }
        else if (region1.getLeft() < region2.getLeft())
        {
            region2.getGaps().get(0).setLeft(region1.getGaps().get(0).getLeft());
            region2.setLeft(region1.getLeft());
        }

        int result = 0;
        for (Rectangle firstRegionGap : region1.getGaps())
        {
            result += gcorr(firstRegionGap, region2);
        }
        return result;
    }
}
