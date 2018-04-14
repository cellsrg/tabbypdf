package ru.icc.cells.tabbypdf.common;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic container from text chunks. Contains chunks that are in the same table cell
 */
@Getter
@NoArgsConstructor
public class TextBlock extends RectangularTextContainer {

    private List<TextChunk> chunks = new ArrayList<>();

    /**
     * Add a chunk to this text block
     *
     * @param textChunk
     */
    public void add(TextChunk textChunk) {
        join(textChunk);
        chunks.add(textChunk);
    }

    /**
     * Join other text block. Result stores in this text block
     *
     * @param textBlock
     */
    public void add(TextBlock textBlock) {
        join(textBlock);
        chunks.addAll(textBlock.chunks);
    }

    @Override
    protected <T extends Rectangle> void join(T other) {
        if (isEmpty()) {
            setLeft(other.getLeft());
            setBottom(other.getBottom());
            setRight(other.getRight());
            setTop(other.getTop());
        } else {
            super.join(other);
        }
    }

    /**
     * @return is that text block contains any chunks
     */
    public boolean isEmpty() {
        return chunks.isEmpty();
    }

    @Override
    public String getText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TextChunk chunk : chunks) {
            stringBuilder.append(chunk.getText());
        }
        return stringBuilder.toString();
    }
}
