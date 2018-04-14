package ru.icc.cells.tabbypdf.common.table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.icc.cells.tabbypdf.common.Rectangle;
import ru.icc.cells.tabbypdf.common.RectangularTextContainer;
import ru.icc.cells.tabbypdf.common.TextBlock;

import java.util.List;

@Getter
@Setter
@ToString
@Accessors
public class Cell extends RectangularTextContainer {
    private int id;
    private int rowHeight;
    private int columnWidth;
    private final List<TextBlock> content;

    public Cell(int id, double left, double bottom, double right, double top, int rowHeight, int columnWidth,
                List<TextBlock> content
    ) {
        super(left, bottom, right, top);
        this.id = id;
        this.rowHeight = rowHeight;
        this.columnWidth = columnWidth;
        this.content = content;
    }

    @Override
    public String getText() {
        return content
            .stream()
            .map(TextBlock::getText)
            .reduce(String::concat)
            .orElse("").trim();
    }

    @Override
    public void join(Rectangle other) {
        if (other != null) {
            super.join(other);
            if (other instanceof Cell) {
                this.columnWidth += ((Cell) other).columnWidth;
                this.content.addAll(((Cell) other).content);
                this.id = Math.min(this.id, ((Cell) other).id);
            }
        }
    }
}
