package ru.icc.cells.common;

import com.itextpdf.text.pdf.parser.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunveil on 27/06/16.
 */
public class TextBlock extends Rectangle implements Orderable {

    private int order;

    public TextBlock() {
        setStartLocation(new Vector(0, 0, 0));
        setEndLocation(new Vector(0, 0, 0));
        setRightTopPoint(new Vector(0, 0, 0));
    }

    private List<TextChunk> chunks = new ArrayList<>();

    public List<TextChunk> getChunks() {
        return chunks;
    }

    public void add(TextChunk textChunk) {
        join(textChunk);
        chunks.add(textChunk);
    }

    public void add(TextBlock textBlock) {
        join(textBlock);
        chunks.addAll(textBlock.chunks);
    }

    @Override
    protected <T extends Rectangle> void join(T other) {
        if (isEmpty()) {
            setStartLocation(other.startLocation);
            setEndLocation(other.endLocation);
            setRightTopPoint(other.rightTopPoint);
        } else {
            super.join(other);
        }
    }

    public boolean isEmpty() {
        return chunks.isEmpty();
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public String getText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TextChunk chunk : chunks) {
            stringBuilder.append(chunk.getText());
        }
        return stringBuilder.toString();
    }
}
