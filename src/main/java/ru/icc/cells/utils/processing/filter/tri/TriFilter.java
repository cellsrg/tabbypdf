package ru.icc.cells.utils.processing.filter.tri;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.utils.processing.filter.ChunkFilter;

public abstract class TriFilter<T extends Rectangle> implements ChunkFilter {
    public enum TriFilterType {
        BEFORE, AFTER
    }

    private final TriFilterType filterType;
    private final Orientation orientation;

    public TriFilter(Orientation orientation, TriFilterType filterType) {
        this.orientation = orientation;
        this.filterType = filterType;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public TriFilterType getFilterType() {
        return filterType;
    }

    public abstract boolean filter(T first, T second, T third);
}
