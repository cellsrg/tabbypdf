package ru.icc.cells.common;

import java.util.List;

/**
 * Created by Андрей on 22.09.2016.
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

    public List<TextChunk> getOriginChunks() {
        return originChunks;
    }

    public List<Ruling> getRulings() {
        return rulings;
    }

    public List<TextChunk> getCharacterChunks() {
        return characterChunks;
    }

    public List<TextChunk> getWordChunks() {
        return wordChunks;
    }
}
