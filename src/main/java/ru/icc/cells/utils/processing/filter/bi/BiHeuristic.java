package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.utils.processing.filter.Heuristic;

import java.lang.reflect.ParameterizedType;

public abstract class BiHeuristic<T extends Rectangle> implements Heuristic {
    private final Orientation orientation;

    private final Class<T> targetClass;

    public BiHeuristic(Orientation orientation) {
        this.orientation = orientation;
        this.targetClass =
                ((Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public abstract boolean test(T first, T second);

    public Class<T> getTargetClass() {
        return targetClass;
    }
}
