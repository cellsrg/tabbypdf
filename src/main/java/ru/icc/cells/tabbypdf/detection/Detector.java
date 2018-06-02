package ru.icc.cells.tabbypdf.detection;

import ru.icc.cells.tabbypdf.entities.Rectangle;

import java.util.List;

/**
 * Detector interface for the needs of table detection
 *
 * @param <T> the type of rectangular objects to be detected
 * @param <K> the type of rectangular objects which will be used for detection
 */
public interface Detector<T extends Rectangle, K extends Rectangle> {
    /**
     * Perform detection
     *
     * @param rectangles
     * @return list of detected rectangular objects
     */
    List<T> detect(List<K> rectangles);
}
