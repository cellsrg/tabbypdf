package ru.icc.cells.debug.visual;

import ru.icc.cells.common.Orderable;
import ru.icc.cells.common.Ruling;

import java.awt.*;

/**
 * Created by Андрей on 22.09.2016.
 */
public interface PdfWriter {
    void setColor(Color color);

    <T extends ru.icc.cells.common.Rectangle & Orderable> void drawChunk(T chunk);

    void drawRuling(Ruling ruling);
}
