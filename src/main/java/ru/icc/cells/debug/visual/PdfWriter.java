package ru.icc.cells.debug.visual;

import ru.icc.cells.common.Ruling;

import java.awt.*;

/**
 * Created by Андрей on 22.09.2016.
 */
public interface PdfWriter {
    void setColor(Color color);

    void printText(String text, float x, float y);

    <T extends ru.icc.cells.common.Rectangle> void drawRect(T rect);

    void drawRuling(Ruling ruling);
}
