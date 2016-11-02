package ru.icc.cells.detectors;

public class TableDetectorConfiguration
{
    int     minTextLineGapProjectionIntersection = 0;
    float   minWhitespaceWidth                   = 0.1f;
    int     maxNonTableLinesBetweenRegions       = 2;
    int     minRegionGapProjectionIntersection   = 0;
    double  gapThreshold                         = 0.8;
    boolean useSortedTextBlocks                  = true;

    /**
     * Default value is 0
     */
    public TableDetectorConfiguration setMinTextLineGapProjectionIntersection(int minTextLineGapProjectionIntersection)
    {
        this.minTextLineGapProjectionIntersection = minTextLineGapProjectionIntersection;
        return this;
    }

    /**
     * Default value is 0.1f
     */
    public TableDetectorConfiguration setMinWhitespaceWidth(float minWhitespaceWidth)
    {
        this.minWhitespaceWidth = minWhitespaceWidth;
        return this;
    }

    /**
     * Default value is 2
     */
    public TableDetectorConfiguration setMaxNonTableLinesBetweenRegions(int maxNonTableLinesBetweenRegions)
    {
        this.maxNonTableLinesBetweenRegions = maxNonTableLinesBetweenRegions;
        return this;
    }

    /**
     * Default value is 0
     */
    public TableDetectorConfiguration setMinRegionGapProjectionIntersection(int minRegionGapProjectionIntersection)
    {
        this.minRegionGapProjectionIntersection = minRegionGapProjectionIntersection;
        return this;
    }

    /**
     * Default value is 0.8
     */
    public TableDetectorConfiguration setGapThreshold(double gapThreshold)
    {
        this.gapThreshold = gapThreshold;
        return this;
    }

    /**
     * Default value is true
     */
    public TableDetectorConfiguration setUseSortedTextBlocks(boolean useSortedTextBlocks) {
        this.useSortedTextBlocks = useSortedTextBlocks;
        return this;
    }
}
