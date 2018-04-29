package ru.icc.cells.tabbypdf.utils.processing.filter.bi;

import ru.icc.cells.tabbypdf.entities.Rectangle;
import ru.icc.cells.tabbypdf.entities.Ruling;

import java.util.List;

public class LinesBetweenChunksBiHeuristic extends BiHeuristic<Rectangle> {
    private List<Ruling> lines;

    public LinesBetweenChunksBiHeuristic(List<Ruling> lines) {
        super(Orientation.BOTH);
        this.lines = lines;
    }

    public LinesBetweenChunksBiHeuristic(Orientation orientation) {
        super(orientation);
    }

    @Override
    public boolean test(Rectangle first, Rectangle second) {
        return lines
            .stream().noneMatch(line -> {
                double rx1 = first.getRight();
                double lx2 = second.getLeft();
                double lx1 = first.getLeft();
                double rx2 = second.getRight();
                double ty1 = first.getTop();
                double ty2 = second.getTop();
                double by1 = first.getBottom();
                double by2 = second.getBottom();
                double lx = Double.min(line.getStartLocation().getX(),
                    line.getEndLocation().getX());
                double rx = Double.max(line.getStartLocation().getX(),
                    line.getEndLocation().getX());
                double by = Double.min(line.getStartLocation().getY(),
                    line.getEndLocation().getY());
                double ty = Double.max(line.getStartLocation().getY(),
                    line.getEndLocation().getY());
                boolean isBetweenChunks;
                isBetweenChunks = lx >= rx1 && lx <= lx2 &&
                    ty >= Double.max(by1, by2) &&
                    by >= Double.min(ty1, ty2);
                isBetweenChunks =
                    isBetweenChunks || ty <= by1 && ty >= ty2 &&
                        lx <= Double.max(rx1, rx2) &&
                        rx >= Double.min(lx1, lx2);
                return isBetweenChunks;
            });
    }
}
