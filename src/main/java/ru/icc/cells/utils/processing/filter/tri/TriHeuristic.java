package ru.icc.cells.utils.processing.filter.tri;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.utils.processing.filter.Heuristic;

public abstract class TriHeuristic<T extends Rectangle> implements Heuristic {
    public enum TriHeuristicType {
        BEFORE, AFTER
    }

    private final TriHeuristicType heuristicType;
    private final Orientation      orientation;

    public TriHeuristic(Orientation orientation, TriHeuristicType heuristicType) {
        this.orientation = orientation;
        this.heuristicType = heuristicType;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public TriHeuristicType getHeuristicType() {
        return heuristicType;
    }

    public abstract boolean test(T first, T second, T third);
}
