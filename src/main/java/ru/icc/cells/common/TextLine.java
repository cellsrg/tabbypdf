package ru.icc.cells.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Андрей on 03.10.2016.
 */
public class TextLine extends Rectangle {
    private List<TextBlock> textBlocks = new ArrayList<>();
    private List<Rectangle> gaps       = new ArrayList<>();

    public TextLine() {
        super();
    }

    public List<TextBlock> getTextBlocks() {
        return textBlocks;
    }

    public List<Rectangle> getGaps() {
        return gaps;
    }

    public void add(TextBlock textBlock) {
        join(textBlock);
        textBlocks.add(textBlock);
    }

    public void addGaps(List<Rectangle> gaps) {
        this.gaps.addAll(gaps);
        Float left   = gaps.stream().map(Rectangle::getLeft).min(Float::compareTo).orElse(0f);
        Float right  = gaps.stream().map(Rectangle::getRight).max(Float::compareTo).orElse(0f);
        Float top    = gaps.stream().map(Rectangle::getTop).max(Float::compareTo).orElse(0f);
        Float bottom = gaps.stream().map(Rectangle::getBottom).min(Float::compareTo).orElse(0f);
        Rectangle gapsBoundingRectangle = new Rectangle(left, bottom, right, top);
        super.join(gapsBoundingRectangle);
    }

    @Override
    protected <T extends Rectangle> void join(T other) {
        if (textBlocks.isEmpty()) {
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
        for (TextBlock textBlock : textBlocks) {
            stringBuilder.append(textBlock.getText());
        }
        return stringBuilder.toString();
    }
}
