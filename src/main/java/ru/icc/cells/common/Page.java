package ru.icc.cells.common;

import java.util.List;

/**
 * Created by Андрей on 22.09.2016.
 */
public class Page {
    private final List<TextChunk> chunks;
    private final List<Ruling> rulings;
    private final int pageNumber;

    public Page(List<TextChunk> chunks, List<Ruling> rulings, int pageNumber) {
        this.chunks = chunks;
        this.rulings = rulings;
        this.pageNumber = pageNumber;
    }

    public List<TextChunk> getChunks() {
        return chunks;
    }

    public List<Ruling> getRulings() {
        return rulings;
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
