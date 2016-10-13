package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.TextChunk;

public class EqualFontSizeBiHeuristic extends BiHeuristic<TextChunk> {
    public EqualFontSizeBiHeuristic() {
        super(Orientation.BOTH);
    }

    public EqualFontSizeBiHeuristic(Orientation orientation) {
        super(orientation);
    }

    @Override
    public boolean test(TextChunk first, TextChunk second) {
        return first.getFontSize() == second.getFontSize();
    }
}
