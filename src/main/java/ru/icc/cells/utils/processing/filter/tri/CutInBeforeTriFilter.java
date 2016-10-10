package ru.icc.cells.utils.processing.filter.tri;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.utils.processing.filter.bi.HorizontalPositionBiFilter;

public class CutInBeforeTriFilter extends TriFilter<Rectangle> {

    public CutInBeforeTriFilter() {
        super(Orientation.VERTICAL, TriFilterType.BEFORE);
    }

    @Override
    public boolean filter(Rectangle first, Rectangle second, Rectangle third) {
        return !(new HorizontalPositionBiFilter().filter(first, second) && first.getLeft() >= third.getLeft());
    }
}
