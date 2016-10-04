package ru.icc.cells.debug.visual;

import ru.icc.cells.common.Ruling;

import java.awt.*;

/**
 * Interface for pdf writer
 */
public interface PdfWriter {
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
    <T extends ru.icc.cells.common.Rectangle> void drawRect(T rect);

    /**
     * Draw ruling
     * @param ruling
     */
    void drawRuling(Ruling ruling);
}
