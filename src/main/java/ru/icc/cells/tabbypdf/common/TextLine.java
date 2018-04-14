package ru.icc.cells.tabbypdf.common;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for text blocks placed on same horizontal line
 */
@Getter
@NoArgsConstructor
public class TextLine extends RectangularTextContainer {
    private List<TextBlock> textBlocks = new ArrayList<>();
    private List<Rectangle> gaps       = new ArrayList<>();

    /**
     * Add text block to this text line
     */
    public void add(TextBlock textBlock) {
        join(textBlock);
        textBlocks.add(textBlock);
    }

    /**
     * Add whitespace areas to this text line
     */
    public void addGaps(List<Rectangle> gaps) {
        this.gaps.addAll(gaps);
        Double left   = gaps.stream().map(Rectangle::getLeft  ).min(Double::compareTo).orElse(0.0);
        Double right  = gaps.stream().map(Rectangle::getRight ).max(Double::compareTo).orElse(0.0);
        Double top    = gaps.stream().map(Rectangle::getTop   ).max(Double::compareTo).orElse(0.0);
        Double bottom = gaps.stream().map(Rectangle::getBottom).min(Double::compareTo).orElse(0.0);

        super.join(new Rectangle(left, bottom, right, top));
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

    @Override
    public String getText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TextBlock textBlock : textBlocks) {
            stringBuilder.append(textBlock.getText());
        }
        return stringBuilder.toString();
    }
}
