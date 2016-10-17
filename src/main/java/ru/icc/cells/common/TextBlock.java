package ru.icc.cells.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic container from text chunks. Contains chunks that are in the same table cell
 */
public class TextBlock extends Rectangle implements TextContainer {

    public TextBlock() {
        super();
    }

    private List<TextChunk> chunks = new ArrayList<>();

    public List<TextChunk> getChunks() {
        return chunks;
    }

    /**
     * Add a chunk to this text block
     * @param textChunk
     */
    public void add(TextChunk textChunk) {
        join(textChunk);
        chunks.add(textChunk);
    }

    /**
     * Join other text block. Result stores in this text block
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
