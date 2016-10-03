package ru.icc.cells.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Андрей on 03.10.2016.
 */
public class TableRegion extends Rectangle {
    private List<TextLine> textLines = new ArrayList<>();

    public TableRegion() {
        super();
    }

    public List<TextLine> getTextLines() {
        return textLines;
    }

    public void add(TextLine textLine) {
        join(textLine);
        textLines.add(textLine);
    }

    @Override
    protected <T extends Rectangle> void join(T other) {
        if (textLines.isEmpty()) {
            setLeft(other.getLeft());
            setBottom(other.getBottom());
            setRight(other.getRight());
            setTop(other.getTop());
        } else {
            super.join(other);
        }
    }

    public String getText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TextLine textLine : textLines) {
            stringBuilder.append(textLine.getText());
        }
        return stringBuilder.toString();
    }
}
