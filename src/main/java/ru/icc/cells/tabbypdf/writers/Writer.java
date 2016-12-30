package ru.icc.cells.tabbypdf.writers;

import ru.icc.cells.tabbypdf.common.Rectangle;

import java.util.List;

/**
 * @author aaltaev
 */
public interface Writer<T extends Rectangle,R> {
    public R write(List<T> tables) throws Exception;
}
