package ru.icc.cells.tabbypdf.utils.processing.filter.tri;

import lombok.Getter;
import ru.icc.cells.tabbypdf.common.Rectangle;
import ru.icc.cells.tabbypdf.utils.processing.filter.Heuristic;

@Getter
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

    public abstract boolean test(T first, T second, T third);
}
