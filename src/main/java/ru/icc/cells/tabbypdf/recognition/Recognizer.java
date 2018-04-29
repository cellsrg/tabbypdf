package ru.icc.cells.tabbypdf.recognition;

import ru.icc.cells.tabbypdf.entities.Rectangle;

public interface Recognizer<T, K extends Rectangle> {
    K recognize(T from);
}
