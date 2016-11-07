package ru.icc.cells.tabbypdf.recognizers;

import ru.icc.cells.tabbypdf.common.Rectangle;

public interface Recognizer<T, K extends Rectangle>
{

    K recognize(T from);

}
