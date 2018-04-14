package ru.icc.cells.tabbypdf.utils.processing.filter.bi;

import ru.icc.cells.tabbypdf.common.TextBlock;
import ru.icc.cells.tabbypdf.common.TextChunk;

public class EqualFontSizeBiHeuristic extends BiHeuristic<TextBlock> {
    public EqualFontSizeBiHeuristic() {
        super(Orientation.BOTH);
    }

    public EqualFontSizeBiHeuristic(Orientation orientation) {
        super(orientation);
    }

    @Override
    public boolean test(TextBlock first, TextBlock second) {
        return first.getChunks().stream()
            .map(TextChunk::getFontSize)
            .anyMatch(fontSize -> second.getChunks().stream()
                .map(TextChunk::getFontSize)
                .anyMatch(fontSize::equals)
            );
    }
}
