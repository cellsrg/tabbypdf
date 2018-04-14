package ru.icc.cells.tabbypdf.utils.processing.filter.bi;

import ru.icc.cells.tabbypdf.common.Rectangle;

public class VerticalPositionBiHeuristic extends BiHeuristic<Rectangle> {
    public VerticalPositionBiHeuristic() {
        super(Orientation.VERTICAL);
    }

    @Override
    public boolean test(Rectangle first, Rectangle second) {
        double lx1 = first.getLeft();
        double rx1 = first.getRight();
        double lx2 = second.getLeft();
        double rx2 = second.getRight();
        double ty1 = first.getTop();
        double ty2 = second.getTop();
        return (rx1 >= lx2) && (lx1 <= rx2) && (ty1 >= ty2);
    }
}
