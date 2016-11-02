package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.Ruling;

import java.util.List;

public class LinesBetweenChunksBiHeuristic extends BiHeuristic<Rectangle>
{
    private List<Ruling> lines;

    public LinesBetweenChunksBiHeuristic(List<Ruling> lines)
    {
        super(Orientation.BOTH);
        this.lines = lines;
    }

    public LinesBetweenChunksBiHeuristic(Orientation orientation)
    {
        super(orientation);
    }

    @Override
    public boolean test(Rectangle first, Rectangle second)
    {
        return lines
                       .stream()
                       .filter(line ->
                               {
                                   float   rx1 = first.getRight();
                                   float   lx2 = second.getLeft();
                                   float   lx1 = first.getLeft();
                                   float   rx2 = second.getRight();
                                   float   ty1 = first.getTop();
                                   float   ty2 = second.getTop();
                                   float   by1 = first.getBottom();
                                   float   by2 = second.getBottom();
                                   double  lx  = Double.min(line.getStartLocation().getX(),
                                                            line.getEndLocation().getX());
                                   double  rx  = Double.max(line.getStartLocation().getX(),
                                                            line.getEndLocation().getX());
                                   double  by  = Double.min(line.getStartLocation().getY(),
                                                            line.getEndLocation().getY());
                                   double  ty  = Double.max(line.getStartLocation().getY(),
                                                            line.getEndLocation().getY());
                                   boolean isBetweenChunks;
                                   isBetweenChunks = lx >= rx1 && lx <= lx2 &&
                                                     ty >= Float.max(by1, by2) &&
                                                     by >= Float.min(ty1, ty2);
                                   isBetweenChunks =
                                           isBetweenChunks || ty <= by1 && ty >= ty2 &&
                                                              lx <= Float.max(rx1, rx2) &&
                                                              rx >= Float.min(lx1, lx2);
                                   return isBetweenChunks;
                               })
                       .count() == 0;
    }
}
