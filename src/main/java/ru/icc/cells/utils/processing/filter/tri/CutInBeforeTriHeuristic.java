package ru.icc.cells.utils.processing.filter.tri;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.utils.processing.filter.bi.HorizontalPositionBiHeuristic;

public class CutInBeforeTriHeuristic extends TriHeuristic<Rectangle> {

    public CutInBeforeTriHeuristic() {
        super(Orientation.VERTICAL, TriHeuristicType.BEFORE);
    }

    @Override
    public boolean test(Rectangle first, Rectangle second, Rectangle third) {
        HorizontalPositionBiHeuristic horizontalPositionBiHeuristic = new HorizontalPositionBiHeuristic();
        return !(first.getLeft() <= second.getRight() && first.getRight() >= third.getLeft() &&
                 horizontalPositionBiHeuristic.test(second, third));
    }
}
