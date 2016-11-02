package ru.icc.cells.recognizers;

import ru.icc.cells.common.Rectangle;

public interface Recognizer<T, K extends Rectangle>
{

    K recognize(T from);

}
