package ru.icc.cells.common;

import java.util.List;

/**
 * Created by Андрей on 22.09.2016.
 */
public class Page extends Rectangle implements Orderable{
    private final List<TextChunk> chunks;
    private final List<Ruling>    rulings;
    private int order;

    public Page(List<TextChunk> chunks, List<Ruling> rulings, int pageNumber) {
        this.chunks = chunks;
        this.rulings = rulings;
    }

    public List<TextChunk> getChunks() {
        return chunks;
    }

    public List<Ruling> getRulings() {
        return rulings;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
