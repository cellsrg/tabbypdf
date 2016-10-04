package ru.icc.cells.common;

import java.util.List;

/**
 * Page object contains raw data from PDF page.
 */
public class Page extends Rectangle {
    private final List<TextChunk> originChunks;
    private final List<TextChunk> characterChunks;
    private final List<TextChunk> wordChunks;
    private final List<Ruling>    rulings;

    public Page(List<TextChunk> originChunks, List<TextChunk> characterChunks, List<TextChunk> wordChunks,
                List<Ruling> rulings) {
        this.originChunks = originChunks;
        this.characterChunks = characterChunks;
        this.wordChunks = wordChunks;
        this.rulings = rulings;
    }

    /**
     * Chunks represented by PDF content stream instructions like 'TJ' etc...
     */
    public List<TextChunk> getOriginChunks() {
        return originChunks;
    }

    /**
     * Rulings represented by PDF content stream instructions like 'LINETO' etc...
     */
    public List<Ruling> getRulings() {
        return rulings;
    }

    /**
     * Chunks compiled from each character render info
     */
    public List<TextChunk> getCharacterChunks() {
        return characterChunks;
    }

    /**
     * Chunks combined into words from character chunks
     */
    public List<TextChunk> getWordChunks() {
        return wordChunks;
    }
}
