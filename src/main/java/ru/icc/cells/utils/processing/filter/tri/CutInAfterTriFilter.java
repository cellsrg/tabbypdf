package ru.icc.cells.utils.processing.filter.tri;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.utils.processing.filter.bi.HorizontalPositionBiFilter;

public class CutInAfterTriFilter extends TriFilter<Rectangle> {
    public CutInAfterTriFilter() {
        super(Orientation.VERTICAL, TriFilterType.AFTER);
    }

    @Override
    public boolean filter(Rectangle first, Rectangle second, Rectangle third) {
        return !(new HorizontalPositionBiFilter().filter(second, third) && first.getRight() >= third.getLeft());
    }
}
