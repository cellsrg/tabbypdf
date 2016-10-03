package ru.icc.cells.detectors;

import ru.icc.cells.common.Rectangle;

import java.util.List;

/**
 * Created by sunveil on 23/06/16.
 */
public interface Detector<T> {
    List<T> detect(List<? extends Rectangle> rectangles);
}
