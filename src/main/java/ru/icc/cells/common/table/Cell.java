package ru.icc.cells.common.table;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.RectangularTextContainer;
import ru.icc.cells.common.TextBlock;

import java.util.List;

public class Cell extends RectangularTextContainer
{
    private int id;
    private int rowHeight, columnWidth;
    private final List<TextBlock> content;

    public Cell(int id,
                float left,  float bottom,
                float right, float top,
                int rowHeight, int columnWidth,
                List<TextBlock> content)
    {
        super(left, bottom, right, top);
        this.id = id;
        this.rowHeight = rowHeight;
        this.columnWidth = columnWidth;
        this.content = content;
    }

    public String getText()
    {
        return content
                .stream()
                .map(TextBlock::getText)
                .reduce(String::concat)
                .orElse("").trim();
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setRowHeight(int rowHeight)
    {
        this.rowHeight = rowHeight;
    }

    public void setColumnWidth(int columnWidth)
    {
        this.columnWidth = columnWidth;
    }

    public int getId()
    {
        return id;
    }

    public int getRowHeight()
    {
        return rowHeight;
    }

    public int getColumnWidth()
    {
        return columnWidth;
    }

    @Override
    public void join(Rectangle other)
    {
        if (other != null)
        {
            super.join(other);
            if (other instanceof Cell)
            {
                this.columnWidth += ((Cell) other).columnWidth;
                this.content.addAll(((Cell) other).content);
                this.id = Math.min(this.id, ((Cell) other).id);
            }
        }
    }

    @Override
    public String toString()
    {
        return "Cell{" + "id=" + id + ", " +
                         "rowHeight=" + rowHeight + ", columnWidth=" + columnWidth + ", " +
                         "content=\"" + getText() + "\"}" + System.lineSeparator();
    }
}
