package ru.icc.cells.detectors;

public class TableDetectorConfiguration {
    int    minTextLineGapProjectionIntersection = 0;
    float  minWhitespaceWidth                   = 0.1f;
    int    maxNonTableLinesBetweenRegions       = 2;
    int    minRegionGapProjectionIntersection   = 0;
    double gapThreshold                         = 0.8;

    public TableDetectorConfiguration setMinTextLineGapProjectionIntersection(
            int minTextLineGapProjectionIntersection) {
        this.minTextLineGapProjectionIntersection = minTextLineGapProjectionIntersection;
        return this;
    }

    public TableDetectorConfiguration setMinWhitespaceWidth(float minWhitespaceWidth) {
        this.minWhitespaceWidth = minWhitespaceWidth;
        return this;
    }

    public TableDetectorConfiguration setMaxNonTableLinesBetweenRegions(int maxNonTableLinesBetweenRegions) {
        this.maxNonTableLinesBetweenRegions = maxNonTableLinesBetweenRegions;
        return this;
    }

    public TableDetectorConfiguration setMinRegionGapProjectionIntersection(int minRegionGapProjectionIntersection) {
        this.minRegionGapProjectionIntersection = minRegionGapProjectionIntersection;
        return this;
    }

    public TableDetectorConfiguration setGapThreshold(double gapThreshold) {
        this.gapThreshold = gapThreshold;
        return this;
    }
}
