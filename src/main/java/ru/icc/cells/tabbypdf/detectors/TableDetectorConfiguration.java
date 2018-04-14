package ru.icc.cells.tabbypdf.detectors;

import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(chain = true)
public class TableDetectorConfiguration {
    int     minTextLineGapProjectionIntersection = 0;
    double  minWhitespaceWidth                   = 0.1f;
    int     maxNonTableLinesBetweenRegions       = 2;
    int     minRegionGapProjectionIntersection   = 0;
    double  gapThreshold                         = 0.8;
    boolean useSortedTextBlocks                  = true;
    double  maxDistanceBetweenRegions            = 48;
}
