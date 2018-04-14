package ru.icc.cells.tabbypdf.utils.processing.filter.bi;

import lombok.Getter;
import ru.icc.cells.tabbypdf.common.Rectangle;
import ru.icc.cells.tabbypdf.utils.processing.filter.Heuristic;

import java.lang.reflect.ParameterizedType;

@Getter
public abstract class BiHeuristic<T extends Rectangle> implements Heuristic {
    private final Orientation orientation;
    private final Class<T>    targetClass;

    public BiHeuristic(Orientation orientation) {
        this.orientation = orientation;
        this.targetClass =
            ((Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    public abstract boolean test(T first, T second);
}
