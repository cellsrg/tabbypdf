package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.utils.processing.filter.ChunkFilter;

public abstract class BiFilter<T extends Rectangle> implements ChunkFilter {
    private final Orientation orientation;

    public BiFilter(Orientation orientation) {
        this.orientation = orientation;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public abstract boolean filter(T first, T second);
}
