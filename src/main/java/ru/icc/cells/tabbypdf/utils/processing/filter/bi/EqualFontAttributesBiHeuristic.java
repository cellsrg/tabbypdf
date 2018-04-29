package ru.icc.cells.tabbypdf.utils.processing.filter.bi;

import ru.icc.cells.tabbypdf.entities.TextBlock;
import ru.icc.cells.tabbypdf.entities.TextChunk;

public class EqualFontAttributesBiHeuristic extends BiHeuristic<TextBlock> {
    public EqualFontAttributesBiHeuristic() {
        super(Orientation.BOTH);
    }

    public EqualFontAttributesBiHeuristic(Orientation orientation) {
        super(orientation);
    }

    @Override
    public boolean test(TextBlock first, TextBlock second) {
        return first.getChunks().stream()
            .map(TextChunk::getFontCharacteristics)
            .anyMatch(firstFont ->
                second.getChunks().stream()
                    .map(TextChunk::getFontCharacteristics)
                    .anyMatch(firstFont::equals)
            );
    }
}
