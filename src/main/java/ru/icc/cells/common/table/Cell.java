package ru.icc.cells.common.table;

import ru.icc.cells.common.RectangularTextContainer;
import ru.icc.cells.common.TextBlock;

import java.util.List;

public class Cell extends RectangularTextContainer {
    private final int id;
    private final int rowHeight, columnWidth;
    private final List<TextBlock> content;

    public Cell(int id, float left, float bottom, float right, float top, int rowHeight, int columnWidth,
                List<TextBlock> content) {
        super(left, bottom, right, top);
        this.id = id;
        this.rowHeight = rowHeight;
        this.columnWidth = columnWidth;
        this.content = content;
    }

    public String getText() {
        return content.stream().map(TextBlock::getText).reduce(String::concat).orElse("");
    }

    public int getId() {
        return id;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public int getColumnWidth() {
        return columnWidth;
    }
}
