package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.utils.processing.filter.Heuristic;

public abstract class BiHeuristic<T extends Rectangle> implements Heuristic {
    private final Orientation orientation;

    public BiHeuristic(Orientation orientation) {
        this.orientation = orientation;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public abstract boolean test(T first, T second);
}
