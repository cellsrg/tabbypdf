package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.Rectangle;

public class VerticalPositionBiFilter extends BiFilter<Rectangle> {
    public VerticalPositionBiFilter() {
        super(Orientation.VERTICAL);
    }

    @Override
    public boolean filter(Rectangle first, Rectangle second) {
        float lx1 = first.getLeft();
        float rx1 = first.getRight();
        float lx2 = second.getLeft();
        float rx2 = second.getRight();
        float ty1 = first.getTop();
        float ty2 = second.getTop();
        float by1 = first.getBottom();
        return (rx1 >= lx2) && (lx1 <= rx2) && (ty1 >= ty2);
    }
}
