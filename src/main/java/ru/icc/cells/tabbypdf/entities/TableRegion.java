package ru.icc.cells.tabbypdf.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains text lines belongs to same table
 */
@Getter
@NoArgsConstructor
public class TableRegion extends RectangularTextContainer {
    private List<TextLine>  textLines = new ArrayList<>();
    private List<Rectangle> gaps      = new ArrayList<>();

    /**
     * Adds text line to this table region
     */
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

    @Override
    public String getText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TextLine textLine : textLines) {
            stringBuilder.append(textLine.getText());
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }
}
