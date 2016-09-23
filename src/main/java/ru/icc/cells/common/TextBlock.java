package ru.icc.cells.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunveil on 27/06/16.
 */
public class TextBlock extends Rectangle {
    private List<TextChunk> chunks = new ArrayList<>();

    public List<TextChunk> getChunks() {
        return chunks;
    }
}
