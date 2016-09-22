package ru.icc.cells.debug.visual;

import ru.icc.cells.common.Ruling;
import ru.icc.cells.common.TextChunk;

import java.awt.*;

/**
 * Created by Андрей on 22.09.2016.
 */
public interface PdfWriter {
    void setColor(Color color);

    void drawChunk(TextChunk chunk);

    void drawRuling(Ruling ruling);
}
