package ru.icc.cells.tabbypdf.debug.visual;

import ru.icc.cells.tabbypdf.common.Ruling;
import ru.icc.cells.tabbypdf.common.Rectangle;

import java.awt.*;

/**
 * Interface for pdf writer
 */
public interface PdfWriter
{
    /**
     * Set stroking color
     */
    void setColor(Color color);

    /**
     * Print text at (x,y)
     */
    void printText(String text, float x, float y);

    /**
     * Draw rectangle
     */
    <T extends ru.icc.cells.tabbypdf.common.Rectangle> void drawRect(T rect);

    <T extends Rectangle> void drawRects(java.util.List<T> rects);

    /**
     * Draw ruling
     * @param ruling
     */
    void drawRuling(Ruling ruling);
}
