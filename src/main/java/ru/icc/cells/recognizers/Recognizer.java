package ru.icc.cells.recognizers;

import ru.icc.cells.common.Rectangle;

import java.util.List;

public interface Recognizer<T extends Rectangle, K extends Rectangle> {

    K recognize(List<T> from);

}
